package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.net.apollo.ApolloCallProcessor
import co.early.fore.kt.Either.Left
import co.early.fore.kt.Either.Right
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.ApolloQueryCall
import foo.bar.example.foreapollokt.feature.authentication.Authenticator
import foo.bar.example.foreapollokt.graphql.*
import foo.bar.example.foreapollokt.message.ErrorMessage
import java.util.Random

data class LaunchService (
    val getLaunchList: () -> ApolloQueryCall<LaunchListQuery.Data>,
    val login: (email: String) -> ApolloMutationCall<LoginMutation.Data>,
    val refreshLaunchDetail: (id: String) -> ApolloQueryCall<LaunchDetailsQuery.Data>,
    val bookTrip: (id: String) -> ApolloMutationCall<BookTripMutation.Data>,
    val cancelTrip: (id: String) -> ApolloMutationCall<CancelTripMutation.Data>
)


class LaunchesModel (
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
     * log in, fetch random launch, toggle booking status
     * chained call demo
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

        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                launchService.login("example@test.com")
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> {
                    // don't log things like this out in the real world!
                    logger.i("new session token:" + result.b.data.login)
                    authenticator.setSessionDirectly(result.b.data.login)
                    success()
                    complete()
                }
            }
        }
    }


    fun bookTripOnCurrentLaunch(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("bookCurrentTrip()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        if (currentLaunch == NO_LAUNCH) {
            failureWithPayload(ErrorMessage.ERROR_NO_LAUNCH)
            return
        }

        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                launchService.bookTrip(currentLaunch.id)
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> {
                    logger.i("just booked:" + result.b.data.bookTrips.launches?.get(0)?.id)
                    success()
                    complete()
                }
            }
        }
    }


    fun cancelTripOnCurrentLaunch(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("cancelCurrentTrip()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        if (currentLaunch == NO_LAUNCH) {
            failureWithPayload(ErrorMessage.ERROR_NO_LAUNCH)
            return
        }

        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                launchService.cancelTrip(currentLaunch.id)
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> {
                    logger.i("just cancelled:" + result.b.data.cancelTrip.launches?.get(0)?.id)
                    success()
                    complete()
                }
            }
        }
    }


    fun refreshLaunchDetails(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("refreshLaunchDetails()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        if (currentLaunch == NO_LAUNCH) {
            failureWithPayload(ErrorMessage.ERROR_NO_LAUNCH)
            return
        }

        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                launchService.refreshLaunchDetail(currentLaunch.id)
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b.data.launch?.toApp() ?: NO_LAUNCH)
            }
        }
    }




//        isBusy = true
//        notifyObservers()
//
//        launchMain(workMode) {
//
//            val result = callProcessor.processCallAwait(
//                FruitsCustomError::class.java
//            ) {
//                var ticketRef = ""
//                logger.i("...create user...")
//                fruitService.getLaunchList
//                    .carryOn {
//                        logger.i("...create user ticket...")
//                        fruitService.createUserTicket(it.userId)
//                    }
//                    .carryOn {
//                        ticketRef = it.ticketRef
//                        logger.i("...get waiting time...")
//                        fruitService.getEstimatedWaitingTime(it.ticketRef)
//                    }
//                    .carryOn {
//                        if (it.minutesWait > 10) {
//                            logger.i("...cancel ticket...")
//                            fruitService.cancelTicket(ticketRef)
//                        } else {
//                            logger.i("...confirm ticket...")
//                            fruitService.confirmTicket(ticketRef)
//                        }
//                    }
//                    .carryOn {
//                        logger.i("...claim free fruit!...")
//                        fruitService.claimFreeFruit(it.ticketRef)
//                    }
//            }
//
//            when (result) {
//                is Left -> handleFailure(failureWithPayload, result.a)
//                is Right -> handleSuccess(success, result.b)
//            }
//        }
// }

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
