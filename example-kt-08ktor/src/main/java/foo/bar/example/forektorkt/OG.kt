package foo.bar.example.forektorkt

import android.app.Application
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallProcessorKtor
import foo.bar.example.forektorkt.api.CustomGlobalErrorHandler
import foo.bar.example.forektorkt.api.CustomGlobalRequestInterceptor
import foo.bar.example.forektorkt.api.CustomKtorBuilder
import foo.bar.example.forektorkt.api.fruits.FruitService
import foo.bar.example.forektorkt.feature.fruit.FruitFetcher
import java.util.*
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

        val logger = AndroidLogger("fore_")

        // networking classes common to all models
        val httpClient = CustomKtorBuilder.create(
                CustomGlobalRequestInterceptor(logger),
                InterceptorLogging(logger)
        )//logging interceptor should be the last one

        val callProcessor = CallProcessorKtor(
                globalErrorHandler = CustomGlobalErrorHandler(logger),
                logger = logger
        )

        // models
        val fruitFetcher = FruitFetcher(
                FruitService.create(httpClient),
                callProcessor,
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
