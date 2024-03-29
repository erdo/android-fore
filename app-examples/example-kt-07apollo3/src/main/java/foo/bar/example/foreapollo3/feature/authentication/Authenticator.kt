package foo.bar.example.foreapollo3.feature.authentication

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.awaitMain
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.core.type.Either.Fail
import co.early.fore.kt.core.type.Either.Success
import co.early.fore.kt.net.apollo3.CallWrapperApollo3
import com.apollographql.apollo3.api.ApolloResponse
import foo.bar.example.foreapollo3.LoginMutation
import foo.bar.example.foreapollo3.feature.FailureCallback
import foo.bar.example.foreapollo3.feature.SuccessCallback
import foo.bar.example.foreapollo3.message.ErrorMessage

data class AuthService(
    val login: suspend (email: String) -> ApolloResponse<LoginMutation.Data>
)

/**
 * logs the user in / out (gets a session token from the server / disposes of it)
 */
class Authenticator(
    private val authService: AuthService,
    private val callWrapperApollo3: CallWrapperApollo3<ErrorMessage>,
    private val logger: Logger
) : Observable by ObservableImp(logger = logger) {

    var isBusy: Boolean = false
        private set
    var sessionToken = NO_SESSION
        private set

    /**
     * get a session token from the server using a GraphQl Mutation
     */
    fun login(
        email: String,
        success: SuccessCallback,
        failureWithPayload: FailureCallback<ErrorMessage>
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

        launchIO {

            val deferredResult = callWrapperApollo3.processCallAsync {
                authService.login(email)
            }

            awaitMain {
                when (val result = deferredResult.await()) {
                    is Success -> handleSuccess(success, result.value.data.login?.token)
                    is Fail -> handleFailure(failureWithPayload, result.value)
                }
            }
        }
    }

    /**
     * drops the sessions token and informs any observers
     */
    fun logout() {

        logger.i("logout()")

        sessionToken =
            NO_SESSION //in reality you would probably also tell the server the user wants to log out
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

    fun hasSessionToken(): Boolean {
        return sessionToken != NO_SESSION
    }

    private fun handleSuccess(
        success: SuccessCallback,
        successResponse: String?
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread().id)

        sessionToken = successResponse ?: NO_SESSION
        success()
        complete()
    }

    private fun handleFailure(
        failureWithPayload: FailureCallback<ErrorMessage>,
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
