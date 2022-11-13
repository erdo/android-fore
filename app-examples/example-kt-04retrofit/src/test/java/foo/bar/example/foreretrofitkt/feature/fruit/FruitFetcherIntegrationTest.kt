package foo.bar.example.foreretrofitkt.feature.fruit

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.retrofit2.CallWrapperRetrofit2
import co.early.fore.kt.net.testhelpers.InterceptorStubOkHttp3
import co.early.fore.kt.net.testhelpers.Stub
import foo.bar.example.foreretrofitkt.api.CommonServiceFailures
import foo.bar.example.foreretrofitkt.api.CustomGlobalErrorHandler
import foo.bar.example.foreretrofitkt.api.CustomRetrofitBuilder
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.api.fruits.FruitService
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

/**
 * This is a slightly more end-to-end style of test, but without actually connecting to a network
 *
 * Using [InterceptorStubOkHttp3] we replace the server response with a canned response taken
 * from static text files saved in /resources. This all happens in OkHttp land so the model
 * under test is not aware of any difference.
 *
 * As usual for tests, we setup the CallWrapper with [WorkMode.SYNCHRONOUS] so
 * that everything plays out in a single thread. Other examples use
 * [Fore.setDelegate(TestDelegateDefault())] to do the same thing
 */
class FruitFetcherIntegrationTest {

    private val logger = SystemLogger()
    private val interceptorLogging = InterceptorLogging(logger)
    private val callWrapper =
        CallWrapperRetrofit2(CustomGlobalErrorHandler(logger), WorkMode.SYNCHRONOUS, logger)

    @MockK
    private lateinit var mockSuccess: SuccessCallback

    @MockK
    private lateinit var mockFailureWithPayload: FailureCallback<ErrorMessage>

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        Fore.setDelegate(TestDelegateDefault())
    }

    /**
     * Here we are making sure that the model correctly handles a successful server response
     * containing a list of fruit
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchFruit_Success() {

        //arrange
        val retrofit = stubbedRetrofit(stubbedSuccess)
        val fruitFetcher = FruitFetcher(
            retrofit.create(FruitService::class.java),
            callWrapper,
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
        Assert.assertEquals(stubbedSuccess.expectedResult?.name, fruitFetcher.currentFruit.name)
        Assert.assertEquals(
            stubbedSuccess.expectedResult?.isCitrus,
            fruitFetcher.currentFruit.isCitrus
        )
        Assert.assertEquals(
            stubbedSuccess.expectedResult?.tastyPercentScore?.toLong(),
            fruitFetcher.currentFruit.tastyPercentScore.toLong()
        )
    }

    /**
     * Here we are making sure that the model correctly handles a server response indicating
     * that the user account has been locked
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchFruit_Fail_UserLocked() {

        //arrange
        val retrofit = stubbedRetrofit(stubbedFailUserLocked)
        val fruitFetcher = FruitFetcher(
            retrofit.create(FruitService::class.java),
            callWrapper,
            logger
        )

        //act
        fruitFetcher.fetchFruitsButFailAdvanced(mockSuccess, mockFailureWithPayload)

        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailUserLocked.expectedResult as ErrorMessage))
        }
        Assert.assertEquals(false, fruitFetcher.isBusy)
        Assert.assertEquals(0, fruitFetcher.currentFruit.tastyPercentScore.toLong())
    }

    /**
     * Here we are making sure that the model correctly handles a server response indicating
     * that the user account has not been enabled
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchFruit_Fail_UserNotEnabled() {

        //arrange
        val retrofit = stubbedRetrofit(stubbedFailureUserNotEnabled)
        val fruitFetcher = FruitFetcher(
            retrofit.create(FruitService::class.java),
            callWrapper,
            logger
        )

        //act
        fruitFetcher.fetchFruitsButFailAdvanced(mockSuccess, mockFailureWithPayload)

        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailureUserNotEnabled.expectedResult as ErrorMessage))
        }
        Assert.assertEquals(false, fruitFetcher.isBusy)
        Assert.assertEquals(0, fruitFetcher.currentFruit.tastyPercentScore.toLong())
    }

    /**
     * Here we are making sure that the model correctly handles common API failed responses
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchFruit_CommonFailures() {

        for (stub in CommonServiceFailures()) {

            logger.i(
                "------- Common Service Failure: HTTP:"
                        + stub.httpCode
                        + " res:" + stub.bodyContentResourceFileName
                        + " expect:" + stub.expectedResult
                        + " --------"
            )

            //arrange
            clearMocks(mockSuccess, mockFailureWithPayload)
            val retrofit = stubbedRetrofit(stub)
            val fruitFetcher = FruitFetcher(
                retrofit.create(FruitService::class.java),
                callWrapper,
                logger
            )

            //act
            fruitFetcher.fetchFruitsAsync(mockSuccess, mockFailureWithPayload)

            //assert
            verify(exactly = 0) {
                mockSuccess()
            }
            verify(exactly = 1) {
                mockFailureWithPayload(eq(stub.expectedResult as ErrorMessage))
            }
            Assert.assertEquals(false, fruitFetcher.isBusy)
            Assert.assertEquals(0, fruitFetcher.currentFruit.tastyPercentScore.toLong())
        }
    }

    private fun stubbedRetrofit(stubbedServiceDefinition: Stub<*>): Retrofit {
        return CustomRetrofitBuilder.create(
            interceptorLogging,
            InterceptorStubOkHttp3(stubbedServiceDefinition),
        )
    }

    companion object {

        private val stubbedSuccess = Stub(
            httpCode = 200, //stubbed HTTP code
            bodyContentResourceFileName = "fruit/success.json", //stubbed body response
            expectedResult = FruitPojo("orange", true, 43) //expected result
        )

        private val stubbedFailUserLocked = Stub(
            httpCode = 401, //stubbed HTTP code
            bodyContentResourceFileName = "common/error_user_locked.json", //stubbed body response
            expectedResult = ErrorMessage.ERROR_FRUIT_USER_LOCKED //expected result
        )

        private val stubbedFailureUserNotEnabled = Stub(
            httpCode = 401, //stubbed HTTP code
            bodyContentResourceFileName = "common/error_user_not_enabled.json", //stubbed body response
            expectedResult = ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED //expected result
        )
    }
}
