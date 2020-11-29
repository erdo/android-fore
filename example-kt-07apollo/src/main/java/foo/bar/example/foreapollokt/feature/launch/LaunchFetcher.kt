package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.apollo.CallProcessor
import co.early.fore.kt.apollo.Either.Left
import co.early.fore.kt.apollo.Either.Right
import com.apollographql.apollo.ApolloQueryCall
import foo.bar.example.foreapollokt.api.fruits.FruitsCustomError
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.UserMessage
import java.util.Random

data class LaunchService (
    val getLaunchList: ApolloQueryCall<LaunchListQuery.Data>,
    val getLaunchListFailGeneric: ApolloQueryCall<LaunchListQuery.Data>,
    val getLaunchListFailSpecific: ApolloQueryCall<LaunchListQuery.Data>,
)

/**
 * gets a list of launches from the network, selects one at random to be currentLaunch
 */


class LaunchFetcher(
        private val launchService: LaunchService,
        private val callProcessor: CallProcessor<UserMessage>,
        private val logger: Logger,
        private val workMode: WorkMode
) : Observable by ObservableImp(workMode, logger) {

    var isBusy: Boolean = false
        private set
    var currentLaunch = NO_LAUNCH
        private set


    fun fetchLaunchesAsync(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchLaunchesAsync() t:" + Thread.currentThread())

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            logger.i("about to use CallProcessor t:" + Thread.currentThread())

            val deferredResult = callProcessor.processCallAsync {

                logger.i("processing call t:" + Thread.currentThread())

                launchService.getLaunchList
            }

            when (val result = deferredResult.await()) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b.launches)
            }
        }

    }


    /**
     * identical to fetchLaunchesAsync() but for demo purposes the URL we point to will give us an error,
     * we also don't specify a custom error class here
     */
    fun fetchLaunchesButFail(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchLaunchesButFail()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()


        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                launchService.getLaunchListFailGeneric
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b.launches)
            }
        }
    }


    /**
     * identical to fetchLaunchesAsync() but for demo purposes the URL we point to will give us an error,
     * here we specify a custom error class for more detail about the error than just an HTTP code can give us
     */
    fun fetchLaunchesButFailAdvanced(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchLaunchesButFailAdvanced()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            val result = callProcessor.processCallAwait(FruitsCustomError::class.java) {
                launchService.getLaunchListFailSpecific
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b.launches)
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
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchManyThings()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
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

        logger.i("handleSuccess() t:" + Thread.currentThread())

        currentLaunch = selectRandomLaunch(successResponse)
        success()
        complete()
    }

    private fun handleFailure(
            failureWithPayload: FailureWithPayload<UserMessage>,
            failureMessage: UserMessage
    ) {

        logger.i("handleFailure() t:" + Thread.currentThread())

        failureWithPayload(failureMessage)
        complete()
    }

    private fun complete() {

        logger.i("complete() t:" + Thread.currentThread())

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
