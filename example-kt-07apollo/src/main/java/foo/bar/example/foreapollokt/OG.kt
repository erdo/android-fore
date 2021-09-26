package foo.bar.example.foreapollokt

import android.app.Application
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.core.logging.SilentLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.apollo.CallProcessorApollo
import com.apollographql.apollo.api.Input
import foo.bar.example.foreapollokt.api.CustomApolloBuilder
import foo.bar.example.foreapollokt.api.CustomGlobalErrorHandler
import foo.bar.example.foreapollokt.api.CustomGlobalRequestInterceptor
import foo.bar.example.foreapollokt.feature.authentication.AuthService
import foo.bar.example.foreapollokt.feature.authentication.Authenticator
import foo.bar.example.foreapollokt.feature.launch.LaunchService
import foo.bar.example.foreapollokt.feature.launch.LaunchesModel
import foo.bar.example.foreapollokt.graphql.*
import java.util.*


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

        val logger = if (BuildConfig.DEBUG) AndroidLogger("fore_") else SilentLogger()

        // networking classes common to all models
        val globalRequestInterceptor = CustomGlobalRequestInterceptor(logger)
        val apolloClient = CustomApolloBuilder.create(
            globalRequestInterceptor,
            InterceptorLogging(logger)
        )//logging interceptor should be the last one

        val callProcessor = CallProcessorApollo(
            CustomGlobalErrorHandler(logger),
            logger
        )

        // models
        val authenticator = Authenticator(
            authService = AuthService(
                login = { email -> apolloClient.mutate(LoginMutation(Input.optional(email))) }
            ),
            callProcessor,
            logger,
            workMode
        )
        globalRequestInterceptor.setAuthenticator(authenticator)
        val launchesModel = LaunchesModel(
            launchService = LaunchService(
                getLaunchList = { apolloClient.query(LaunchListQuery()) },
                login = { email -> apolloClient.mutate(LoginMutation(Input.optional(email))) },
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
