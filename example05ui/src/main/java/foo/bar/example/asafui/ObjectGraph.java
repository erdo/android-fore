package foo.bar.example.asafui;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.AndroidLogger;

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
//
//        notNull(application);
//        notNull(workMode);
//
//
        // create dependency graph
        AndroidLogger logger = new AndroidLogger();

        // networking classes common to all models
        Retrofit retrofit = CustomRetrofitBuilder.create(
                new CustomGlobalRequestInterceptor(logger),
                new InterceptorLogging(logger));//logging interceptor should be the last one

        CallProcessor<UserMessage> callProcessor = new CallProcessor<UserMessage>(
                new CustomGlobalErrorHandler(logger),
                logger);


        // models
        FruitCollector fruitCollector = new FruitCollector(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                workMode);
//
//
//        // add models to the dependencies map if you will need them later
//        dependencies.put(FruitFetcher.class, fruitFetcher);
//
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
