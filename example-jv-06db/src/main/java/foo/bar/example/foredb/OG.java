package foo.bar.example.foredb;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.AndroidLogger;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.time.SystemTimeWrapper;
import co.early.fore.net.retrofit2.CallProcessorRetrofit2;
import co.early.fore.net.InterceptorLogging;
import foo.bar.example.foredb.api.CustomGlobalErrorHandler;
import foo.bar.example.foredb.api.CustomGlobalRequestInterceptor;
import foo.bar.example.foredb.api.CustomRetrofitBuilder;
import foo.bar.example.foredb.api.todoitems.TodoItemService;
import foo.bar.example.foredb.db.todoitems.TodoItemDatabase;
import foo.bar.example.foredb.feature.bossmode.BossMode;
import foo.bar.example.foredb.feature.remote.RemoteWorker;
import foo.bar.example.foredb.feature.todoitems.TodoListModel;
import foo.bar.example.foredb.message.UserMessage;
import retrofit2.Retrofit;

import static co.early.fore.core.Affirm.notNull;

/**
 *
 * OG - Object Graph, pure DI implementation
 *
 * Copyright © 2019 early.co. All rights reserved.
 */
public class OG {

    private static boolean initialized = false;
    private static final Map<Class<?>, Object> dependencies = new HashMap<>();


    public static void setApplication(Application application) {
        setApplication(application, WorkMode.ASYNCHRONOUS);
    }

    public static void setApplication(Application application, final WorkMode workMode) {

        notNull(application);
        notNull(workMode);


        // create dependency graph
        AndroidLogger logger = new AndroidLogger("fore_");
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

        Retrofit retrofit = CustomRetrofitBuilder.create(
                new CustomGlobalRequestInterceptor(logger),
                new InterceptorLogging(logger));//logging interceptor should be the last one
        CallProcessorRetrofit2<UserMessage> callProcessor = new CallProcessorRetrofit2<UserMessage>(
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

    public static void init() {
        if (!initialized) {
            initialized = true;

            // run any necessary initialization code once object graph has been created here
            get(TodoListModel.class).fetchLatestFromDb();
        }
    }

    public static <T> T get(Class<T> model) {

        notNull(model);
        T t = model.cast(dependencies.get(model));
        notNull(t);

        return t;
    }

    public static <T> void putMock(Class<T> clazz, T object) {

        notNull(clazz);
        notNull(object);

        dependencies.put(clazz, object);
    }

}
