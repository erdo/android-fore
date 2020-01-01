package foo.bar.example.foredb.feature.todoitems

import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.room.InvalidationTracker
import co.early.fore.adapters.DiffCalculator
import co.early.fore.adapters.DiffSpec
import co.early.fore.adapters.Diffable
import co.early.fore.core.WorkMode
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.ObservableImp
import co.early.fore.core.threading.AsyncBuilder
import co.early.fore.core.time.SystemTimeWrapper
import foo.bar.example.foredb.db.todoitems.TodoItemDatabase
import foo.bar.example.foredb.db.todoitems.TodoItemEntity
import foo.bar.example.foredb.db.todoitems.TodoItemEntity.TABLE_NAME
import foo.bar.example.foredb.feature.todoitems.TodoListModelKotlin.RefreshStatus.ADDITIONAL_REFRESH_WAITING
import foo.bar.example.foredb.feature.todoitems.TodoListModelKotlin.RefreshStatus.IDLE
import foo.bar.example.foredb.feature.todoitems.TodoListModelKotlin.RefreshStatus.REQUESTED
import foo.bar.example.foredb.feature.todoitems.TodoListModelKotlin.RefreshStatus.TAKEN_DB_LIST_SNAPSHOT
import java.util.ArrayList


/**
 * This model wraps the database and all access to the db should go through here. It's setup to
 * maintain an in-memory list that can drive a view adapter on the UI thread. (In this case we are
 * not using a cursor directly).
 *
 *
 * The only changes that are made to the in memory list are done via a refresh with the latest db
 * data so that nothing gets out of sync. i.e. any changes go directly to the database and come
 * back later on the UI thread as a result of a db refresh.
 *
 *
 * For performance reasons we avoid unnecessary db refreshes using refreshStatus to drop calls
 * where we can - this is only really necessary for extreme situations where you may be updating
 * large amounts of data continuously, but it's here for completeness none the less.
 *
 *
 * As we may be getting updates here from the network or other threads, we need to synchronize access
 * to the db via the dao objects for total robustness - again if we didn't bother synchronizing here
 * you would only see issues occasionally or in extreme situations, but we do it here for completeness
 * anyway.
 *
 *
 */
