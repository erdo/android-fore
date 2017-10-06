package foo.bar.example.asafthreading;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.asaf.framework.WorkMode;
import co.early.asaf.framework.logging.AndroidLogger;
import foo.bar.example.asafthreading.feature.counter.CounterBasic;
import foo.bar.example.asafthreading.feature.counter.CounterWithProgress;

import static co.early.asaf.framework.Affirm.notNull;

/**
 * This is the price you pay for not using Dagger, the payback is not having to write modules
 */
class ObjectGraph {

    private volatile boolean initialized = false;

    private final Map<Class<?>, Object> dependencies;


    ObjectGraph() {
        dependencies = new HashMap<>();
    }

    void setApplication(Application application) {
        setApplication(application, WorkMode.ASYNCHRONOUS);
    }

    void setApplication(Application application, final WorkMode workMode) {

        notNull(application);
        notNull(workMode);

        AndroidLogger logger = new AndroidLogger();


        // create dependency graph
        // this list can get long, formatting one parameter per line helps with merging
        final CounterBasic counterBasic = new CounterBasic(
                workMode,
                logger);
        final CounterWithProgress counterWithProgress = new CounterWithProgress(
                workMode,
                logger);



        // add models to the dependencies map if you will need them later
        dependencies.put(CounterBasic.class, counterBasic);
        dependencies.put(CounterWithProgress.class, counterWithProgress);

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
