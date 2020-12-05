package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.WorkMode
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.apollo.ApolloCallProcessor
import co.early.fore.net.testhelpers.InterceptorStubbedService
import co.early.fore.net.testhelpers.StubbedServiceDefinition
import com.apollographql.apollo.ApolloClient
import foo.bar.example.foreapollokt.api.CommonServiceFailures
import foo.bar.example.foreapollokt.api.CustomApolloBuilder
import foo.bar.example.foreapollokt.api.CustomGlobalErrorHandler
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch


/**
 * This is a slightly more end-to-end style of test, but without actually connecting to a network
 *
 * Using [InterceptorStubbedService] we
 * replace the server response with a canned response taken from static text files saved
 * in /resources. This all happens in OkHttp land so the model under test is not aware of any
 * difference.
 *
 * Apollo doesn't let us work in single thread mode for tests, so we need to use count down
 * latches unfortunately
 *
 */
class LaunchFetcherIntegrationTest {

    private val logger = SystemLogger()
    private val interceptorLogging = InterceptorLogging(logger)
    private val callProcessor = ApolloCallProcessor(CustomGlobalErrorHandler(logger), logger)

    @MockK
    private lateinit var mockSuccess: Success

    @MockK
    private lateinit var mockFailureWithPayload: FailureWithPayload<ErrorMessage>


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        ForeDelegateHolder.setDelegate(TestDelegateDefault())
    }


    /**
     * Here we are making sure that the model correctly handles a successful server response
     * containing a list of launches
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchFruit_Success() {

        //arrange
        val apolloClient = stubbedApolloClient(stubbedSuccess)
        val launchFetcher = LaunchFetcher(
                launchService = LaunchService(
                        getLaunchList = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailGeneric = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailSpecific = { apolloClient.query(LaunchListQuery()) }
                ),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )
        val latchForApolloCalls = CountDownLatch(2)
        // we're expecting two observer notifications during this process so we'll use that
        // to count down the latch, that makes our test code brittle unfortunately:
        // https://erdo.github.io/android-fore/05-extras.html#notification-counting
        // it's the best we can do for junit testing Apollo though
        launchFetcher.addObserver(Observer { latchForApolloCalls.countDown() })


        //act
        launchFetcher.fetchLaunchesAsync(mockSuccess, mockFailureWithPayload)
        latchForApolloCalls.await()


        //assert
        verify(exactly = 1) {
            mockSuccess()
        }
        verify(exactly = 0) {
            mockFailureWithPayload(any())
        }
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(stubbedSuccess.expectedResult.isCitrus, launchFetcher.currentLaunch.isCitrus)
        Assert.assertEquals(stubbedSuccess.expectedResult.tastyPercentScore.toLong(), launchFetcher.currentLaunch.tastyPercentScore.toLong())
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
        val apolloClient = stubbedApolloClient(stubbedFailUserLocked)
        val launchFetcher = LaunchFetcher(
                launchService = LaunchService(
                        getLaunchList = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailGeneric = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailSpecific = { apolloClient.query(LaunchListQuery()) }
                ),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )
        val latchForApolloCalls = CountDownLatch(2)
        // we're expecting two observer notifications during this process so we'll use that
        // to count down the latch, that makes our test code brittle unfortunately:
        // https://erdo.github.io/android-fore/05-extras.html#notification-counting
        // it's the best we can do for junit testing Apollo though
        launchFetcher.addObserver(Observer { latchForApolloCalls.countDown() })


        //act
        launchFetcher.fetchLaunchesButFailAdvanced(mockSuccess, mockFailureWithPayload)
        latchForApolloCalls.await()


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailUserLocked.expectedResult))
        }
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(0, launchFetcher.currentLaunch.tastyPercentScore.toLong())
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
        val apolloClient = stubbedApolloClient(stubbedFailureUserNotEnabled)
        val launchFetcher = LaunchFetcher(
                launchService = LaunchService(
                        getLaunchList = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailGeneric = { apolloClient.query(LaunchListQuery()) },
                        getLaunchListFailSpecific = { apolloClient.query(LaunchListQuery()) }
                ),
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )
        val latchForApolloCalls = CountDownLatch(2)
        // we're expecting two observer notifications during this process so we'll use that
        // to count down the latch, that makes our test code brittle unfortunately:
        // https://erdo.github.io/android-fore/05-extras.html#notification-counting
        // it's the best we can do for junit testing Apollo though
        launchFetcher.addObserver(Observer { latchForApolloCalls.countDown() })


        //act
        launchFetcher.fetchLaunchesButFailAdvanced(mockSuccess, mockFailureWithPayload)
        latchForApolloCalls.await()


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailureUserNotEnabled.expectedResult))
        }
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(0, launchFetcher.currentLaunch.tastyPercentScore.toLong())
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
            val apolloClient = stubbedApolloClient(stubbedFailureUserNotEnabled)
            val launchFetcher = LaunchFetcher(
                    launchService = LaunchService(
                            getLaunchList = { apolloClient.query(LaunchListQuery()) },
                            getLaunchListFailGeneric = { apolloClient.query(LaunchListQuery()) },
                            getLaunchListFailSpecific = { apolloClient.query(LaunchListQuery()) }
                    ),
                    callProcessor,
                    logger,
                    WorkMode.SYNCHRONOUS
            )
            val latchForApolloCalls = CountDownLatch(2)
            // we're expecting two observer notifications during this process so we'll use that
            // to count down the latch, that makes our test code brittle unfortunately:
            // https://erdo.github.io/android-fore/05-extras.html#notification-counting
            // it's the best we can do for junit testing Apollo though
            launchFetcher.addObserver(Observer { latchForApolloCalls.countDown() })

            //act
            launchFetcher.fetchLaunchesAsync(mockSuccess, mockFailureWithPayload)
            latchForApolloCalls.await()

            //assert
            verify(exactly = 0) {
                mockSuccess()
            }
            verify(exactly = 1) {
                mockFailureWithPayload(eq(stubbedServiceDefinition.expectedResult))
            }
            Assert.assertEquals(false, launchFetcher.isBusy)
            Assert.assertEquals(0, launchFetcher.currentLaunch.tastyPercentScore.toLong())
        }
    }


    private fun stubbedApolloClient(stubbedServiceDefinition: StubbedServiceDefinition<*>): ApolloClient {
        return CustomApolloBuilder.create(
                InterceptorStubbedService(stubbedServiceDefinition),
                interceptorLogging
        )
    }

    companion object {

        private val stubbedSuccess = StubbedServiceDefinition(
                200, //stubbed HTTP code
                "fruit/success.json", //stubbed body response
                Launch("123", "orange", true, 43)
        ) //expected result

        private val stubbedFailUserLocked = StubbedServiceDefinition(
                401, //stubbed HTTP code
                "common/error_user_locked.json", //stubbed body response
                ErrorMessage.ERROR_FRUIT_USER_LOCKED
        ) //expected result

        private val stubbedFailureUserNotEnabled = StubbedServiceDefinition(
                401, //stubbed HTTP code
                "common/error_user_not_enabled.json", //stubbed body response
                ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED
        ) //expected result
    }

}
