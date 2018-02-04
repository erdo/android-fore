package foo.bar.example.asafretrofit.feature.fruit;

import java.util.List;
import java.util.Random;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.api.fruits.FruitsCustomError;
import foo.bar.example.asafretrofit.message.UserMessage;

/**
 * gets a list of fruit from the network, selects one at random to be currentFruit
 */
public class FruitFetcher extends ObservableImp{

    public static final String TAG = FruitFetcher.class.getSimpleName();

    private final FruitService fruitService;
    private final CallProcessor<UserMessage> callProcessor;
    private final WorkMode workMode;
    private final Logger logger;
    private static Random random = new Random();

    private boolean busy;
    private FruitPojo currentFruit = new FruitPojo("(fruitless)", false, 0);

    public FruitFetcher(FruitService fruitService, CallProcessor<UserMessage> callProcessor, Logger logger, WorkMode workMode) {
        super(workMode);
        this.fruitService = Affirm.notNull(fruitService);
        this.callProcessor = Affirm.notNull(callProcessor);
        this.logger = Affirm.notNull(logger);
        this.workMode = Affirm.notNull(workMode);
    }


    public void fetchFruits(final SuccessCallBack successCallBack, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "fetchFruits()");

        Affirm.notNull(successCallBack);
        Affirm.notNull(failureCallbackWithPayload);

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateOk("3s"), workMode, FruitsCustomError.class,
                successResponse -> FruitFetcher.this.handleSuccess(successCallBack, successResponse),
                failureMessage -> handleFailure(failureCallbackWithPayload, failureMessage));

    }


    //identical to fetchFruits() but for demo purposes the URL we point to will give us an error, we also don't specify a custom error class here
    public void fetchFruitsButFailBasic(final SuccessCallBack successCallBack, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "fetchFruitsButFailBasic()");

        Affirm.notNull(successCallBack);
        Affirm.notNull(failureCallbackWithPayload);

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateNotAuthorised("3s"), workMode,
                successResponse -> handleSuccess(successCallBack, successResponse),
                failureMessage -> handleFailure(failureCallbackWithPayload, failureMessage));

    }


    //identical to fetchFruits() but for demo purposes the URL we point to will give us an error
    public void fetchFruitsButFailAdvanced(final SuccessCallBack successCallBack, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        logger.i(TAG, "fetchFruitsButFailAdvanced()");

        Affirm.notNull(successCallBack);
        Affirm.notNull(failureCallbackWithPayload);

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateNotAuthorised("3s"), workMode, FruitsCustomError.class,
                successResponse -> handleSuccess(successCallBack, successResponse),
                failureMessage -> handleFailure(failureCallbackWithPayload, failureMessage));

    }

    private void handleSuccess(SuccessCallBack successCallBack, List<FruitPojo> successResponse){
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
