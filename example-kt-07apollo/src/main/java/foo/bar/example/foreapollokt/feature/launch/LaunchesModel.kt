package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.net.apollo.ApolloCallProcessor
import co.early.fore.kt.core.Either.Left
import co.early.fore.kt.core.Either.Right
import co.early.fore.kt.core.carryOn
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.ApolloQueryCall
import foo.bar.example.foreapollokt.feature.authentication.Authenticator
import foo.bar.example.foreapollokt.graphql.*
import foo.bar.example.foreapollokt.message.ErrorMessage
import java.util.Random

data class LaunchService(
        val getLaunchList: () -> ApolloQueryCall<LaunchListQuery.Data>,
        val login: (email: String) -> ApolloMutationCall<LoginMutation.Data>,
        val refreshLaunchDetail: (id: String) -> ApolloQueryCall<LaunchDetailsQuery.Data>,
        val bookTrip: (id: String) -> ApolloMutationCall<BookTripMutation.Data>,
        val cancelTrip: (id: String) -> ApolloMutationCall<CancelTripMutation.Data>
)


class LaunchesModel(
        private val launchService: LaunchService,
        private val callProcessor: ApolloCallProcessor<ErrorMessage>,
        private val authenticator: Authenticator,
        private val logger: Logger,
        private val workMode: WorkMode
) : Observable by ObservableImp(workMode, logger) {

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

        launchMain(workMode) {

            val deferredResult = callProcessor.processCallAsync {
                launchService.getLaunchList()
            }

            when (val result = deferredResult.await()) {
                is Right -> handleSuccess(success, selectRandomLaunch(result.b.data.launches))
                is Left -> handleFailure(failureWithPayload, result.a)
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

        launchMain(workMode) {

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
                is Left -> handleFailure(failureWithPayload, response.a)
                is Right -> handleSuccess(success, response.b.data.launch?.toApp() ?: NO_LAUNCH)
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
