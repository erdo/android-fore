package foo.bar.example.asafretrofit.feature.fruit;

import java.util.List;
import java.util.Random;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.callbacks.SuccessCallbackWithPayload;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.api.fruits.FruitsCustomError;
import foo.bar.example.asafretrofit.message.UserMessage;

/**
 * gets fruit from the network
 */
public class FruitFetcher extends ObservableImp{

    public static final String TAG = FruitFetcher.class.getSimpleName();

    private final FruitService fruitService;
    private final CallProcessor<UserMessage> callProcessor;
    private final WorkMode workMode;
    private final Logger logger;
    private static Random random = new Random();

    private boolean busy;
    private FruitPojo currentFruit = new FruitPojo("fruitless", false, 0);

    public FruitFetcher(FruitService fruitService, CallProcessor<UserMessage> callProcessor, Logger logger, WorkMode workMode) {
        super(workMode);
        this.fruitService = Affirm.notNull(fruitService);
        this.callProcessor = Affirm.notNull(callProcessor);
        this.logger = Affirm.notNull(logger);
        this.workMode = Affirm.notNull(workMode);
    }


    public void fetchFruits(final SuccessCallBack successCallBack, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateOk(), workMode,
                new SuccessCallbackWithPayload<List<FruitPojo>>() {
                    @Override
                    public void success(List<FruitPojo> successResponse) {
                        currentFruit = selectRandomFruit(successResponse);
                        complete();
                        successCallBack.success();
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        complete();
                        failureCallbackWithPayload.fail(failureMessage);
                    }
                });

    }


    public void fetchFruitsButFail(final SuccessCallBack successCallBack, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateNotAuthorised(), workMode,
                new SuccessCallbackWithPayload<List<FruitPojo>>() {
                    @Override
                    public void success(List<FruitPojo> successResponse) {
                        currentFruit = selectRandomFruit(successResponse);
                        complete();
                        successCallBack.success();
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        complete();
                        failureCallbackWithPayload.fail(failureMessage);
                    }
                });

    }


    public void fetchFruitsButFailGetCustomError(final SuccessCallBack successCallBack, final FailureCallbackWithPayload<UserMessage> failureCallbackWithPayload){

        if (busy){
            failureCallbackWithPayload.fail(UserMessage.ERROR_BUSY);
            return;
        }

        busy = true;
        notifyObservers();

        callProcessor.processCall(fruitService.getFruitsSimulateNotAuthorised(), workMode, FruitsCustomError.class,
                new SuccessCallbackWithPayload<List<FruitPojo>>() {
                    @Override
                    public void success(List<FruitPojo> successResponse) {
                        currentFruit = selectRandomFruit(successResponse);
                        complete();
                        successCallBack.success();
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        complete();
                        failureCallbackWithPayload.fail(failureMessage);
                    }
                });

    }


    public boolean isBusy() {
        return busy;
    }

    public FruitPojo getCurrentFruit() {
        return currentFruit;
    }

    private void complete(){
        busy = false;
        notifyObservers();
    }

    private FruitPojo selectRandomFruit(List<FruitPojo> listOfFruits){
        return listOfFruits.get(random.nextInt(listOfFruits.size()-1));
    }

}
