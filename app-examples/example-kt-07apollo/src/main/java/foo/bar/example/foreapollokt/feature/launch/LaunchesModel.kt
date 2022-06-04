package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.Error
import co.early.fore.kt.core.Success
import co.early.fore.kt.core.carryOn
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.net.apollo.CallProcessorApollo
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.ApolloQueryCall
import foo.bar.example.foreapollokt.feature.FailureCallback
import foo.bar.example.foreapollokt.feature.SuccessCallback
import foo.bar.example.foreapollokt.feature.authentication.Authenticator
import foo.bar.example.foreapollokt.graphql.*
import foo.bar.example.foreapollokt.message.ErrorMessage
import java.util.*

data class LaunchService(
    val getLaunchList: () -> ApolloQueryCall<LaunchListQuery.Data>,
    val login: (email: String) -> ApolloMutationCall<LoginMutation.Data>,
    val refreshLaunchDetail: (id: String) -> ApolloQueryCall<LaunchDetailsQuery.Data>,
    val bookTrip: (id: String) -> ApolloMutationCall<BookTripMutation.Data>,
    val cancelTrip: (id: String) -> ApolloMutationCall<CancelTripMutation.Data>
)


class LaunchesModel(
    private val launchService: LaunchService,
    private val callProcessor: CallProcessorApollo<ErrorMessage>,
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

        launchMain {

            val deferredResult = callProcessor.processCallAsync {
                launchService.getLaunchList()
            }

            when (val result = deferredResult.await()) {
                is Success -> handleSuccess(success, selectRandomLaunch(result.b.data.launches))
                is Error -> handleFailure(failureWithPayload, result.a)
            }
        }

    }

    /**
     * here we perform multiple network calls, all chained together:
     * login (if we aren't already) > re-fetch current launch to check the
     * booking status > toggle the booking status to the opposite of what
     * it was > re-fetch the launch detail again
     *
     * Slightly more clunky than the retrofit equivalent,
     * but not too bad
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

        launchMain {

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

            when (response) {
                is Error -> handleFailure(failureWithPayload, response.a)
                is Success -> handleSuccess(success, response.b.data.launch?.toApp() ?: NO_LAUNCH)
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
