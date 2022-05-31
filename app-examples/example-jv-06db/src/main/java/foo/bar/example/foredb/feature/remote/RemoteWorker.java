package foo.bar.example.foredb.feature.remote;

import java.util.ArrayList;
import java.util.List;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallback;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.time.SystemTimeWrapper;
import co.early.fore.net.retrofit2.CallProcessorRetrofit2;
import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.api.todoitems.TodoItemPojo;
import foo.bar.example.foredb.api.todoitems.TodoItemService;
import foo.bar.example.foredb.feature.todoitems.TodoItem;
import foo.bar.example.foredb.feature.todoitems.TodoListModel;
import foo.bar.example.foredb.message.UserMessage;

/**
 * gets a list of todoitems from the network and adds them to the database
 */
public class RemoteWorker extends ObservableImp{

    public static final String LOG_TAG = RemoteWorker.class.getSimpleName();

    //notice how we use the TodoListModel, we don't go directly to the db layer
    private final TodoListModel todoListModel;
    private final TodoItemService service;
    private final CallProcessorRetrofit2<UserMessage> callProcessor;
    private final SystemTimeWrapper systemTimeWrapper;
    private final WorkMode workMode;
    private final Logger logger;

    private int connections;
    private static final String WEB = App.getInst().getString(R.string.todo_web);

    public RemoteWorker(TodoListModel todoListModel, TodoItemService service, CallProcessorRetrofit2<UserMessage> callProcessor,
                        SystemTimeWrapper systemTimeWrapper, Logger logger, WorkMode workMode) {
        super(workMode);
        this.todoListModel = Affirm.notNull(todoListModel);
        this.service = Affirm.notNull(service);
        this.callProcessor = Affirm.notNull(callProcessor);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        this.logger = Affirm.notNull(logger);
        this.workMode = Affirm.notNull(workMode);
    }

    public void fetchTodoItems(final SuccessCallback successCallback, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(LOG_TAG, "fetchTodoItems()");

        Affirm.notNull(successCallback);
        Affirm.notNull(failureCallbackWithPayload);

        if (connections>8){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }


        connections++;
        notifyObservers();

        callProcessor.processCall(service.getTodoItems("3s"), workMode,
                successResponse -> handleNetworkSuccess(successCallback, successResponse),
                failureMessage -> handleNetworkFailure(failureCallbackWithPayload, failureMessage));

    }

    private void handleNetworkSuccess(SuccessCallback successCallBack, List<TodoItemPojo> todoItemPojos){
        addTodoItemsToDatabase(todoItemPojos);
        successCallBack.success();
        complete();
    }

    private void handleNetworkFailure(FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload, UserMessage failureMessage){
        failureCallbackWithPayload.fail(failureMessage);
        complete();
    }

    private void addTodoItemsToDatabase(List<TodoItemPojo> todoItemPojos){

        List<TodoItem> todoItems = new ArrayList<>(todoItemPojos.size());

        for (TodoItemPojo todoItemPojo : todoItemPojos){
            todoItems.add(new TodoItem(systemTimeWrapper.currentTimeMillis(), WEB + connections + " " + todoItemPojo.label));
        }

        todoListModel.addMany(todoItems);
    }

    public boolean isBusy() {
        return connections>0;
    }

    public int getConnections() {
        return connections;
    }

    private void complete(){

        logger.i(LOG_TAG, "complete()");

        connections--;
        notifyObservers();
    }

}
