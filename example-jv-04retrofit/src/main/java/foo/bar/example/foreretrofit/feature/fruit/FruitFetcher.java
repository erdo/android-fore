package foo.bar.example.foreretrofit.feature.fruit;

import java.util.List;
import java.util.Random;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallback;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.net.retrofit2.CallProcessorRetrofit2;
import foo.bar.example.foreretrofit.api.fruits.FruitPojo;
import foo.bar.example.foreretrofit.api.fruits.FruitService;
import foo.bar.example.foreretrofit.api.fruits.FruitsCustomError;
import foo.bar.example.foreretrofit.message.UserMessage;

/**
 * gets a list of fruit from the network, selects one at random to be currentFruit
 */
public class FruitFetcher extends ObservableImp{

    public static final String TAG = FruitFetcher.class.getSimpleName();

    private final FruitService fruitService;
    private final CallProcessorRetrofit2<UserMessage> callProcessor;
    private final WorkMode workMode;
    private final Logger logger;
    private static Random random = new Random();

    private boolean busy;
    private FruitPojo currentFruit = new FruitPojo("(fruitless)", false, 0);

    public FruitFetcher(FruitService fruitService, CallProcessorRetrofit2<UserMessage> callProcessor, Logger logger, WorkMode workMode) {
        super(workMode);
        this.fruitService = Affirm.notNull(fruitService);
        this.callProcessor = Affirm.notNull(callProcessor);
        this.logger = Affirm.notNull(logger);
        this.workMode = Affirm.notNull(workMode);
    }


    public void fetchFruits(final SuccessCallback successCallback, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "fetchFruits()");

        Affirm.notNull(successCallback);
        Affirm.notNull(failureCallbackWithPayload);

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateOk("3s"), workMode, FruitsCustomError.class,
                successResponse -> handleSuccess(successCallback, successResponse),
                failureMessage -> handleFailure(failureCallbackWithPayload, failureMessage));

    }


    //identical to fetchFruits() but for demo purposes the URL we point to will give us an error, we also don't specify a custom error class here
    public void fetchFruitsButFailBasic(final SuccessCallback successCallback, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "fetchFruitsButFailBasic()");

        Affirm.notNull(successCallback);
        Affirm.notNull(failureCallbackWithPayload);

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateNotAuthorised("3s"), workMode,
                successResponse -> handleSuccess(successCallback, successResponse),
                failureMessage -> handleFailure(failureCallbackWithPayload, failureMessage));

    }


    //identical to fetchFruits() but for demo purposes the URL we point to will give us an error
    public void fetchFruitsButFailAdvanced(final SuccessCallback successCallback, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "fetchFruitsButFailAdvanced()");

        Affirm.notNull(successCallback);
        Affirm.notNull(failureCallbackWithPayload);

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateNotAuthorised("3s"), workMode, FruitsCustomError.class,
                successResponse -> handleSuccess(successCallback, successResponse),
                failureMessage -> handleFailure(failureCallbackWithPayload, failureMessage));

    }

    private void handleSuccess(SuccessCallback successCallBack, List<FruitPojo> successResponse){
        currentFruit = selectRandomFruit(successResponse);
        successCallBack.success();
        complete();
    }

    private void handleFailure(FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload, UserMessage failureMessage){
        failureCallbackWithPayload.fail(failureMessage);
        complete();
    }

    public boolean isBusy() {
        return busy;
    }

    public FruitPojo getCurrentFruit() {
        return currentFruit;
    }

    private void complete(){

        logger.i(TAG, "complete()");

        busy = false;
        notifyObservers();
    }

    private FruitPojo selectRandomFruit(List<FruitPojo> listOfFruits){
        return listOfFruits.get(listOfFruits.size() == 1 ? 0 : random.nextInt(listOfFruits.size()-1));
    }

}
