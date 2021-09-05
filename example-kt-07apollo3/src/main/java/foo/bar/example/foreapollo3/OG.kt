package foo.bar.example.foreapollo3

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.core.logging.SilentLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.apollo3.CallProcessorApollo3
import com.apollographql.apollo3.api.ApolloRequest
import foo.bar.example.foreapollokt.BuildConfig
import foo.bar.example.foreapollo3.api.CustomApolloBuilder
import foo.bar.example.foreapollo3.api.CustomGlobalErrorHandler
import foo.bar.example.foreapollo3.api.CustomGlobalRequestInterceptor
import foo.bar.example.foreapollo3.feature.authentication.AuthService
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.feature.launch.LaunchService
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import java.util.*


/**
 *
 * OG - Object Graph, pure DI implementation
 *
 * Copyright © 2019 early.co. All rights reserved.
 */
@ExperimentalStdlibApi
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    @JvmOverloads
    fun setApplication(application: Application, workMode: WorkMode = WorkMode.ASYNCHRONOUS) {

        // create dependency graph

        val logger = if (BuildConfig.DEBUG) AndroidLogger("fore_") else SilentLogger()

        // networking classes common to all models
        val globalRequestInterceptor = CustomGlobalRequestInterceptor(logger)
        val apolloClient = CustomApolloBuilder.create(
                globalRequestInterceptor,
                InterceptorLogging(logger)
        )//logging interceptor should be the last one

        val callProcessor = CallProcessorApollo3(
                CustomGlobalErrorHandler(logger),
                logger
        )

        // models
        val authenticator = Authenticator(
                authService = AuthService(
                        login = { email -> apolloClient.mutate(ApolloRequest(LoginMutation(email))) }
                ),
                callProcessor,
                logger,
                workMode
        )
        globalRequestInterceptor.setAuthenticator(authenticator)
        val launchesModel = LaunchesModel(
                launchService = LaunchService(
                        getLaunchList = { apolloClient.query(LaunchListQuery()) },
                        login = { email -> apolloClient.mutate(LoginMutation(email)) },
                        refreshLaunchDetail = { id -> apolloClient.query(LaunchDetailsQuery(id)) },
                        bookTrip = { id -> apolloClient.mutate(BookTripMutation(id)) },
                        cancelTrip = { id -> apolloClient.mutate(CancelTripMutation(id)) }
                ),
                callProcessor,
                authenticator,
                logger,
                workMode
        )

        // add models to the dependencies map if you will need them later
        dependencies[Authenticator::class.java] = authenticator
        dependencies[LaunchesModel::class.java] = launchesModel
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