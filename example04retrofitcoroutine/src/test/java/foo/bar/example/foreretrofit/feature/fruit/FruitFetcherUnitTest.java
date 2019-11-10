package foo.bar.example.foreretrofit.feature.fruit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallback;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;
import co.early.fore.core.observer.Observer;
import co.early.fore.retrofit.CallProcessor;
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo;
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitService;
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher;
import foo.bar.example.foreretrofitcoroutine.message.UserMessage;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Tests for this model cover a few areas:
 * <p>
 * 1) Construction: we check that the model is constructed in the correct state
 * 2) Receiving data: we check that the model behaves appropriately when receiving various success and fail responses from the CallProcessor
 * 3) Observers and State: we check that the model updates its observers correctly and presents it's current state accurately
 *
 */
public class FruitFetcherUnitTest {

    public static final String TAG = FruitFetcherUnitTest.class.getSimpleName();

    private static Logger logger = new SystemLogger();
    private FruitPojo fruitPojo = new FruitPojo("strawberry", false, 71);

    private SuccessCallback mockSuccessCallback;
    private FailureCallbackWithPayload mockFailureCallbackWithPayload;
    private CallProcessor<UserMessage> mockCallProcessor;
    private FruitService mockFruitService;
    private Observer mockObserver;


    @Before
    public void setUp() throws Exception {
        mockSuccessCallback = mock(SuccessCallback.class);
        mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
        mockCallProcessor = mock(CallProcessor.class);
        mockFruitService = mock(FruitService.class);
        mockObserver = mock(Observer.class);
    }


    @Test
    public void initialConditions() throws Exception {

        //arrange
        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);

        //act

        //assert
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().getTastyPercentScore());
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().getIsCitrus());
    }


    @Test
    public void fetchFruit_MockSuccess() throws Exception {

        //arrange
        new StateBuilder(mockCallProcessor).getFruitSuccess(fruitPojo);

        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallback, times(1)).success();
        verify(mockFailureCallbackWithPayload, never()).fail(any());
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(fruitPojo.getName(), fruitFetcher.getCurrentFruit().getName());
        Assert.assertEquals(fruitPojo.getIsCitrus(), fruitFetcher.getCurrentFruit().getIsCitrus());
        Assert.assertEquals(fruitPojo.getTastyPercentScore(), fruitFetcher.getCurrentFruit().getTastyPercentScore());
    }


    @Test
    public void fetchFruit_MockFailure() throws Exception {

        //arrange
        new StateBuilder(mockCallProcessor).getFruitFail(UserMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT);

        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


        //assert
        verify(mockSuccessCallback, never()).success();
        verify(mockFailureCallbackWithPayload, times(1)).fail(eq(UserMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT));
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().getIsCitrus());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().getTastyPercentScore());
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
        new StateBuilder(mockCallProcessor).getFruitSuccess(fruitPojo);

        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);
        fruitFetcher.addObserver(mockObserver);


        //act
        fruitFetcher.fetchFruits(mockSuccessCallback, mockFailureCallbackWithPayload);


        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


}
