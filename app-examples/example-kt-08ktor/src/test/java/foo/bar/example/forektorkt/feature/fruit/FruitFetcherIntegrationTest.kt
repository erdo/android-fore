package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallWrapperKtor
import co.early.fore.kt.net.testhelpers.InterceptorStubOkHttp3
import co.early.fore.kt.net.testhelpers.Stub
import foo.bar.example.forektorkt.api.CommonServiceFailures
import foo.bar.example.forektorkt.api.CustomGlobalErrorHandler
import foo.bar.example.forektorkt.api.CustomKtorBuilder
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.api.fruits.FruitService
import foo.bar.example.forektorkt.message.ErrorMessage
import io.ktor.client.*
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * This is a slightly more end-to-end style of test, but without actually connecting to a network
 *
 * Using [InterceptorStubOkHttp3] we replace the server response with a canned response taken
 * from static text files saved in /resources. This all happens in OkHttp land so the model
 * under test is not aware of any difference.
 */
class FruitFetcherIntegrationTest {

    private val logger = SystemLogger()
    private val interceptorLogging = InterceptorLogging(logger)
    private val callWrapper = CallWrapperKtor(
        errorHandler = CustomGlobalErrorHandler(logger),
        logger = logger
    )

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
        val httpClient = stubbedHttpClient(stubbedSuccess)
        val fruitFetcher = FruitFetcher(
            FruitService.create(httpClient),
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
        val httpClient = stubbedHttpClient(stubbedFailUserLocked)
        val fruitFetcher = FruitFetcher(
            FruitService.create(httpClient),
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
        val httpClient = stubbedHttpClient(stubbedFailureUserNotEnabled)
        val fruitFetcher = FruitFetcher(
            FruitService.create(httpClient),
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
            val httpClient = stubbedHttpClient(stub)
            val fruitFetcher = FruitFetcher(
                FruitService.create(httpClient),
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

    private fun stubbedHttpClient(stub: Stub<*>): HttpClient {
        return CustomKtorBuilder.create(
            interceptorLogging,
            InterceptorStubOkHttp3(stub),
        )
    }

    companion object {

        private val stubbedSuccess = Stub(
            httpCode = 201, //stubbed HTTP code
            bodyContentResourceFileName = "fruit/success.json", //stubbed body response
            headers = listOf(Stub.Header("Content-Type", "application/json")), //headers
            expectedResult = FruitPojo("orange", true, 43) //expected result
        )

        private val stubbedFailUserLocked = Stub(
            httpCode = 401, //stubbed HTTP code
            bodyContentResourceFileName = "common/error_user_locked.json", //stubbed body response
            headers = listOf(Stub.Header("Content-Type", "application/json")), //headers
            expectedResult = ErrorMessage.ERROR_FRUIT_USER_LOCKED //expected result
        )

        private val stubbedFailureUserNotEnabled = Stub(
            httpCode = 401, //stubbed HTTP code
            bodyContentResourceFileName = "common/error_user_not_enabled.json", //stubbed body response
            headers = listOf(Stub.Header("Content-Type", "application/json")), //headers
            expectedResult = ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED //expected result
        )
    }
}
