package foo.bar.example.foredb.feature.todoitems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.InvalidationTracker;
import co.early.fore.adapters.DiffCalculator;
import co.early.fore.adapters.DiffSpec;
import co.early.fore.adapters.Diffable;
import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.threading.AsyncBuilder;
import co.early.fore.core.time.SystemTimeWrapper;
import foo.bar.example.foredb.db.todoitems.TodoItemDatabase;
import foo.bar.example.foredb.db.todoitems.TodoItemEntity;

import static foo.bar.example.foredb.db.todoitems.TodoItemEntity.TABLE_NAME;
import static foo.bar.example.foredb.feature.todoitems.TodoListModel.RefreshStatus.ADDITIONAL_REFRESH_WAITING;
import static foo.bar.example.foredb.feature.todoitems.TodoListModel.RefreshStatus.IDLE;
import static foo.bar.example.foredb.feature.todoitems.TodoListModel.RefreshStatus.REQUESTED;
import static foo.bar.example.foredb.feature.todoitems.TodoListModel.RefreshStatus.TAKEN_DB_LIST_SNAPSHOT;


/**
 * This model wraps the database and all access to the db should go through here. It's setup to
 * maintain an in-memory list that can drive a view adapter on the UI thread. (In this case we are
 * not using a cursor directly).
 * <p>
 * The only changes that are made to the in memory list are done via a refresh with the latest db
 * data so that nothing gets out of sync. i.e. any changes go directly to the database and come
 * back later on the UI thread as a result of a db refresh.
 * <p>
 * For performance reasons we avoid unnecessary db refreshes using refreshStatus to drop calls
 * where we can - this is only really necessary for extreme situations where you may be updating
 * large amounts of data continuously, but it's here for completeness none the less.
 * <p>
 * As we may be getting updates here from the network or other threads, we need to synchronize access
 * to the db via the dao objects for total robustness - again if we didn't bother synchronizing here
 * you would only see issues occasionally or in extreme situations, but we do it here for completeness
 * anyway.
 * <p>
 */
public class TodoListModel extends ObservableImp implements Diffable {

    private static final String LOG_TAG = TodoListModel.class.getSimpleName();

    private final TodoItemDatabase todoItemDatabase;
    private final Logger logger;
    private final SystemTimeWrapper systemTimeWrapper;
    private final WorkMode workMode;

    //we use this to synchronize access to the dao
    private final Object dbMonitor = new Object();

    //we don't use a cursor here, so we do maintain an in memory list of the entire db
    private final List<TodoItem> todoItems = new ArrayList<>();

    // after about 1000 rows, DiffResult begins to get way too slow, so we forget
    // about animating changes to the list after that
    private DiffSpec latestDiffSpec;
    private int maxSizeForDiffUtil = 1000;

    private volatile int totalNumberOfTodos = 0;
    private volatile int totalNumberOfDoneTodos = 0;


