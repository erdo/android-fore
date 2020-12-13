package foo.bar.example.foreapollokt.feature.authentication

import co.early.fore.core.WorkMode
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.Either.Left
import co.early.fore.kt.core.Either.Right
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.net.apollo.ApolloCallProcessor
import com.apollographql.apollo.ApolloMutationCall
import foo.bar.example.foreapollokt.graphql.LoginMutation
import foo.bar.example.foreapollokt.message.ErrorMessage

data class AuthService(
        val login: (email: String) -> ApolloMutationCall<LoginMutation.Data>
)

/**
 * logs the user in / out (gets a session token from the server / disposes of it)
 */
class Authenticator(
        private val authService: AuthService,
        private val callProcessor: ApolloCallProcessor<ErrorMessage>,
        private val logger: Logger,
        private val workMode: WorkMode
) : Observable by ObservableImp(workMode, logger) {

    var isBusy: Boolean = false
        private set
    var sessionToken = NO_SESSION
        private set

    /**
     * get a session token from the server using a GraphQl Mutation
     */
    fun login(
            email: String,
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("login() :$email")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        if (email.isBlank()) {
            failureWithPayload(ErrorMessage.ERROR_BLANK_EMAIL)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            val deferredResult = callProcessor.processCallAsync {
                authService.login(email)
            }

            when (val result = deferredResult.await()) {
                is Right -> handleSuccess(success, result.b.data.login)
                is Left -> handleFailure(failureWithPayload, result.a)
            }
        }
    }

    /**
     * drops the sessions token and informs any observers
     */
    fun logout() {

        logger.i("logout()")

        sessionToken = NO_SESSION //in reality you would probably also tell the server the user wants to log out
        complete()
    }

    /**
     * if you have a session token from elsewhere (from storage maybe) you can set it here
     */
    fun setSessionDirectly(sessionToken: String?) {

        logger.i("setSessionDirectly()")

        this.sessionToken = sessionToken ?: NO_SESSION
        complete()
    }

    fun hasSessionToken() : Boolean {
        return sessionToken != NO_SESSION
    }

    private fun handleSuccess(
            success: Success,
            successResponse: String?
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread().id)

        sessionToken = successResponse ?: NO_SESSION
        success()
        complete()
    }

    private fun handleFailure(
            failureWithPayload: FailureWithPayload<ErrorMessage>,
            failureMessage: ErrorMessage
    ) {

        logger.i("handleFailure() t:" + Thread.currentThread().id)

        failureWithPayload(failureMessage)
        complete()
    }

    private fun complete() {

        logger.i("complete() t:" + Thread.currentThread().id)

        isBusy = false
        notifyObservers()
    }

    companion object {
        const val NO_SESSION = ""
    }
}
