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
import com.apollographql.apollo.ApolloQueryCall
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import java.util.Random

data class LaunchService (
    val getLaunchList: () -> ApolloQueryCall<LaunchListQuery.Data>,
    val getLaunchListFailGeneric: () -> ApolloQueryCall<LaunchListQuery.Data>,
    val getLaunchListFailSpecific: () -> ApolloQueryCall<LaunchListQuery.Data>,
)

/**
 * gets a list of launches from the network, selects one at random to be currentLaunch
 */


class LaunchFetcher (
        private val launchService: LaunchService,
        private val callProcessor: ApolloCallProcessor<ErrorMessage>,
        private val logger: Logger,
        private val workMode: WorkMode
) : Observable by ObservableImp(workMode, logger) {

    var isBusy: Boolean = false
        private set
    var currentLaunch = NO_LAUNCH
        private set


    fun fetchLaunchesAsync(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchLaunchesAsync()")

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
                is Right -> handleSuccess(success, result.b.data.launches)
                is Left -> handleFailure(failureWithPayload, result.a)
            }
        }

    }


    /**
     * identical to fetchLaunchesAsync() but for demo purposes the URL we point to will give us an error,
     * we also don't specify a custom error class here
     */
    fun fetchLaunchesButFail(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchLaunchesButFail()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()


        launchMain(workMode) {

           // val result: Either<UserMessage, SuccessResult<LaunchListQuery.Data>> = callProcessor.processCallAwait {
            val result = callProcessor.processCallAwait {
                launchService.getLaunchListFailGeneric()
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b.data.launches)
            }
        }
    }


    /**
     * identical to fetchLaunchesAsync() but for demo purposes the URL we point to will give us a
     * more specific error using a field called "code" in the extras map of the error object
     */
    fun fetchLaunchesButFailAdvanced(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchLaunchesButFailAdvanced()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                launchService.getLaunchListFailSpecific()
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b.data.launches)
            }
        }
    }


    /**
     * Demonstration of how to use carryOn to chain multiple connection requests together in a
     * simple way - don't overuse it! if it's starting to get hard to test, a little restructuring
     * might be in order
     */
    fun fetchManyThings(
            success: Success,
            failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchManyThings()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
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

    }

    private fun handleSuccess(
            success: Success,
            successResponse: LaunchListQuery.Launches
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread().id)

        currentLaunch = selectRandomLaunch(successResponse)
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