    /**
     * We are keeping this <strong>showDone</strong> flag here because we only have one window into the data.
     * <p>
     * If we wanted to have more than one window, we'd probably use a different strategy:
     * <p>
     * Lets say we had a "main" page where you can toggle between seeing all the todos, and only
     * the todos that aren't done yet AND we also had an "admin" page where you would see all
     * the todos, no matter what the showDone flag said.
     * <p>
     * In that case we would probably have two separate models for each of those views. The admin
     * page could be driven by this class (but with the showDone flag removed) and the main page
     * would be driven by a smaller model which got its list data from this class but filtered it
     * based on a showDone flag as follows:
     * <p>
     * <strong>Old pre Build.VERSION_CODES.N version to filter for done items:</strong>
     * <code>
     *  finalTodoItemsList.clear();
     *  for (TodoItemEntity todoItemEntity : allTodoItemsEntityList) {
     *      if (showDone || !todoItemEntity.isDone()) {
     *          finalTodoItemsList.add(new TodoItem(todoItemEntity));
     *      }
     *  }
     * </code>
     * <p>
     * <strong>Java streams version to filter for done items:</strong>
     * <code>
     *  finalTodoItemsList = allTodoItemsEntityList.stream()
     *      .filter(todoItemEntity -> showDone || !todoItemEntity.isDone())
     *      .map(todoItemEntity -> new TodoItem(todoItemEntity))
     *      .collect(Collectors.toList());
     * </code>
     * <p>
     * <strong>Kotlin version to filter for done items:</strong>
     * <code>
     *  finalTodoItemsList.clear()
     *  finalTodoItemsList.addAll(allTodoItemsEntityList
     *      .filter {todoItemEntity -> showDone || !todoItemEntity.isDone()}
     *      .map {todoItemEntity -> TodoItem(todoItemEntity))
     * </code>
     * <p>
     * <strong>RxJava version to filter for done items:</strong>
     * <code>
     *  finalTodoItemsList.clear();
     *  Observable.fromIterable(allTodoItemsEntityList)//this line not necessary if you already have an observable
     *      .filter(todoItemEntity -> showDone || todoItemEntity.isDone())
     *      .map(todoItemEntity -> new TodoItem(todoItemEntity))
     *      .subscribe(todoItem -> finalTodoItemsList.add(todoItem));
     * </code>
     *
     *
     * For our current purposes however, we can just relly on SQL doing the work for us
     * and this works fine
     */
    private volatile boolean showDone = false;


    // This helps performance, but it won't be necessary until you get to
    // updating your db multiple times a second with a db larger than a few thousand items,
    // if you want a super simple but slightly less performant implementation, just delete all
    // the references to RefreshStatus
    enum RefreshStatus {
        IDLE,
        REQUESTED,
        TAKEN_DB_LIST_SNAPSHOT,
        ADDITIONAL_REFRESH_WAITING
    }
    private volatile RefreshStatus refreshStatus = IDLE;



