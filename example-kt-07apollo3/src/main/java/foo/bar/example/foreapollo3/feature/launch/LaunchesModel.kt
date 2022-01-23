package foo.bar.example.foreapollo3.feature.launch

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.Either.Left
import co.early.fore.kt.core.Either.Right
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.carryOn
import co.early.fore.kt.core.coroutine.awaitMain
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.net.apollo3.CallProcessorApollo3
import com.apollographql.apollo3.api.ApolloResponse
import foo.bar.example.foreapollo3.*
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
    private val callProcessor: CallProcessorApollo3<ErrorMessage>,
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
        success: Success,
        failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchLaunches()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchIO {

            val deferredResult = callProcessor.processCallAsync {
                launchService.getLaunchList()
            }

            awaitMain {
                when (val result = deferredResult.await()) {
                    is Right -> handleSuccess(success, selectRandomLaunch(result.b.data.launches))
                    is Left -> handleFailure(failureWithPayload, result.a)
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
        success: Success,
        failureWithPayload: FailureWithPayload<ErrorMessage>
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
            val response = callProcessor.processCallAwait {
                launchService.login("example@test.com")
            }.carryOn { // refresh launch details now we have session token to see the real booking status
                authenticator.setSessionDirectly(it.data.login)
                callProcessor.processCallAwait {
                    launchService.refreshLaunchDetail(currentLaunch.id)
                }
            }.carryOn { // toggle the booking status
                if (it.data.launch?.isBooked == true) {
                    callProcessor.processCallAwait {
                        launchService.cancelTrip(currentLaunch.id)
                    }
                } else {
                    callProcessor.processCallAwait {
                        launchService.bookTrip(currentLaunch.id)
                    }
                }
            }.carryOn { // refresh the launch detail again
                callProcessor.processCallAwait {
                    launchService.refreshLaunchDetail(currentLaunch.id)
                }
            }

            awaitMain {
                when (response) {
                    is Left -> handleFailure(failureWithPayload, response.a)
                    is Right -> handleSuccess(success, response.b.data.launch?.toApp() ?: NO_LAUNCH)
                }
            }
        }
    }

    private fun handleSuccess(
        success: Success,
        launch: Launch
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread().id)

        currentLaunch = launch
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
