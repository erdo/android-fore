package foo.bar.example.foreadapters;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.AndroidLogger;
import foo.bar.example.foreadapters.feature.playlist.PlaylistAdvancedModel;
import foo.bar.example.foreadapters.feature.playlist.PlaylistSimpleModel;
import co.early.fore.core.time.SystemTimeWrapper;

import static co.early.fore.core.Affirm.notNull;

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
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(
                systemTimeWrapper,
                workMode,
                logger);
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(
                workMode,
                logger);



        // add models to the dependencies map if you will need them later
        dependencies.put(SystemTimeWrapper.class, systemTimeWrapper);
        dependencies.put(PlaylistAdvancedModel.class, playlistAdvancedModel);
        dependencies.put(PlaylistSimpleModel.class, playlistSimpleModel);

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