    public TodoListModel(TodoItemDatabase todoItemDatabase, Logger logger, SystemTimeWrapper systemTimeWrapper, WorkMode workMode) {
        super(workMode);

        this.todoItemDatabase = Affirm.notNull(todoItemDatabase);
        this.logger = Affirm.notNull(logger);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        this.workMode = Affirm.notNull(workMode);

        latestDiffSpec = createFullDiffSpec(systemTimeWrapper);

        //hook into the database invalidation tracker and forward the updates to our own observers
        todoItemDatabase.getInvalidationTracker().addObserver(new InvalidationTracker.Observer(TABLE_NAME) {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                fetchLatestFromDb();
            }
        });
    }

    public void fetchLatestFromDb() {

        logger.i(LOG_TAG, "1 fetchLatestFromDb()");

        synchronized (refreshStatus) {


            switch (refreshStatus) {
                case IDLE:
                    refreshStatus = REQUESTED;
                    break;
                case TAKEN_DB_LIST_SNAPSHOT:
                    //we are now committed and we need to leave this to finish before refreshing again
                    refreshStatus = ADDITIONAL_REFRESH_WAITING;
                case REQUESTED:
                case ADDITIONAL_REFRESH_WAITING:
                    //we can forget about this, it's already in hand
                    return;
            }


            //noinspection unchecked
            new AsyncBuilder<List<TodoItem>, Pair<List<TodoItem>, DiffUtil.DiffResult>>(workMode)
                .doInBackground(oldList -> {

                    logger.i(LOG_TAG, "2 asking for latest data");

                    synchronized (refreshStatus) {
                        if (refreshStatus == REQUESTED) {
                            refreshStatus = TAKEN_DB_LIST_SNAPSHOT;
                        }
                    }

                    synchronized (dbMonitor) {
                        totalNumberOfTodos = todoItemDatabase.todoItemDao().getRowCount();
                        totalNumberOfDoneTodos = todoItemDatabase.todoItemDao().getDoneRowCount();
                    }

                    List<TodoItem> newList = new ArrayList<>();
                    List<TodoItemEntity> dbList;
                    synchronized (dbMonitor) {
                        if (showDone){ //i.e. show everything
                            dbList = todoItemDatabase.todoItemDao().getAllTodoItems();
                        }else { //i.e. only show todos which are yet to be done
                            dbList = todoItemDatabase.todoItemDao().getTodoItems(false);
                        }
                    }
                    for (TodoItemEntity todoItemEntity : dbList) {
                        newList.add(new TodoItem(todoItemEntity));
                    }

                    logger.i(LOG_TAG, "3 old list size (" + oldList[0].size() + ") new list size:(" + newList.size() + ")");

                    // work out the differences in the lists
                    DiffUtil.DiffResult diffResult;
                    if (oldList[0].size() < maxSizeForDiffUtil && newList.size() < maxSizeForDiffUtil) {
                        diffResult = new DiffCalculator<TodoItem>().createDiffResult(oldList[0], newList);
                    } else {
                        diffResult = null;
                    }

                    //hop back to the UI thread to update the UI
                    return new Pair<>(newList, diffResult);

                })
                .onPostExecute(payload -> {

                    logger.i(LOG_TAG, "4 updating in memory copy");

                    //we defer to whatever the db says here so that we don't get out of sync
                    todoItems.clear();
                    todoItems.addAll(payload.first);
                    latestDiffSpec = new DiffSpec(payload.second, systemTimeWrapper);

                    //notify immediately so that the changes are picked up
                    notifyObservers();

                    boolean triggerNewRefresh = false;
                    synchronized (refreshStatus) {
                        if (refreshStatus == ADDITIONAL_REFRESH_WAITING) {
                            triggerNewRefresh = true;
                        }
                        refreshStatus = IDLE;
                    }
                    if (triggerNewRefresh) {
                        fetchLatestFromDb();
                    }

                })
                .execute(todoItems);
        }
    }

    //common db operations

    public void add(TodoItem todoItem) {

        logger.i(LOG_TAG, "add()");

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<TodoItem, Long>(workMode)
                .doInBackground(todoItems -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().insertTodoItem(todoItems[0].getEntity());
                    }
                })
                .execute(todoItem);
    }

    public void addMany(List<TodoItem> todoItems) {

        logger.i(LOG_TAG, "addMany()");

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        //noinspection unchecked
        new AsyncBuilder<List<TodoItem>, Void>(workMode)
                .doInBackground(newTodoItems -> {

                    //see the docs above for alternative ways of doing this
                    List<TodoItemEntity> todoItemEntities = new ArrayList<>(newTodoItems.length);

                    for (TodoItem todoItem : newTodoItems[0]){
                        todoItemEntities.add(todoItem.getEntity());
                    }

                    synchronized (dbMonitor) {
                        todoItemDatabase.todoItemDao().insertManyTodoItems(todoItemEntities);
                    }
                    return null;
                })
                .execute(todoItems);
    }

    public void remove(TodoItem todoItem) {

        logger.i(LOG_TAG, "remove()");

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<TodoItem, Integer>(workMode)
                .doInBackground(todoItems -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().deleteTodoItem(todoItems[0].getEntity());
                    }
                })
                .execute(todoItem);
    }

    public void update(TodoItem todoItem) {

        logger.i(LOG_TAG, "update()");

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<TodoItemEntity, Integer>(workMode)
                .doInBackground(todoItems -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().updateTodoItem(todoItems[0]);
                    }
                })
                .execute(todoItem.getEntity());
    }

    public void clear() {

        logger.i(LOG_TAG, "clear()");

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<Void, Integer>(workMode)
                .doInBackground(voids -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().clear();
                    }
                })
                .execute((Void) null);
    }


    //cheat mode db operations

    public void addRandom(final int numberToAdd){

        logger.i(LOG_TAG, "addRandom():" + numberToAdd);

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<Integer, Integer>(workMode)
                .doInBackground(howMany -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().addRandom(howMany[0], systemTimeWrapper);
                    }
                })
                .execute(numberToAdd);
    }


    public void doRandom(final int percent){

        logger.i(LOG_TAG, "doRandom():" + percent);

        if (percent<1 || percent>100){
            throw new IllegalArgumentException("percent must be between 1 and 100, not:" + percent);
        }

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<Integer, Integer>(workMode)
                .doInBackground(howMany -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().doRandomXPercent(howMany[0], systemTimeWrapper);
                    }
                })
                .execute(percent);
    }


    public void deleteRandom(final int percent){

        logger.i(LOG_TAG, "deleteRandom():" + percent);

        if (percent<1 || percent>100){
            throw new IllegalArgumentException("percent must be between 1 and 100, not:" + percent);
        }

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        new AsyncBuilder<Integer, Integer>(workMode)
                .doInBackground(howMany -> {
                    synchronized (dbMonitor) {
                        return todoItemDatabase.todoItemDao().deleteRandomXPercent(howMany[0], systemTimeWrapper);
                    }
                })
                .execute(percent);
    }

    public void add(String label) {
        add(new TodoItem(systemTimeWrapper.currentTimeMillis(), label));
    }


    //other getters/setters for our model

    public int getMaxSizeForDiffUtil() {
        return maxSizeForDiffUtil;
    }

    public void setMaxSizeForDiffUtil(int maxSizeForDiffUtil) {
        this.maxSizeForDiffUtil = maxSizeForDiffUtil;
        notifyObservers();
    }

    public boolean isShowDone() {
        return showDone;
    }

    public void setShowDone(boolean showDone) {
        this.showDone = showDone;
        fetchLatestFromDb(); //notifyObservers() will get called at the end of the db fetch
    }

    public boolean isValidItemLabel(String label){
        return (label == null ? false : (label.length()>0));
    }


    // methods that let us drive a view adapter easily

    public TodoItem get(int index) {
        checkIndex(index);
        return todoItems.get(index);
    }

    public int size() {
        return todoItems.size();
    }

    public int getTotalNumberOfTodos(){
        return totalNumberOfTodos;
    }

    public int getTotalNumberOfDoneTodos(){
        return totalNumberOfDoneTodos;
    }

    public int getTotalNumberOfRemainingTodos(){
        return totalNumberOfTodos - totalNumberOfDoneTodos;
    }

    public void setDone(boolean done, int index) {
        TodoItem item = get(index);
        item.setDone(done);
        update(item);
    }

    private void checkIndex(int index) {
        if (todoItems.size() == 0) {
            throw new IndexOutOfBoundsException("todoItems has no items in it, can not get index:" + index);
        } else if (index < 0 || index > todoItems.size() - 1) {
            throw new IndexOutOfBoundsException("todoItems index needs to be between 0 and " + (todoItems.size() - 1) + " not:" + index);
        }
    }


    /**
     * If the DiffResult is old, then we assume that whatever changes
     * were made to the list last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the DiffResult and create a fresh one with a
     * full diff spec.
     *
     * @return the latest DiffResult for the list
     */
    @Override
    public DiffSpec getAndClearLatestDiffSpec(long maxAgeMs) {

        DiffSpec latestDiffSpecAvailable = latestDiffSpec;
        latestDiffSpec = createFullDiffSpec(systemTimeWrapper);

        if (systemTimeWrapper.currentTimeMillis() - latestDiffSpecAvailable.timeStamp < maxAgeMs) {
            return latestDiffSpecAvailable;
        } else {
            return latestDiffSpec;
        }
    }

    private DiffSpec createFullDiffSpec(SystemTimeWrapper stw) {
        return new DiffSpec(null, stw);
    }

}
