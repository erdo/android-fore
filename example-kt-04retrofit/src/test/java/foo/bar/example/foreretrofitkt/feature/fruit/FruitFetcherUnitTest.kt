package foo.bar.example.foreretrofitkt.feature.fruit

import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.api.fruits.FruitService
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test


/**
 * Tests for this model cover a few areas:
 *
 *
 * 1) Construction: we check that the model is constructed in the correct state
 * 2) Receiving data: we check that the model behaves appropriately when receiving various success and fail responses from the CallProcessor
 * 3) Observers and State: we check that the model updates its observers correctly and presents its current state accurately
 *
 */
class FruitFetcherUnitTest {

    private val fruitPojo = FruitPojo("strawberry", false, 71)

    @MockK
    private lateinit var mockSuccess: SuccessCallback

    @MockK
    private lateinit var mockFailureWithPayload: FailureCallback<ErrorMessage>

    @MockK
    private lateinit var mockCallProcessorRetrofit2: co.early.fore.kt.net.retrofit2.CallProcessorRetrofit2<ErrorMessage>

    @MockK
    private lateinit var mockFruitService: FruitService

    @MockK
    private lateinit var mockObserver: Observer


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        Fore.setDelegate(TestDelegateDefault())
    }


    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val fruitFetcher = FruitFetcher(
            mockFruitService,
            mockCallProcessorRetrofit2,
            logger
        )

        //act

        //assert
        Assert.assertEquals(false, fruitFetcher.isBusy)
        Assert.assertEquals(0, fruitFetcher.currentFruit.tastyPercentScore.toLong())
        Assert.assertEquals(false, fruitFetcher.currentFruit.isCitrus)
    }


    @Test
    @Throws(Exception::class)
    fun fetchFruit_MockSuccess() {

        //arrange
        StateBuilder(mockCallProcessorRetrofit2).getFruitSuccess(fruitPojo)
        val fruitFetcher = FruitFetcher(
            mockFruitService,
            mockCallProcessorRetrofit2,
            logger
        )


        //act
        fruitFetcher.fetchFruitsAsync(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 1) {
            mockSuccess()
        }
        verify(exactly = 0) {
            mockFailureWithPayload(any())
        }
        Assert.assertEquals(false, fruitFetcher.isBusy)
        Assert.assertEquals(fruitPojo.name, fruitFetcher.currentFruit.name)
        Assert.assertEquals(fruitPojo.isCitrus, fruitFetcher.currentFruit.isCitrus)
        Assert.assertEquals(
            fruitPojo.tastyPercentScore.toLong(),
            fruitFetcher.currentFruit.tastyPercentScore.toLong()
        )
    }


    @Test
    @Throws(Exception::class)
    fun fetchFruit_MockFailure() {

        //arrange
        StateBuilder(mockCallProcessorRetrofit2).getFruitFail(ErrorMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT)
        val fruitFetcher = FruitFetcher(
            mockFruitService,
            mockCallProcessorRetrofit2,
            logger
        )


        //act
        fruitFetcher.fetchFruitsButFailAdvanced(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(ErrorMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT))
        }
        Assert.assertEquals(false, fruitFetcher.isBusy)
        Assert.assertEquals(false, fruitFetcher.currentFruit.isCitrus)
        Assert.assertEquals(0, fruitFetcher.currentFruit.tastyPercentScore.toLong())
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
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnce() {

        //arrange
        StateBuilder(mockCallProcessorRetrofit2).getFruitSuccess(fruitPojo)
        val fruitFetcher = FruitFetcher(
            mockFruitService,
            mockCallProcessorRetrofit2,
            logger
        )
        fruitFetcher.addObserver(mockObserver)


        //act
        fruitFetcher.fetchFruitsAsync(mockSuccess, mockFailureWithPayload)


        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    companion object {
        private val logger = SystemLogger()
    }
}
