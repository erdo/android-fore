package foo.bar.example.foreretrofit.feature.fruit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallback;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;
import co.early.fore.net.retrofit2.Retrofit2CallProcessor;
import co.early.fore.net.InterceptorLogging;
import co.early.fore.net.testhelpers.InterceptorStubbedService;
import co.early.fore.net.testhelpers.StubbedServiceDefinition;
import foo.bar.example.foreretrofit.api.CommonServiceFailures;
import foo.bar.example.foreretrofit.api.CustomGlobalErrorHandler;
import foo.bar.example.foreretrofit.api.CustomRetrofitBuilder;
import foo.bar.example.foreretrofit.api.fruits.FruitPojo;
import foo.bar.example.foreretrofit.api.fruits.FruitService;
import foo.bar.example.foreretrofit.message.UserMessage;
import retrofit2.Retrofit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This is a slightly more end-to-end style of test, but without actually connecting to a network
 * <p>
 * Using {@link InterceptorStubbedService} we
 * replace the server response with a canned response taken from static text files saved
 * in /resources. This all happens in OkHttp land so the model under test is not aware of any
 * difference.
 * <p>
 * As usual for tests, we setup the {@link Retrofit2CallProcessor} with {@link WorkMode#SYNCHRONOUS} so
 * that everything plays out in a single thread.
 *
 */
public class FruitFetcherIntegrationTest {

    public static final String TAG = FruitFetcherIntegrationTest.class.getSimpleName();

    private Logger logger = new SystemLogger();

    private SuccessCallback mockSuccessCallback;
    private FailureCallbackWithPayload mockFailureCallbackWithPayload;
    private InterceptorLogging interceptorLogging;
    private Retrofit2CallProcessor<UserMessage> callProcessor;


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


    @Before
    public void setup(){
        mockSuccessCallback = mock(SuccessCallback.class);
        mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
        interceptorLogging = new InterceptorLogging(logger);
        callProcessor = new Retrofit2CallProcessor<>(new CustomGlobalErrorHandler(logger), logger);
    }


    /**
     * Here we are making sure that the model correctly handles a successful server response
     * containing a list of fruit
     */
    @Test
    public void fetchFruit_Success() {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedSuccess);
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallback, times(1)).success();
        verify(mockFailureCallbackWithPayload, never()).fail(any());
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(stubbedSuccess.expectedResult.name, fruitFetcher.getCurrentFruit().name);
        Assert.assertEquals(stubbedSuccess.expectedResult.isCitrus, fruitFetcher.getCurrentFruit().isCitrus);
        Assert.assertEquals(stubbedSuccess.expectedResult.tastyPercentScore, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }

    /**
     * Here we are making sure that the model correctly handles a server response indicating
     * that the user account has been locked
     */
    @Test
    public void fetchFruit_Fail_UserLocked() {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedFailUserLocked);
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallback, never()).success();
        verify(mockFailureCallbackWithPayload, times(1)).fail(eq(stubbedFailUserLocked.expectedResult));
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }

    /**
     * Here we are making sure that the model correctly handles a server response indicating
     * that the user account has not been enabled
     */
    @Test
    public void fetchFruit_Fail_UserNotEnabled() {

        //arrange
        Retrofit retrofit = stubbedRetrofit(stubbedFailureUserNotEnabled);
        FruitFetcher fruitFetcher = new FruitFetcher(
                retrofit.create(FruitService.class),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallback, never()).success();
        verify(mockFailureCallbackWithPayload, times(1)).fail(eq(stubbedFailureUserNotEnabled.expectedResult));
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    /**
     * Here we are making sure that the model correctly handles common API failed responses
     */
    @Test
    public void fetchFruit_CommonFailures() {

        for (StubbedServiceDefinition<UserMessage> stubbedServiceDefinition : new CommonServiceFailures()) {

            logger.i(TAG, "------- Common Service Failure: HTTP:"
                    + stubbedServiceDefinition.httpCode
                    + " res:" + stubbedServiceDefinition.resourceFileName
                    + " --------");

            //arrange
            mockSuccessCallback = mock(SuccessCallback.class);
            mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
            Retrofit retrofit = stubbedRetrofit(stubbedServiceDefinition);
            FruitFetcher fruitFetcher = new FruitFetcher(
                    retrofit.create(FruitService.class),
                    callProcessor,
                    logger,
                    WorkMode.SYNCHRONOUS);


            //act
            fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


            //assert
            verify(mockSuccessCallback, never()).success();
            verify(mockFailureCallbackWithPayload, times(1)).fail(eq(stubbedServiceDefinition.expectedResult));
            Assert.assertEquals(false, fruitFetcher.isBusy());
            Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
        }
    }


    private Retrofit stubbedRetrofit(StubbedServiceDefinition stubbedServiceDefinition){
        return CustomRetrofitBuilder.create(
                new InterceptorStubbedService(stubbedServiceDefinition),
                interceptorLogging);
    }

}
