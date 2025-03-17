package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.core.delegate.Fore
import co.early.fore.core.delegate.TestDelegateDefault
import co.early.fore.core.logging.SystemLogger
import co.early.fore.net.PluginNetworkLogs
import co.early.fore.net.ktor.CallWrapperKtor
import co.early.fore.net.stub.PluginStubInterceptor
import co.early.fore.net.stub.Stub
import foo.bar.example.forektorkt.api.CommonServiceFailures
import foo.bar.example.forektorkt.api.GlobalErrorHandler
import foo.bar.example.forektorkt.api.KtorClientBuilder
import foo.bar.example.forektorkt.api.fruits.FruitPojo
import foo.bar.example.forektorkt.api.fruits.FruitService
import foo.bar.example.forektorkt.message.ErrorMessage
import io.ktor.client.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.headersOf
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
 * Using [PluginStubInterceptor] we replace the server response with a canned response taken
 * from static text files saved in /resources. This all happens in Ktor land so the model
 * under test is not aware of any difference.
 */
class FruitFetcherIntegrationTest {

    private val logger = SystemLogger()
    private val callWrapper = CallWrapperKtor(
        errorHandler = GlobalErrorHandler(logger),
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
            FruitService(httpClient),
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
            FruitService(httpClient),
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
            FruitService(httpClient),
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

        CommonServiceFailures.forEach { stub ->

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
                FruitService(httpClient),
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
        return KtorClientBuilder.create(
            configurePluginsAfter = {
                install(PluginNetworkLogs) {
                    curlStyleRequestLogs = false
                }
                install(PluginStubInterceptor) {
                    stubs = listOf({ _: HttpRequestBuilder -> true } to stub)
                }
            }
        )
    }

    companion object {

        private val stubbedSuccess = Stub(
            httpCode = 201, //stubbed HTTP code
            bodyContentResourceFileName = "fruit/success.json", //stubbed body response
            headers = headersOf("Content-Type" to listOf("application/json")), //headers
            expectedResult = FruitPojo("orange", true, 43) //expected result
        )

        private val stubbedFailUserLocked = Stub(
            httpCode = 401, //stubbed HTTP code
            bodyContentResourceFileName = "common/error_user_locked.json", //stubbed body response
            headers = headersOf("Content-Type", listOf("application/json")), //headers
            expectedResult = ErrorMessage.ERROR_FRUIT_USER_LOCKED //expected result
        )

        private val stubbedFailureUserNotEnabled = Stub(
            httpCode = 401, //stubbed HTTP code
            bodyContentResourceFileName = "common/error_user_not_enabled.json", //stubbed body response
            headers = headersOf("Content-Type", listOf( "application/json")), //headers
            expectedResult = ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED //expected result
        )
    }
}
