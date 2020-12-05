package foo.bar.example.foreapollokt

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.net.apollo.ApolloCallProcessor
import co.early.fore.kt.net.InterceptorLogging
import foo.bar.example.foreapollokt.api.CustomApolloBuilder
import foo.bar.example.foreapollokt.api.CustomGlobalErrorHandler
import foo.bar.example.foreapollokt.api.CustomGlobalRequestInterceptor
import foo.bar.example.foreapollokt.feature.launch.LaunchFetcher
import foo.bar.example.foreapollokt.feature.launch.LaunchService
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import java.util.HashMap


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

    @JvmOverloads
    fun setApplication(application: Application, workMode: WorkMode = WorkMode.ASYNCHRONOUS) {

        // create dependency graph

        val logger = AndroidLogger("fore_")

        // networking classes common to all models
        val apolloClient = CustomApolloBuilder.create(
                CustomGlobalRequestInterceptor(logger),
                InterceptorLogging(logger)
        )//logging interceptor should be the last one

        //apolloClient.idleCallback()

        val callProcessor = ApolloCallProcessor(
                CustomGlobalErrorHandler(logger),
                logger
        )

        // models
        val launchFetcher = LaunchFetcher(
                launchService = LaunchService(
                        getLaunchList = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailGeneric = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailSpecific = { apolloClient.query(LaunchListQuery()) }
                ),
                callProcessor,
                logger,
                workMode
        )

        // add models to the dependencies map if you will need them later
        dependencies[LaunchFetcher::class.java] = launchFetcher
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
