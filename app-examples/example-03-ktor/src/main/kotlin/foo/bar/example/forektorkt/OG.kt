package foo.bar.example.forektorkt

import android.app.Application
import co.early.fore.core.delegate.DelegateDebug
import co.early.fore.core.delegate.Fore
import co.early.fore.net.wrap.CallWrapper
import foo.bar.example.forektorkt.api.GlobalErrorHandler
import foo.bar.example.forektorkt.api.KtorClientBuilder
import foo.bar.example.forektorkt.api.fruits.FruitService
import foo.bar.example.forektorkt.feature.fruit.FruitFetcher
import kotlin.collections.set

/**
 *
 * OG - Object Graph, pure DI implementation
 *
 * Copyright © 2019 early.co. All rights reserved.
 */
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    fun setApplication(application: Application) {

        // create dependency graph

        if (BuildConfig.DEBUG) {
            Fore.setDelegate(DelegateDebug("fore_"))
        }
        val logger = Fore.getLogger()

        // networking classes common to all models
        val httpClient = KtorClientBuilder.create(logger)
        val callWrapper = CallWrapper(
            errorHandler = GlobalErrorHandler(logger),
            logger = logger
        )

        // models
        val fruitFetcher = FruitFetcher(
            FruitService(httpClient),
            callWrapper,
            logger
        )

        // add models to the dependencies map if you will need them later
        dependencies[FruitFetcher::class.java] = fruitFetcher
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
