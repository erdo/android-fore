package foo.bar.example.foreretrofitcoroutine

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.core.logging.AndroidLogger
import co.early.fore.retrofit.InterceptorLogging
import co.early.fore.retrofit.coroutine.CallProcessor
import foo.bar.example.foreretrofitcoroutine.api.CustomGlobalErrorHandler
import foo.bar.example.foreretrofitcoroutine.api.CustomGlobalRequestInterceptor
import foo.bar.example.foreretrofitcoroutine.api.CustomRetrofitBuilder
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitService
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import java.util.HashMap

/**
 * This is the price you pay for not using Dagger, the payback is not having to write modules
 */
internal class ObjectGraph {

    @Volatile
    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    @JvmOverloads
    fun setApplication(application: Application, workMode: WorkMode = WorkMode.ASYNCHRONOUS) {

        // create dependency graph
        val logger = AndroidLogger()

        // networking classes common to all models
        val retrofit = CustomRetrofitBuilder.create(
            CustomGlobalRequestInterceptor(logger),
            InterceptorLogging(logger)
        )//logging interceptor should be the last one

        val callProcessor = CallProcessor(
            CustomGlobalErrorHandler(logger),
            logger
        )


        // models
        val fruitFetcher = FruitFetcher(
            retrofit.create(FruitService::class.java!!),
            callProcessor,
            logger,
            workMode
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

    operator fun <T> get(model: Class<T>): T = model.cast(dependencies[model]) as T

    fun <T> putMock(clazz: Class<T>, instance: T) {
        dependencies[clazz] = instance as Any
    }

}
