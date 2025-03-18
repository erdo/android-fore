package foo.bar.example.foreapollo3.feature.launch

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.awaitMain
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.core.type.Either.Fail
import co.early.fore.kt.core.type.Either.Success
import co.early.fore.kt.core.type.carryOn
import co.early.fore.kt.net.apollo3.CallWrapperApollo3
import com.apollographql.apollo3.api.ApolloResponse
import foo.bar.example.foreapollo3.*
import foo.bar.example.foreapollo3.feature.FailureCallback
import foo.bar.example.foreapollo3.feature.SuccessCallback
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.message.ErrorMessage
import java.util.*

data class LaunchService(
    val getLaunchList: suspend () -> ApolloResponse<LaunchListQuery.Data>,
    val login: suspend (email: String) -> ApolloResponse<LoginMutation.Data>,
    val refreshLaunchDetail: suspend (id: String) -> ApolloResponse<LaunchDetailsQuery.Data>,
    val bookTrip: suspend (id: String) -> ApolloResponse<BookTripMutation.Data>,
    val cancelTrip: suspend (id: String) -> ApolloResponse<CancelTripMutation.Data>
)


class LaunchesModel(
    private val launchService: LaunchService,
    private val callWrapper: CallWrapperApollo3<ErrorMessage>,
    private val authenticator: Authenticator,
    private val logger: Logger
) : Observable by ObservableImp(logger = logger) {

    var isBusy: Boolean = false
        private set
    var currentLaunch = NO_LAUNCH
        private set

    /**
     * fetch the list of launches using a GraphQl Query, select one at random for the UI
     */
    fun fetchLaunches(
        success: SuccessCallback,
        failureWithPayload: FailureCallback<ErrorMessage>
    ) {

        logger.i("fetchLaunches()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchIO {

            val deferredResult = callWrapper.processCallAsync {
                launchService.getLaunchList()
            }

            awaitMain {
                when (val result = deferredResult.await()) {
                    is Success -> handleSuccess(success, selectRandomLaunch(result.value.data.launches))
                    is Fail -> handleFailure(failureWithPayload, result.value)
                }
            }
        }
    }

    /**
     * here we perform multiple network calls, all chained together:
     * login (if we aren't already) > re-fetch current launch to check the
     * booking status > toggle the booking status to the opposite of what
     * it was > re-fetch the launch detail again
     */
    fun chainedCall(
        success: SuccessCallback,
        failureWithPayload: FailureCallback<ErrorMessage>
    ) {

        logger.i("chainedCall()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        if (currentLaunch == NO_LAUNCH) {
            failureWithPayload(ErrorMessage.ERROR_NO_LAUNCH)
            return
        }

        isBusy = true
        notifyObservers()

        launchIO {

            /**
             * we're using fore's carryOn() extension function here but you can do whatever
             * you like here, including using reactive streams if appropriate. Once your network
             * chain is complete though, when you want to expose the resulting state,
             * you need to: 1) set it, and 2) call notifyObservers().
             *
             * (carryOn() lets you transparently handle networking
             * errors at each step - Internet search for "railway oriented programming"
             * or "andThen" functions)
             */
            // log in
            val response = callWrapper.processCallAwait {
                launchService.login("example@test.com")
            }.carryOn { // refresh launch details now we have session token to see the real booking status
                authenticator.setSessionDirectly(it.data.login?.token)
                callWrapper.processCallAwait {
                    launchService.refreshLaunchDetail(currentLaunch.id)
                }
            }.carryOn { // toggle the booking status
                if (it.data.launch?.isBooked == true) {
                    callWrapper.processCallAwait {
                        launchService.cancelTrip(currentLaunch.id)
                    }
                } else {
                    callWrapper.processCallAwait {
                        launchService.bookTrip(currentLaunch.id)
                    }
                }
            }.carryOn { // refresh the launch detail again
                callWrapper.processCallAwait {
                    launchService.refreshLaunchDetail(currentLaunch.id)
                }
            }

            awaitMain {
                when (response) {
                    is Fail -> handleFailure(failureWithPayload, response.value)
                    is Success -> handleSuccess(success, response.value.data.launch?.toApp() ?: NO_LAUNCH)
                }
            }
        }
    }

    private fun handleSuccess(
        success: SuccessCallback,
        launch: Launch
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread().id)

        currentLaunch = launch
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

    private fun selectRandomLaunch(launches: LaunchListQuery.Launches): Launch {
        val listOfLaunches = launches.launches.filterNotNull()
        return when {
            listOfLaunches.isEmpty() -> NO_LAUNCH
            listOfLaunches.size == 1 -> listOfLaunches[0].toApp()
            else -> listOfLaunches[random.nextInt(listOfLaunches.size - 1)].toApp()
        }
    }

    companion object {
        private val random = Random()
    }
}
