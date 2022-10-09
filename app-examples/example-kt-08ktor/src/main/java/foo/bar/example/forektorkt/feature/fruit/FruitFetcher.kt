package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.carryOn
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.eitherError
import co.early.fore.kt.core.eitherSuccess
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.core.type.Either.Fail
import co.early.fore.kt.core.type.Either.Success
import co.early.fore.kt.core.type.carryOn
import co.early.fore.kt.net.ktor.CallWrapperKtor
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.api.fruits.FruitService
import foo.bar.example.forektorkt.api.fruits.FruitsCustomError
import foo.bar.example.forektorkt.message.ErrorMessage
import java.util.Random

typealias SuccessCallback = () -> Unit
typealias FailureCallback<T> = (T) -> Unit

/**
 * gets a list of fruit from the network, selects one at random to be currentFruit
 */
class FruitFetcher(
    private val fruitService: FruitService,
    private val callWrapperKtor: CallWrapperKtor<ErrorMessage>,
    private val logger: Logger
) : Observable by ObservableImp(logger = logger) {

    var isBusy: Boolean = false
        private set
    var currentFruit = FruitPojo("(fruitless)", false, 0)
        private set


    fun fetchFruitsAsync(
            successCallBack: SuccessCallback,
            failureWithPayload: FailureCallback<ErrorMessage>
    ) {

        logger.i("fetchFruitsAsync() t:" + Thread.currentThread())

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain {

            logger.i("about to use CallWrapper t:" + Thread.currentThread())

            val deferredResult = callWrapperKtor.processCallAsync {

                logger.i("processing call t:" + Thread.currentThread())

                fruitService.getFruitsSimulateOk()
            }

            when (val result = deferredResult.await()) {
                is Fail -> handleFailure(failureWithPayload, result.value)
                is Success -> handleSuccess(successCallBack, result.value)
            }
        }

    }


    /**
     * identical to fetchFruitsAsync() but for demo purposes the URL we point to will give us an error,
     * we also don't specify a custom error class here
     */
    fun fetchFruitsButFail(
            successCallBack: SuccessCallback,
            failureWithPayload: FailureCallback<ErrorMessage>
    ) {

        logger.i("fetchFruitsButFail()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain {

            val result = callWrapperKtor.processCallAwait {
                fruitService.getFruitsSimulateNotAuthorised()
            }

            when (result) {
                is Fail -> handleFailure(failureWithPayload, result.value)
                is Success -> handleSuccess(successCallBack, result.value)
            }
        }
    }


    /**
     * identical to fetchFruitsAsync() but for demo purposes the URL we point to will give us an error,
     * here we specify a custom error class for more detail about the error than just an HTTP code can give us
     */
    fun fetchFruitsButFailAdvanced(
            successCallBack: SuccessCallback,
            failureWithPayload: FailureCallback<ErrorMessage>
    ) {

        logger.i("fetchFruitsButFailAdvanced()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain {

            val result = callWrapperKtor.processCallAwait(FruitsCustomError::class) {
                fruitService.getFruitsSimulateNotAuthorised()
            }

            when (result) {
                is Fail -> handleFailure(failureWithPayload, result.value)
                is Success -> handleSuccess(successCallBack, result.value)
            }
        }
    }


    /**
     * Demonstration of how to use carryOn to chain multiple connection requests together in a
     * simple way
     */
    fun chainedCall(
            successCallBack: SuccessCallback,
            failureWithPayload: FailureCallback<ErrorMessage>
    ) {

        logger.i("chainedCall()")

        if (isBusy) {
            failureWithPayload(ErrorMessage.ERROR_BUSY)
            return
        }

        isBusy = true
        notifyObservers()

        launchMain {

            var ticketRef = ""

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
            val response = callWrapperKtor.processCallAwait {
                logger.i("...create user...")
                fruitService.createUser()
            }.carryOn {
                logger.i("...create user ticket...")
                callWrapperKtor.processCallAwait {
                    fruitService.createUserTicket(it.userId)
                }
            }.carryOn {
                ticketRef = it.ticketRef
                logger.i("...get waiting time...")
                callWrapperKtor.processCallAwait {
                    fruitService.getEstimatedWaitingTime(it.ticketRef)
                }
            }.carryOn {
                if (it.minutesWait > 10) {
                    logger.i("...cancel ticket...")
                    callWrapperKtor.processCallAwait {
                        fruitService.cancelTicket(ticketRef)
                    }
                } else {
                    logger.i("...confirm ticket...")
                    callWrapperKtor.processCallAwait {
                        fruitService.confirmTicket(ticketRef)
                    }
                }
            }.carryOn {
                logger.i("...claim free fruit!...")
                callWrapperKtor.processCallAwait {
                    fruitService.claimFreeFruit(it.ticketRef)
                }
            }

            when (response) {
                is Fail -> handleFailure(failureWithPayload, response.value)
                is Success -> handleSuccess(successCallBack, response.value)
            }
        }
    }

    private fun handleSuccess(
            successCallBack: SuccessCallback,
            successResponse: List<FruitPojo>
    ) {

        logger.i("handleSuccess() t:" + Thread.currentThread())

        currentFruit = selectRandomFruit(successResponse)
        successCallBack()
        complete()
    }

    private fun handleFailure(
            failureWithPayload: FailureCallback<ErrorMessage>,
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
