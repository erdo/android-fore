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
import co.early.fore.net.retrofit2.Retrofit2CallProcessor;
import foo.bar.example.foreretrofit.api.fruits.FruitPojo;
import foo.bar.example.foreretrofit.api.fruits.FruitService;
import foo.bar.example.foreretrofit.message.UserMessage;

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

    private static Logger logger = new SystemLogger();
    private FruitPojo fruitPojo = new FruitPojo("strawberry", false, 71);

    private SuccessCallback mockSuccessCallback;
    private FailureCallbackWithPayload mockFailureCallbackWithPayload;
    private Retrofit2CallProcessor<UserMessage> mockCallProcessor;
    private FruitService mockFruitService;
    private Observer mockObserver;


    @Before
    public void setUp() {
        mockSuccessCallback = mock(SuccessCallback.class);
        mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
        mockCallProcessor = mock(Retrofit2CallProcessor.class);
        mockFruitService = mock(FruitService.class);
        mockObserver = mock(Observer.class);
    }


    @Test
    public void initialConditions() {

        //arrange
        FruitFetcher fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS);

        //act

        //assert
        Assert.assertEquals(false, fruitFetcher.isBusy());
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().isCitrus);
    }


    @Test
    public void fetchFruit_MockSuccess() {

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
        Assert.assertEquals(fruitPojo.name, fruitFetcher.getCurrentFruit().name);
        Assert.assertEquals(fruitPojo.isCitrus, fruitFetcher.getCurrentFruit().isCitrus);
        Assert.assertEquals(fruitPojo.tastyPercentScore, fruitFetcher.getCurrentFruit().tastyPercentScore);
    }


    @Test
    public void fetchFruit_MockFailure() {

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
        Assert.assertEquals(false, fruitFetcher.getCurrentFruit().isCitrus);
        Assert.assertEquals(0, fruitFetcher.getCurrentFruit().tastyPercentScore);
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
     * See the reactive UI docs for more information about this
     *
     */
    @Test
    public void observersNotifiedAtLeastOnce() {

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
