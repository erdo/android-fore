package foo.bar.example.forecompose

import android.app.Application
import co.early.fore.kt.core.delegate.DebugDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.*
import co.early.persista.PerSista
import foo.bar.example.forecompose.feature.counter.CounterModel
import java.util.*

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 */
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    fun setApplication(application: Application) {

        // create dependency graph
        if (BuildConfig.DEBUG) {
            Fore.setDelegate(DebugDelegateDefault("foo_"))
        }
        val logger = Fore.getLogger()

        val perSista = PerSista(
            dataDirectory = application.filesDir,
            logger = logger,
        )

        val counterModel = CounterModel(
            perSista = perSista,
            logger = logger,
        )

        // add models to the dependencies map if you will need them later
        dependencies[CounterModel::class.java] = counterModel
    }

    fun init() {
        if (!initialized) {
            initialized = true

            // run any necessary initialization code once object graph has been created here

//            // just in case you need to, but defaults are recommended
//            ForeSize.overrideBreakPoints(
//                ViewPortBreakPoints(
//                    widthSDpBelow = 400.dp,
//                    widthMDpBelow = 500.dp,
//                )
//            )

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
