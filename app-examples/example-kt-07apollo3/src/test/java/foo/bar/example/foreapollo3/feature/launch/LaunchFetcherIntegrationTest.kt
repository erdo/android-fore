package foo.bar.example.foreapollo3.feature.launch

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.apollo3.CallWrapperApollo3
import co.early.fore.net.testhelpers.InterceptorStubbedService
import co.early.fore.net.testhelpers.StubbedServiceDefinition
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import foo.bar.example.foreapollo3.*
import foo.bar.example.foreapollo3.api.CommonServiceFailures
import foo.bar.example.foreapollo3.api.CustomApolloBuilder
import foo.bar.example.foreapollo3.api.CustomGlobalErrorHandler
import foo.bar.example.foreapollo3.feature.FailureCallback
import foo.bar.example.foreapollo3.feature.SuccessCallback
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.message.ErrorMessage
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
 */
@ExperimentalStdlibApi
class LaunchFetcherIntegrationTest {

    private val interceptorLogging = InterceptorLogging()
    private val logger = SystemLogger()
    private val callWrapper = CallWrapperApollo3(CustomGlobalErrorHandler(logger))

    @MockK
    private lateinit var mockSuccess: SuccessCallback

    @MockK
    private lateinit var mockAuthenticator: Authenticator

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
     * containing launches
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun fetchLaunch_Success() {

        //arrange
        val apolloClient = stubbedApolloClient(stubbedSuccess)
        val launchesModel = LaunchesModel(
            createLaunchService(apolloClient),
            callWrapper,
            mockAuthenticator,
            logger
        )


        //act
        launchesModel.fetchLaunches(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 1) {
            mockSuccess()
        }
        verify(exactly = 0) {
            mockFailureWithPayload(any())
        }
        Assert.assertEquals(false, launchesModel.isBusy)
        Assert.assertEquals(
            stubbedSuccess.expectedResult?.isBooked,
            launchesModel.currentLaunch.isBooked
        )
        Assert.assertEquals(stubbedSuccess.expectedResult?.id, launchesModel.currentLaunch.id)
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
        val launchesModel = LaunchesModel(
            createLaunchService(apolloClient),
            callWrapper,
            mockAuthenticator,
            logger
        )


        //act
        launchesModel.fetchLaunches(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailSaysNo.expectedResult as ErrorMessage))
        }
        Assert.assertEquals(false, launchesModel.isBusy)
        Assert.assertEquals(NO_ID, launchesModel.currentLaunch.id)
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
        val launchesModel = LaunchesModel(
            createLaunchService(apolloClient),
            callWrapper,
            mockAuthenticator,
            logger
        )


        //act
        launchesModel.fetchLaunches(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(stubbedFailureInternalServerError.expectedResult as ErrorMessage))
        }
        Assert.assertEquals(false, launchesModel.isBusy)
        Assert.assertEquals(NO_ID, launchesModel.currentLaunch.id)
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
            val launchesModel = LaunchesModel(
                createLaunchService(apolloClient),
                callWrapper,
                mockAuthenticator,
                logger
            )


            //act
            launchesModel.fetchLaunches(mockSuccess, mockFailureWithPayload)


            //assert
            verify(exactly = 0) {
                mockSuccess()
            }
            verify(exactly = 1) {
                mockFailureWithPayload(eq(stubbedServiceDefinition.expectedResult as ErrorMessage))
            }
            Assert.assertEquals(false, launchesModel.isBusy)
            Assert.assertEquals(NO_ID, launchesModel.currentLaunch.id)
        }
    }


    private fun stubbedApolloClient(stubbedServiceDefinition: StubbedServiceDefinition<*>): ApolloClient {
        return CustomApolloBuilder.create(
            InterceptorStubbedService(
                stubbedServiceDefinition
            ),
            interceptorLogging
        )
    }

    private fun createLaunchService(apolloClient: ApolloClient): LaunchService {
        return LaunchService(
            getLaunchList = { apolloClient.query(LaunchListQuery()).execute() },
            login = { email -> apolloClient.mutation(LoginMutation(Optional.Present(email))).execute() },
            refreshLaunchDetail = { id -> apolloClient.query(LaunchDetailsQuery(id)).execute() },
            bookTrip = { id -> apolloClient.mutation(BookTripMutation(id)).execute() },
            cancelTrip = { id -> apolloClient.mutation(CancelTripMutation(id)).execute() }
        )
    }

    companion object {

        private val stubbedSuccess =
            StubbedServiceDefinition(
                200, //stubbed HTTP code
                "launches/success.json", //stubbed body response
                Launch("109", "Site 40") //expected result
            )


        private val stubbedFailSaysNo =
            StubbedServiceDefinition(
                200, //stubbed HTTP code
                "launches/error_launch_service_says_no.json", //stubbed body response
                ErrorMessage.LAUNCH_SERVICE_SAYS_NO_ERROR //expected result
            )

        private val stubbedFailureInternalServerError =
            StubbedServiceDefinition(
                200, //stubbed HTTP code - all 200 because GraphQL
                "common/error_internal_server.json", //stubbed body response
                ErrorMessage.INTERNAL_SERVER_ERROR  //expected result
            )
    }
}
