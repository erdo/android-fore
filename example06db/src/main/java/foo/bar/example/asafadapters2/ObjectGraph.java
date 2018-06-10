package foo.bar.example.asafadapters2;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.AndroidLogger;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.time.SystemTimeWrapper;
import co.early.asaf.retrofit.CallProcessor;
import co.early.asaf.retrofit.InterceptorLogging;
import foo.bar.example.asafadapters2.api.CustomGlobalErrorHandler;
import foo.bar.example.asafadapters2.api.CustomGlobalRequestInterceptor;
import foo.bar.example.asafadapters2.api.CustomRetrofitBuilder;
import foo.bar.example.asafadapters2.api.todoitems.TodoItemService;
import foo.bar.example.asafadapters2.db.todoitems.TodoItemDatabase;
import foo.bar.example.asafadapters2.feature.bossmode.BossMode;
import foo.bar.example.asafadapters2.feature.todoitems.TodoListModel;
import foo.bar.example.asafadapters2.feature.remote.RemoteWorker;
import foo.bar.example.asafadapters2.message.UserMessage;
import retrofit2.Retrofit;

import static co.early.asaf.core.Affirm.notNull;

/**
 * This is the price you pay for not using Dagger, the payback is not having to write modules
 */
class ObjectGraph {

    private volatile boolean initialized = false;
    private final Map<Class<?>, Object> dependencies = new HashMap<>();


    void setApplication(Application application) {
        setApplication(application, WorkMode.ASYNCHRONOUS);
    }

    void setApplication(Application application, final WorkMode workMode) {

        notNull(application);
        notNull(workMode);


        // create dependency graph
        // this list can get long, formatting one parameter per line helps with merging
        AndroidLogger logger = new AndroidLogger();
        SystemTimeWrapper systemTimeWrapper = new SystemTimeWrapper();
        TodoItemDatabase todoItemDatabase = TodoItemDatabase.getInstance(
                application,
                false,
                workMode);
        TodoListModel todoListModel = new TodoListModel(
                todoItemDatabase,
                logger,
                systemTimeWrapper,
                workMode);
        BossMode bossMode = new BossMode(
                todoListModel,
                systemTimeWrapper,
                workMode,
                logger);
        // networking classes common to all models
        Retrofit retrofit = CustomRetrofitBuilder.create(
                new CustomGlobalRequestInterceptor(logger),
                new InterceptorLogging(logger));//logging interceptor should be the last one
        CallProcessor<UserMessage> callProcessor = new CallProcessor<UserMessage>(
                new CustomGlobalErrorHandler(logger),
                logger);
        RemoteWorker remoteWorker = new RemoteWorker(
                todoListModel,
                retrofit.create(TodoItemService.class),
                callProcessor,
                systemTimeWrapper,
                logger,
                workMode);


        // add models to the dependencies map if you will need them later
        dependencies.put(BossMode.class, bossMode);
        dependencies.put(TodoListModel.class, todoListModel);
        dependencies.put(RemoteWorker.class, remoteWorker);
        dependencies.put(Logger.class, logger);

    }

    void init() {
        if (!initialized) {
            initialized = true;

            // run any necessary initialization code once object graph has been created here

        }
    }

    <T> T get(Class<T> model) {

        notNull(model);
        T t = model.cast(dependencies.get(model));
        notNull(t);

        return t;
    }

    <T> void putMock(Class<T> clazz, T object) {

        notNull(clazz);
        notNull(object);

        dependencies.put(clazz, object);
    }

}
