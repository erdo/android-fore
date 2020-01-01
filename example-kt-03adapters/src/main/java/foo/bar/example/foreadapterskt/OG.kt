package foo.bar.example.foreadapterskt

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.core.logging.AndroidLogger
import co.early.fore.core.time.SystemTimeWrapper
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistAdvancedModel
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistSimpleModel
import java.util.HashMap


/**
 *
 * OG - Object Graph, pure DI implementation
 *
 * Copyright © 2019 early.co. All rights reserved.
 */
object OG {

    @Volatile
    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    @JvmOverloads
    fun setApplication(application: Application, workMode: WorkMode = WorkMode.ASYNCHRONOUS) {

        // create dependency graph
        val logger = AndroidLogger("fore_")
        val systemTimeWrapper = SystemTimeWrapper()
        val playlistAdvancedModel = PlaylistAdvancedModel(
            systemTimeWrapper,
            workMode,
            logger
        )
        val playlistSimpleModel = PlaylistSimpleModel(
            workMode,
            logger
        )


        // add models to the dependencies map if you will need them later
        dependencies[SystemTimeWrapper::class.java] = systemTimeWrapper
        dependencies[PlaylistAdvancedModel::class.java] = playlistAdvancedModel
        dependencies[PlaylistSimpleModel::class.java] = playlistSimpleModel

    }


    fun init() {
        if (!initialized) {
            initialized = true

            // run any necessary initialization code once object graph has been created here

        }
    }


    /**
     * This is how dependencies get injected, typically an Activity/Fragment/View will call this
     * during the onCreate()/onCreateView()/onFinishInflate() method respectively for each of the
     * dependencies it needs.
     *
     * Can use a DI library for similar behaviour using annotations
     *
     * Will return mocks if they have been set previously in putMock()
     *
     *
     * Call it like this:
     *
     * <code>
     *     yourModel = OG[YourModel::class.java]
     * </code>
     *
     * If you want to more tightly scoped object, one way is to pass a factory class here and create
     * an instance where you need it
     *
     */
    operator fun <T> get(model: Class<T>): T = dependencies[model] as T

    fun <T> putMock(clazz: Class<T>, instance: T) {
        dependencies[clazz] = instance as Any
    }
}
