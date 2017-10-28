package foo.bar.example.asafretrofit.feature.fruit;

import org.junit.Assert;
import org.junit.Test;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.logging.SystemLogger;
import co.early.asaf.core.observer.Observer;
import co.early.asaf.retrofit.CallProcessor;
import co.early.asaf.retrofit.InterceptorLogging;
import co.early.asaf.retrofit.testhelpers.InterceptorStubbedService;
import co.early.asaf.retrofit.testhelpers.StubbedServiceDefinition;
import foo.bar.example.asafretrofit.api.CommonServiceFailures;
import foo.bar.example.asafretrofit.api.CustomGlobalErrorHandler;
import foo.bar.example.asafretrofit.api.CustomRetrofitBuilder;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.message.UserMessage;
import retrofit2.Retrofit;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class FruitFetcherTest {

    public static final String TAG = FruitFetcherTest.class.getSimpleName();

    private static final String OK = "OK";
    private static Logger logger = new SystemLogger();
    private static InterceptorLogging interceptorLogging = new InterceptorLogging(logger);
    CallProcessor<UserMessage> callProcessor = new CallProcessor<UserMessage>(
            new CustomGlobalErrorHandler(logger),
            logger);


    private static StubbedServiceDefinition<FruitPojo> stubbedSuccess = new StubbedServiceDefinition<>(
            200, //stubbed HTTP code
            "fruit/success.json", //stubbed body response
            new FruitPojo("orange", true, 43)); //expected result

    private static StubbedServiceDefinition<UserMessage> stubbedFailUserLocked = new StubbedServiceDefinition<>(
            401, //stubbed HTTP code
            "common/error_user_locked.json", //stubbed body response
            UserMessage.ERROR_FRUIT_USER_LOCKED); //expected result

    private static StubbedServiceDefinition<UserMessage> stubbedFailureUserNotEnabled = new StubbedServiceDefinition<>(
            401, //stubbed HTTP code
            "common/error_user_not_enabled.json", //stubbed body response
            UserMessage.ERROR_FRUIT_USER_NOT_ENABLED); //expected result



    @Test
    public void initialConditions() throws Exception {

        //arrange
        FruitFetcher fruitFetcher = new FruitFetcher(
                mock(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);

        //act

        //assert
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().isCitrus);
    }



    @Test
    public void fetchFruit_Success() throws Exception {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedSuccess);
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(
                new SuccessCallBack() {
                    @Override
                    public void success() {
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        Assert.fail("shouldn't be failing here 1" + failureMessage);
                    }
                });


        //assert
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(stubbedSuccess.expectedResult.name, fruitFetcher.getCurrentFruit().name);
        Assert.assertEquals(stubbedSuccess.expectedResult.isCitrus, fruitFetcher.getCurrentFruit().isCitrus);
        Assert.assertEquals(stubbedSuccess.expectedResult.tastyPercentScore, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    @Test
    public void fetchFruit_Fail_UserLocked() throws Exception {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedFailUserLocked);
        final String[] failureReported = new String[1];
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(
                new SuccessCallBack() {
                    @Override
                    public void success() {
                        Assert.fail("shouldn't be succeeding here 1");
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        failureReported[0] = OK;
                        Assert.assertEquals(stubbedFailUserLocked.expectedResult, failureMessage);
                    }
                });


        //assert
        Assert.assertEquals(OK, failureReported[0]);
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    @Test
    public void fetchFruit_Fail_UserNotEnabled() throws Exception {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedFailureUserNotEnabled);
        final String[] failureReported = new String[1];
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(
                new SuccessCallBack() {
                    @Override
                    public void success() {
                        Assert.fail("shouldn't be succeeding here 2");
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        failureReported[0] = OK;
                        Assert.assertEquals(stubbedFailureUserNotEnabled.expectedResult, failureMessage);
                    }
                });


        //assert
        Assert.assertEquals(OK, failureReported[0]);
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    /**
     * Here we are making sure that the model correctly handles common API failed responses
     *
     * @throws Exception
     */
    @Test
    public void fetchFruit_CommonFailures() throws Exception {

        for (StubbedServiceDefinition<UserMessage> stubbedServiceDefinition : new CommonServiceFailures()) {

            logger.i(TAG, "------- Common Service Failure: HTTP:"
                    + stubbedServiceDefinition.httpCode
                    + " res:" + stubbedServiceDefinition.resourceFileName
                    + " --------");

            //arrange
            Retrofit retrofit = stubbedRetrofit(stubbedServiceDefinition);
            final String[] failureReported = new String[1];
            FruitFetcher fruitFetcher = new FruitFetcher(
                    retrofit.create(FruitService.class),
                    callProcessor,
                    logger,
                    WorkMode.SYNCHRONOUS);


            //act
            fruitFetcher.fetchFruits(
                    new SuccessCallBack() {
                        @Override
                        public void success() {
                            Assert.fail("shouldn't be succeeding for common service failure");
                        }
                    },
                    new FailureCallbackWithPayload<UserMessage>() {
                        @Override
                        public void fail(UserMessage failureMessage) {
                            failureReported[0] = OK;
                            Assert.assertEquals(stubbedServiceDefinition.expectedResult, failureMessage);
                        }
                    });


            //assert
            Assert.assertEquals(OK, failureReported[0]);
            Assert.assertEquals(false, fruitFetcher.isBusy());
            Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
        }
    }



    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times the observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the databinding docs for more information about this
     *
     * @throws Exception
     */
    @Test
    public void observersNotifiedAtLeastOnce() throws Exception {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedSuccess);
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);
        Observer mockObserver = mock(Observer.class);
        fruitFetcher.addObserver(mockObserver);


        //act
        fruitFetcher.fetchFruits(
                new SuccessCallBack() {
                    @Override
                    public void success() {
                    }
                },
                new FailureCallbackWithPayload<UserMessage>() {
                    @Override
                    public void fail(UserMessage failureMessage) {
                        Assert.fail("shouldn't be failing here 3" + failureMessage);
                    }
                });


        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


    private Retrofit stubbedRetrofit(StubbedServiceDefinition stubbedServiceDefinition){
        return CustomRetrofitBuilder.create(
                new InterceptorStubbedService(stubbedServiceDefinition),
                interceptorLogging);
    }

}
