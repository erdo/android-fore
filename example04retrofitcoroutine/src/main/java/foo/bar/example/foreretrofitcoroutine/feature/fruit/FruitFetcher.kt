package foo.bar.example.foreretrofitcoroutine.feature.fruit

import co.early.fore.core.WorkMode
import co.early.fore.core.callbacks.FailureWithPayload
import co.early.fore.core.callbacks.Success
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.ObservableImp
import co.early.fore.retrofit.coroutine.CallProcessor
import co.early.fore.retrofit.coroutine.carryOn
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitService
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitsCustomError
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import java.util.Random


/**
 * gets a list of fruit from the network, selects one at random to be currentFruit
 */
class FruitFetcher(
        private val fruitService: FruitService,
        private val callProcessor: CallProcessor<UserMessage>,
        private val logger: Logger,
        private val workMode: WorkMode
) : ObservableImp(workMode) {

    var isBusy: Boolean = false
        private set
    var currentFruit = FruitPojo("(fruitless)", false, 0)
        private set


    fun fetchFruits(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i(LOG_TAG, "fetchFruits()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        callProcessor.processCall(
            workMode,
            { successResponse -> handleSuccess(success, successResponse) },
            { failureMessage -> handleFailure(failureWithPayload, failureMessage) }) {
            fruitService.getFruitsSimulateOk()
        }
    }


    //just a demonstration of how to use carryOn
    fun fetchManyThings(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i(LOG_TAG, "fetchManyThings()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        callProcessor.processCall(
            workMode,
            FruitsCustomError::class.java,
            { successResponse -> handleSuccess(success, successResponse) },
            { failureMessage -> handleFailure(failureWithPayload, failureMessage) }) {

            var ticketRef = ""
            fruitService.createUser()
                .carryOn {
                    fruitService.createUserTicket(it.userId)
                }
                .carryOn {
                    ticketRef = it.ticketRef
                    fruitService.getEstimatedWaitingTime(it.ticketRef)
                }
                .carryOn {
                    if (it.minutesWait > 10) {
                        fruitService.cancelTicket(ticketRef)
                    } else {
                        fruitService.confirmTicket(ticketRef)
                    }
                }
                .carryOn {
                    fruitService.claimFreeFruit(it.ticketRef)
                }
        }
    }


    //identical to fetchFruits() but for demo purposes the URL we point to will give us an error, we also don't specify a custom error class here
    fun fetchFruitsButFail(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i(LOG_TAG, "fetchFruitsButFailBasic()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        callProcessor.processCall(
            workMode,
            { successResponse -> handleSuccess(success, successResponse) },
            { failureMessage -> handleFailure(failureWithPayload, failureMessage) }) {
            fruitService.getFruitsSimulateNotAuthorised()
        }
    }


    //identical to fetchFruits() but for demo purposes the URL we point to will give us an error,
    //here we specify a custom error class for more detail about the error than just an HTTP code can give us
    fun fetchFruitsButFailAdvanced(
            success: Success,
            failureWithPayload: FailureWithPayload<UserMessage>
    ) {

        logger.i(LOG_TAG, "fetchFruitsButFailBasic()")

        if (isBusy) {
            failureWithPayload(UserMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        callProcessor.processCall(
            workMode, FruitsCustomError::class.java,
            { successResponse -> handleSuccess(success, successResponse) },
            { failureMessage -> handleFailure(failureWithPayload, failureMessage) }) {
            fruitService.getFruitsSimulateNotAuthorised()
        }
    }

    private fun handleSuccess(
            success: Success,
            successResponse: List<FruitPojo>
    ) {
        currentFruit = selectRandomFruit(successResponse)
        success()
        complete()
    }

    private fun handleFailure(
            failureWithPayload: FailureWithPayload<UserMessage>,
            failureMessage: UserMessage
    ) {
        failureWithPayload(failureMessage)
        complete()
    }

    private fun complete() {

        logger.i(LOG_TAG, "complete()")

        isBusy = false
        notifyObservers()
    }

    private fun selectRandomFruit(listOfFruits: List<FruitPojo>): FruitPojo {
        return listOfFruits[if (listOfFruits.size == 1) 0 else random.nextInt(listOfFruits.size - 1)]
    }

    companion object {
        val LOG_TAG = FruitFetcher::class.java.simpleName
        private val random = Random()
    }
}
