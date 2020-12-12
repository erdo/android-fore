package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.WorkMode
import co.early.fore.core.testhelpers.CountDownLatchWrapper.runInBatch
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


/**
 * This is a slightly more end-to-end style of test, but without actually connecting to a network
 *
 * Using [InterceptorStubbedService] we
 * replace the server response with a canned response taken from static text files saved
 * in /resources. This all happens in OkHttp land so the model under test is not aware of any
 * difference.
 *
 * Apollo doesn't let us work in single thread mode for tests, so we need to use count down
 * latches unfortunately (we use fore's CountDownLatchWrapper class to get rid of some of the
 * boiler plate)
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
     * containing launches
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchLaunch_Success() {

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


        // we're expecting two observer notifications during this process so we'll use that
        // to count down the latch, that makes our test code brittle unfortunately:
        // https://erdo.github.io/android-fore/05-extras.html#notification-counting
        // it's the best we can do for junit testing Apollo though
        runInBatch(2, launchFetcher) {
            //act
            launchFetcher.fetchLaunchesAsync(mockSuccess, mockFailureWithPayload)
        }


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
     * Here we are making sure that the model correctly handles a custom server response indicating
     * "server says no"
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchLaunch_Fail_SaysNo() {

        //arrange
        val apolloClient = stubbedApolloClient(stubbedFailSaysNo)
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


        //act
        runInBatch(2, launchFetcher) {
            launchFetcher.fetchLaunchesButFailAdvanced(mockSuccess, mockFailureWithPayload)
        }


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailSaysNo.expectedResult))
        }
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(0, launchFetcher.currentLaunch.tastyPercentScore.toLong())
    }

    /**
     * Here we are making sure that the model correctly handles a server response indicating
     * that the server has had an internal server error
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchLaunch_Fail_InternalServer() {

        //arrange
        val apolloClient = stubbedApolloClient(stubbedFailureInternalServerError)
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


        //act
        runInBatch(2, launchFetcher) {
            launchFetcher.fetchLaunchesButFailAdvanced(mockSuccess, mockFailureWithPayload)
        }


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailureInternalServerError.expectedResult))
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
    fun fetchLaunch_CommonFailures() {

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
            val apolloClient = stubbedApolloClient(stubbedServiceDefinition)
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


            //act
            runInBatch(2, launchFetcher) {
                launchFetcher.fetchLaunchesAsync(mockSuccess, mockFailureWithPayload)
            }

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
                "launches/success.json", //stubbed body response
                Launch("109", "Site 40") //expected result
        )


        private val stubbedFailSaysNo = StubbedServiceDefinition(
                200, //stubbed HTTP code
                "launches/error_launch_service_says_no.json", //stubbed body response
                ErrorMessage.LAUNCH_SERVICE_SAYS_NO_ERROR //expected result
        )

        private val stubbedFailureInternalServerError = StubbedServiceDefinition(
                200, //stubbed HTTP code - all 200 because GraphQL
                "common/error_internal_server.json", //stubbed body response
                ErrorMessage.INTERNAL_SERVER_ERROR  //expected result
        )
    }

}
