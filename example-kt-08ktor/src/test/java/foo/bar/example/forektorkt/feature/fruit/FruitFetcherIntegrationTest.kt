package foo.bar.example.forektorkt.feature.fruit

import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallProcessorKtor
import co.early.fore.net.testhelpers.InterceptorStubbedService
import co.early.fore.net.testhelpers.StubbedServiceDefinition
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
 *
 * Using [InterceptorStubbedService] we
 * replace the server response with a canned response taken from static text files saved
 * in /resources. This all happens in OkHttp land so the model under test is not aware of any
 * difference.
 *
 */
class FruitFetcherIntegrationTest {

    private val logger = SystemLogger()
    private val interceptorLogging = InterceptorLogging(logger)
    private val callProcessor = CallProcessorKtor(
            globalErrorHandler = CustomGlobalErrorHandler(logger),
            logger = logger
    )

    @MockK
    private lateinit var mockSuccess: Success

    @MockK
    private lateinit var mockFailureWithPayload: FailureWithPayload<ErrorMessage>


    @Before
    fun setup() {

        ForeDelegateHolder.setDelegate(TestDelegateDefault())

        MockKAnnotations.init(this, relaxed = true)
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
                callProcessor,
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
        Assert.assertEquals(stubbedSuccess.expectedResult.name, fruitFetcher.currentFruit.name)
        Assert.assertEquals(stubbedSuccess.expectedResult.isCitrus, fruitFetcher.currentFruit.isCitrus)
        Assert.assertEquals(stubbedSuccess.expectedResult.tastyPercentScore.toLong(), fruitFetcher.currentFruit.tastyPercentScore.toLong())
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
                callProcessor,
                logger
        )


        //act
        fruitFetcher.fetchFruitsButFailAdvanced(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailUserLocked.expectedResult))
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
                callProcessor,
                logger
        )


        //act
        fruitFetcher.fetchFruitsButFailAdvanced(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailureUserNotEnabled.expectedResult))
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

        for (stubbedServiceDefinition in CommonServiceFailures()) {

            logger.i(
                    "------- Common Service Failure: HTTP:"
                            + stubbedServiceDefinition.httpCode
                            + " res:" + stubbedServiceDefinition.resourceFileName
                            + " expect:" + stubbedServiceDefinition.expectedResult
                            + " --------"
            )

            //arrange
            clearMocks(mockSuccess, mockFailureWithPayload)
            val httpClient = stubbedHttpClient(stubbedServiceDefinition)
            val fruitFetcher = FruitFetcher(
                    FruitService.create(httpClient),
                    callProcessor,
                    logger
            )


            //act
            fruitFetcher.fetchFruitsAsync(mockSuccess, mockFailureWithPayload)


            //assert
            verify(exactly = 0) {
                mockSuccess()
            }
            verify(exactly = 1) {
                mockFailureWithPayload(eq(stubbedServiceDefinition.expectedResult))
            }
            Assert.assertEquals(false, fruitFetcher.isBusy)
            Assert.assertEquals(0, fruitFetcher.currentFruit.tastyPercentScore.toLong())
        }
    }


    private fun stubbedHttpClient(stubbedServiceDefinition: StubbedServiceDefinition<*>): HttpClient {
        return CustomKtorBuilder.create(
                InterceptorStubbedService(stubbedServiceDefinition),
                interceptorLogging
        )
    }

    companion object {

        private val stubbedSuccess = StubbedServiceDefinition(
                201, //stubbed HTTP code
                "fruit/success.json", //stubbed body response
                "application/json; charset=UTF-8",
                FruitPojo("orange", true, 43) //expected result
        )

        private val stubbedFailUserLocked = StubbedServiceDefinition(
                401, //stubbed HTTP code
                "common/error_user_locked.json", //stubbed body response
                "application/json; charset=UTF-8",
                ErrorMessage.ERROR_FRUIT_USER_LOCKED //expected result
        )

        private val stubbedFailureUserNotEnabled = StubbedServiceDefinition(
                401, //stubbed HTTP code
                "common/error_user_not_enabled.json", //stubbed body response
                "application/json; charset=UTF-8",
                ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED //expected result
        )
    }
}
