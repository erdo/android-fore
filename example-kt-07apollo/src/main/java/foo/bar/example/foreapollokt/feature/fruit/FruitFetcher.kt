package foo.bar.example.foreapollokt.feature.fruit

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
import co.early.fore.kt.apollo.carryOn
import foo.bar.example.foreapollokt.api.fruits.FruitPojo
import foo.bar.example.foreapollokt.api.fruits.FruitService
import foo.bar.example.foreapollokt.api.fruits.FruitsCustomError
import foo.bar.example.foreapollokt.message.UserMessage
import java.util.Random


/**
 * gets a list of fruit from the network, selects one at random to be currentFruit
 */
class FruitFetcher(
        private val fruitService: FruitService,
        private val callProcessor: CallProcessor<UserMessage>,
        private val logger: Logger,
        private val workMode: WorkMode
) : Observable by ObservableImp(workMode, logger) {

    var isBusy: Boolean = false
        private set
    var currentFruit = FruitPojo("(fruitless)", false, 0)
        private set


    fun fetchFruitsAsync(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchFruitsAsync() t:" + Thread.currentThread())

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

                fruitService.getFruitsSimulateOk()
            }

            when (val result = deferredResult.await()) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b)
            }
        }

    }


    /**
     * identical to fetchFruitsAsync() but for demo purposes the URL we point to will give us an error,
     * we also don't specify a custom error class here
     */
    fun fetchFruitsButFail(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchFruitsButFail()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()


        launchMain(workMode) {

            val result = callProcessor.processCallAwait {
                fruitService.getFruitsSimulateNotAuthorised()
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b)
            }
        }
    }


    /**
     * identical to fetchFruitsAsync() but for demo purposes the URL we point to will give us an error,
     * here we specify a custom error class for more detail about the error than just an HTTP code can give us
     */
    fun fetchFruitsButFailAdvanced(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i("fetchFruitsButFailAdvanced()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            val result = callProcessor.processCallAwait(FruitsCustomError::class.java) {
                fruitService.getFruitsSimulateNotAuthorised()
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b)
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

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            val result = callProcessor.processCallAwait(
                FruitsCustomError::class.java
            ) {
                var ticketRef = ""
                logger.i("...create user...")
                fruitService.createUser()
                    .carryOn {
                        logger.i("...create user ticket...")
                        fruitService.createUserTicket(it.userId)
                    }
                    .carryOn {
                        ticketRef = it.ticketRef
                        logger.i("...get waiting time...")
                        fruitService.getEstimatedWaitingTime(it.ticketRef)
                    }
                    .carryOn {
                        if (it.minutesWait > 10) {
                            logger.i("...cancel ticket...")
                            fruitService.cancelTicket(ticketRef)
                        } else {
                            logger.i("...confirm ticket...")
                            fruitService.confirmTicket(ticketRef)
                        }
                    }
                    .carryOn {
                        logger.i("...claim free fruit!...")
                        fruitService.claimFreeFruit(it.ticketRef)
                    }
            }

            when (result) {
                is Left -> handleFailure(failureWithPayload, result.a)
                is Right -> handleSuccess(success, result.b)
            }
        }

    }

    private fun handleSuccess(
            success: Success,
            successResponse: List<FruitPojo>
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread())

        currentFruit = selectRandomFruit(successResponse)
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

    private fun selectRandomFruit(listOfFruits: List<FruitPojo>): FruitPojo {
        return listOfFruits[if (listOfFruits.size == 1) 0 else random.nextInt(listOfFruits.size - 1)]
    }

    companion object {
        private val random = Random()
    }
}
