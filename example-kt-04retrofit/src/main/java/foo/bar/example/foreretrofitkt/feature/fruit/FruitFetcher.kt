package foo.bar.example.foreretrofitkt.feature.fruit

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.Either.Left
import co.early.fore.kt.core.Either.Right
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.coroutine.awaitMain
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.net.retrofit2.CallProcessorRetrofit2
import co.early.fore.kt.net.retrofit2.carryOn
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.api.fruits.FruitService
import foo.bar.example.foreretrofitkt.api.fruits.FruitsCustomError
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import java.util.*


/**
 * gets a list of fruit from the network, selects one at random to be currentFruit
 */
class FruitFetcher(
    private val fruitService: FruitService,
    private val retrofit2CallProcessor: CallProcessorRetrofit2<ErrorMessage>,
    private val logger: Logger
) : Observable by ObservableImp(logger = logger) {

    var isBusy: Boolean = false
        private set
    var currentFruit = FruitPojo("(fruitless)", false, 0)
        private set


    fun fetchFruitsAsync(
        success: Success,
        failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchFruitsAsync() t:" + Thread.currentThread())

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain {

            logger.i("about to use CallProcessor t:" + Thread.currentThread())

            val deferredResult = retrofit2CallProcessor.processCallAsync {

                logger.i("processing call t:" + Thread.currentThread())

                fruitService.getFruitsSimulateOk()
            }

            awaitMain {
                when (val result = deferredResult.await()) {
                    is Left -> handleFailure(failureWithPayload, result.a)
                    is Right -> handleSuccess(success, result.b)
                }
            }
        }

    }


    /**
     * identical to fetchFruitsAsync() but for demo purposes the URL we point to will give us an error,
     * we also don't specify a custom error class here
     */
    fun fetchFruitsButFail(
        success: Success,
        failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchFruitsButFail()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()


        launchMain {

            val result = retrofit2CallProcessor.processCallAwait {
                fruitService.getFruitsSimulateNotAuthorised()
            }

            awaitMain {
                when (result) {
                    is Left -> handleFailure(failureWithPayload, result.a)
                    is Right -> handleSuccess(success, result.b)
                }
            }
        }
    }


    /**
     * identical to fetchFruitsAsync() but for demo purposes the URL we point to will give us an error,
     * here we specify a custom error class for more detail about the error than just an HTTP code can give us
     */
    fun fetchFruitsButFailAdvanced(
        success: Success,
        failureWithPayload: FailureWithPayload<ErrorMessage>
    ) {

        logger.i("fetchFruitsButFailAdvanced()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain {

            val result = retrofit2CallProcessor.processCallAwait(FruitsCustomError::class.java) {
                fruitService.getFruitsSimulateNotAuthorised()
            }

            awaitMain {
                when (result) {
                    is Left -> handleFailure(failureWithPayload, result.a)
                    is Right -> handleSuccess(success, result.b)
                }
            }
        }
    }


    /**
     * Demonstration of how to use carryOn to chain multiple connection requests together in a
     * simple way
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

        isBusy = true
        notifyObservers()

        launchMain {

            val result = retrofit2CallProcessor.processCallAwait(
                FruitsCustomError::class.java
            ) {

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

            awaitMain {
                when (result) {
                    is Left -> handleFailure(failureWithPayload, result.a)
                    is Right -> handleSuccess(success, result.b)
                }
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
        failureWithPayload: FailureWithPayload<ErrorMessage>,
        failureMessage: ErrorMessage
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