class TodoListModelKotlin(private val todoItemDatabase: TodoItemDatabase, private val logger: Logger,
                          private val systemTimeWrapper: SystemTimeWrapper, private val workMode: WorkMode) : ObservableImp(workMode), Diffable {


    //we use this to synchronize access to the dao
    private val dbMonitor = Any()

    //we don't use a cursor here, so we do maintain an in memory list of the entire db
    private val todoItems = ArrayList<TodoItem>()

    private var latestDiffSpec: DiffSpec? = createFullDiffSpec(systemTimeWrapper)

    // after about 1000 rows, DiffResult begins to get way too slow, so we forget
    // about animating changes to the list after that
    var maxSizeForDiffUtil = 1000
        set(maxSizeForDiffUtil) {
            field = maxSizeForDiffUtil
            notifyObservers()
        }


    /**
     * We are keeping this **showDone** flag here because we only have one window into the data.
     *
     *
     * If we wanted to have more than one window, we'd probably use a different strategy:
     *
     *
     * Lets say we had a "main" page where you can toggle between seeing all the todos, and only
     * the todos that aren't done yet AND we also had an "admin" page where you would see all
     * the todos, no matter what the showDone flag said.
     *
     *
     * In that case we would probably have two separate models for each of those views. The admin
     * page could be driven by this class (but with the showDone flag removed) and the main page
     * would be driven by a smaller model which got its list data from this class but filtered it
     * based on a showDone flag as follows:
     *
     *
     * **Old pre Build.VERSION_CODES.N version to filter for done items:**
     * `
     *  finalTodoItemsList.clear();
     *  for (TodoItemEntity todoItemEntity : allTodoItemsEntityList) {
     *      if (showDone || !todoItemEntity.isDone()) {
     *          finalTodoItemsList.add(new TodoItem(todoItemEntity));
     *      }
     *  }
     * `
     *
     * **Java streams version to filter for done items:**
     * `
     *  finalTodoItemsList = allTodoItemsEntityList.stream()
     *      .filter(todoItemEntity -> showDone || !todoItemEntity.isDone())
     *      .map(todoItemEntity -> new TodoItem(todoItemEntity))
     *      .collect(Collectors.toList());
     * `
     *
     * **Kotlin version to filter for done items:**
     * `
     *  finalTodoItemsList.clear()
     *  finalTodoItemsList.addAll(allTodoItemsEntityList
     *      .filter {todoItemEntity -> showDone || !todoItemEntity.isDone()}
     *      .map {todoItemEntity -> TodoItem(todoItemEntity))
     * `
     *
     * **RxJava version to filter for done items:**
     * `
     *  finalTodoItemsList.clear();
     *  Observable.fromIterable(allTodoItemsEntityList)//this line not necessary if you already have an observable
     *      .filter(todoItemEntity -> showDone || todoItemEntity.isDone())
     *      .map(todoItemEntity -> new TodoItem(todoItemEntity))
     *      .subscribe(todoItem -> finalTodoItemsList.add(todoItem));
     * `
     *
     * For our current purposes however, we can just relly on SQL doing the work for us
     * and this works fine
     */
    @Volatile //notifyObservers() will get called at the end of the db fetch
    var isShowDone = false
        set(showDone) {
            field = showDone
            fetchLatestFromDb()
        }


    // This helps performance, but it won't be necessary until you get to
    // updating your db multiple times a second with a db larger than a few thousand items,
    // if you want a super simple but slightly less performant implementation, just delete all
    // the references to RefreshStatus
    @Volatile
    private var refreshStatus = IDLE
    internal enum class RefreshStatus {
        IDLE,
        REQUESTED,
        TAKEN_DB_LIST_SNAPSHOT,
        ADDITIONAL_REFRESH_WAITING
    }

    init {
        //hook into the database invalidation tracker and forward the updates to our own observers
        todoItemDatabase.invalidationTracker.addObserver(object : InvalidationTracker.Observer(TABLE_NAME) {
            override fun onInvalidated(tables: Set<String>) {
                fetchLatestFromDb()
            }
        })
    }

    fun fetchLatestFromDb() {

        logger.i(LOG_TAG, "1 fetchLatestFromDb()")

        synchronized(refreshStatus) {


            when (refreshStatus) {
                RefreshStatus.IDLE -> refreshStatus = REQUESTED
                RefreshStatus.TAKEN_DB_LIST_SNAPSHOT -> {
                    //we are now committed and we need to leave this to finish before refreshing again
                    refreshStatus = ADDITIONAL_REFRESH_WAITING
                    //we can forget about this, it's already in hand
                    return
                }
                RefreshStatus.REQUESTED, RefreshStatus.ADDITIONAL_REFRESH_WAITING -> return
            }



            AsyncBuilder<List<TodoItem>, Pair<List<TodoItem>, DiffUtil.DiffResult>>(workMode)
                .doInBackground { oldList ->

                    logger.i(LOG_TAG, "2 asking for latest data")

                    synchronized(refreshStatus) {
                        if (refreshStatus == REQUESTED) {
                            refreshStatus = TAKEN_DB_LIST_SNAPSHOT
                        }
                    }


                    val newList = ArrayList<TodoItem>()
                    var dbList: List<TodoItemEntity>

                    synchronized(dbMonitor) {
                        //dbList = logEntryDatabase.logEntryDao().allLogs
                        dbList = todoItemDatabase.todoItemDao().getTodoItems(isShowDone)
                    }

                    for (todoItemEntity in dbList) {
                        newList.add(TodoItem(todoItemEntity))
                    }

                    logger.i(LOG_TAG, "3 old list size (" + oldList[0].size + ") new list size:(" + newList.size + ")")

                    // work out the differences in the lists
                    val diffResult: DiffUtil.DiffResult?
                    if (oldList[0].size < this.maxSizeForDiffUtil && newList.size < this.maxSizeForDiffUtil) {
                        diffResult = DiffCalculator<TodoItem>().createDiffResult(oldList[0], newList)
                    } else {
                        diffResult = null
                    }

                    //hop back to the UI thread to update the UI
                    Pair(newList, diffResult)
                }
                .onPostExecute { payload ->

                    logger.i(LOG_TAG, "4 updating in memory copy")

                    //we defer to whatever the db says here so that we don't get out of sync
                    todoItems.clear()
                    todoItems.addAll(payload.first!!)
                    latestDiffSpec = DiffSpec(payload.second, systemTimeWrapper)

                    //notify immediately so that the changes are picked up
                    notifyObservers()

                    var triggerNewRefresh = false
                    synchronized(refreshStatus) {
                        if (refreshStatus == ADDITIONAL_REFRESH_WAITING) {
                            triggerNewRefresh = true
                        }
                        refreshStatus = IDLE
                    }
                    if (triggerNewRefresh) {
                        fetchLatestFromDb()
                    }

                }
                .execute(todoItems)
        }
    }

    //common db operations

    fun add(todoItem: TodoItem) {

        logger.i(LOG_TAG, "add()")

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        AsyncBuilder<TodoItem, Long>(workMode)
            .doInBackground { todoItems ->
                synchronized(dbMonitor) {
                    todoItemDatabase.todoItemDao().insertTodoItem(todoItems[0].entity)
                }
            }
            .execute(todoItem)
    }

    fun remove(todoItem: TodoItem) {

        logger.i(LOG_TAG, "remove()")

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        AsyncBuilder<TodoItem, Int>(workMode)
            .doInBackground { todoItems ->
                synchronized(dbMonitor) {
                    todoItemDatabase.todoItemDao().deleteTodoItem(todoItems[0].entity)
                }
            }
            .execute(todoItem)
    }

    fun update(todoItem: TodoItem) {

        logger.i(LOG_TAG, "update()")

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        AsyncBuilder<TodoItemEntity, Int>(workMode)
            .doInBackground { todoItems ->
                synchronized(dbMonitor) {
                    todoItemDatabase.todoItemDao().updateTodoItem(todoItems[0])
                }
            }
            .execute(todoItem.entity)
    }

    fun clear() {

        logger.i(LOG_TAG, "clear()")

        //fire to the db and forget - the invalidation tracker will keep us informed of changes
        AsyncBuilder<Void, Int>(workMode)
            .doInBackground {
                synchronized(dbMonitor) {
                    todoItemDatabase.todoItemDao().clear()
                }
            }
            .execute(null as Void?)
    }


    // methods that let us drive a view adapter easily

    operator fun get(index: Int): TodoItem {
        checkIndex(index)
        return todoItems[index]
    }

    fun size(): Int {
        return todoItems.size
    }

    private fun checkIndex(index: Int) {
        if (todoItems.size == 0) {
            throw IndexOutOfBoundsException("todoItems has no items in it, can not get index:$index")
        } else if (index < 0 || index > todoItems.size - 1) {
            throw IndexOutOfBoundsException("todoItems index needs to be between 0 and " + (todoItems.size - 1) + " not:" + index)
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
    override fun getAndClearLatestDiffSpec(maxAgeMs: Long): DiffSpec {

        val latestDiffSpecAvailable = latestDiffSpec
        val fullDiffSpec = createFullDiffSpec(systemTimeWrapper)

        latestDiffSpec = fullDiffSpec

        return if (systemTimeWrapper.currentTimeMillis() - latestDiffSpecAvailable!!.timeStamp < maxAgeMs) {
            latestDiffSpecAvailable
        } else {
            fullDiffSpec
        }
    }

    private fun createFullDiffSpec(stw: SystemTimeWrapper): DiffSpec {
        return DiffSpec(null, stw)
    }

    companion object {
        val LOG_TAG = TodoListModelKotlin::class.java.simpleName
    }

}
