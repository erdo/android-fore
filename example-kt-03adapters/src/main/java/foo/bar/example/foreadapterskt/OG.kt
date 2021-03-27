package foo.bar.example.foreadapterskt

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.immutable.ImmutablePlaylistModel
import java.util.HashMap


/**
 * OG - Object Graph, pure DI implementation
 *
 * Copyright © 2015-2020 early.co. All rights reserved.
*/
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    @JvmOverloads
    fun setApplication(application: Application, workMode: WorkMode = WorkMode.ASYNCHRONOUS) {

        // create dependency graph
        val logger = AndroidLogger("fore_")
        val playlistAdvancedModel = MutablePlaylistModel(
                logger
        )
        val playlistSimpleModel = ImmutablePlaylistModel(
                logger
        )


        // add models to the dependencies map if you will need them later
        dependencies[MutablePlaylistModel::class.java] = playlistAdvancedModel
        dependencies[ImmutablePlaylistModel::class.java] = playlistSimpleModel
        dependencies[Logger::class.java] = logger

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
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(model: Class<T>): T = dependencies[model] as T

    fun <T> putMock(clazz: Class<T>, instance: T) {
        dependencies[clazz] = instance as Any
    }
}
