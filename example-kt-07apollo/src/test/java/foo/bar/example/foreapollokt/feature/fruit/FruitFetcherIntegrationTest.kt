package foo.bar.example.foreapollokt.feature.fruit

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.apollo.CallProcessor
import co.early.fore.kt.apollo.InterceptorLogging
import co.early.fore.apollo.testhelpers.InterceptorStubbedService
import co.early.fore.apollo.testhelpers.StubbedServiceDefinition
import foo.bar.example.foreapollokt.api.CommonServiceFailures
import foo.bar.example.foreapollokt.api.CustomGlobalErrorHandler
import foo.bar.example.foreapollokt.api.CustomRetrofitBuilder
import foo.bar.example.foreapollokt.api.fruits.FruitPojo
import foo.bar.example.foreapollokt.api.fruits.FruitService
import foo.bar.example.foreapollokt.message.UserMessage
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
 *
 * Using [InterceptorStubbedService] we
 * replace the server response with a canned response taken from static text files saved
 * in /resources. This all happens in OkHttp land so the model under test is not aware of any
 * difference.
 *
 *
 * As usual for tests, we setup the [XCallProcessor] with [WorkMode.SYNCHRONOUS] so
 * that everything plays out in a single thread.
 *
 */
class FruitFetcherIntegrationTest {

    private val logger = SystemLogger()
    private val interceptorLogging = InterceptorLogging(logger)
    private val callProcessor = CallProcessor(CustomGlobalErrorHandler(logger), WorkMode.SYNCHRONOUS, logger)

    @MockK
    private lateinit var mockSuccess: Success

    @MockK
    private lateinit var mockFailureWithPayload: FailureWithPayload<UserMessage>


    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)


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
            callProcessor,
            logger,
            WorkMode.SYNCHRONOUS
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
        val retrofit = stubbedRetrofit(stubbedFailUserLocked)
        val fruitFetcher = FruitFetcher(
            retrofit.create(FruitService::class.java),
            callProcessor,
            logger,
            WorkMode.SYNCHRONOUS
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
        val retrofit = stubbedRetrofit(stubbedFailureUserNotEnabled)
        val fruitFetcher = FruitFetcher(
            retrofit.create(FruitService::class.java),
            callProcessor,
            logger,
            WorkMode.SYNCHRONOUS
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
                        + " --------"
            )

            //arrange
            clearMocks(mockSuccess, mockFailureWithPayload)
            val retrofit = stubbedRetrofit(stubbedServiceDefinition)
            val fruitFetcher = FruitFetcher(
                retrofit.create(FruitService::class.java),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
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


    private fun stubbedRetrofit(stubbedServiceDefinition: StubbedServiceDefinition<*>): Retrofit {
        return CustomRetrofitBuilder.create(
            InterceptorStubbedService(stubbedServiceDefinition),
            interceptorLogging
        )
    }

    companion object {

        private val stubbedSuccess = StubbedServiceDefinition(
            200, //stubbed HTTP code
            "fruit/success.json", //stubbed body response
            FruitPojo("orange", true, 43)
        ) //expected result

        private val stubbedFailUserLocked = StubbedServiceDefinition(
            401, //stubbed HTTP code
            "common/error_user_locked.json", //stubbed body response
            UserMessage.ERROR_FRUIT_USER_LOCKED
        ) //expected result

        private val stubbedFailureUserNotEnabled = StubbedServiceDefinition(
            401, //stubbed HTTP code
            "common/error_user_not_enabled.json", //stubbed body response
            UserMessage.ERROR_FRUIT_USER_NOT_ENABLED
        ) //expected result
    }

}
