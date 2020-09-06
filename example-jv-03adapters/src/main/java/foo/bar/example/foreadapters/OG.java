package foo.bar.example.foreadapters;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.AndroidLogger;
import co.early.fore.core.time.SystemTimeWrapper;
import foo.bar.example.foreadapters.feature.playlist.PlaylistAdvancedModel;
import foo.bar.example.foreadapters.feature.playlist.PlaylistSimpleModel;

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

    public static void init() {
        if (!initialized) {
            initialized = true;

            // run any necessary initialization code once object graph has been created here

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
