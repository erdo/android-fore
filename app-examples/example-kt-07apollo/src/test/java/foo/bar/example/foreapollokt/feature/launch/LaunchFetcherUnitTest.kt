package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.apollo.CallWrapperApollo
import foo.bar.example.foreapollokt.feature.FailureCallback
import foo.bar.example.foreapollokt.feature.SuccessCallback
import foo.bar.example.foreapollokt.feature.authentication.Authenticator
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
class LaunchesModelUnitTest {

    private val launch = Launch("123", "site", true, "http://www.test.com/someimage.png")

    @MockK
    private lateinit var mockSuccess: SuccessCallback

    @MockK
    private lateinit var mockFailureWithPayload: FailureCallback<ErrorMessage>

    @MockK
    private lateinit var mockCallWrapperApollo: CallWrapperApollo<ErrorMessage>

    @MockK
    private lateinit var mockLaunchService: LaunchService

    @MockK
    private lateinit var mockAuthenticator: Authenticator

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
        val launchesModel = LaunchesModel(
            mockLaunchService,
            mockCallWrapperApollo,
            mockAuthenticator,
            logger
        )

        //act

        //assert
        Assert.assertEquals(false, launchesModel.isBusy)
        Assert.assertEquals(NO_ID, launchesModel.currentLaunch.id)
        Assert.assertEquals(false, launchesModel.currentLaunch.isBooked)
    }


    @Test
    @Throws(Exception::class)
    fun fetchLaunch_MockSuccess() {

        val mockLaunchesData = createMockLaunchesResponse(launch)

        //arrange
        val callProcessor =
            StateBuilder().getLaunchSuccess(mockLaunchesData).mockCallWrapperApollo
        val launchesModel = LaunchesModel(
            mockLaunchService,
            callProcessor,
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
        Assert.assertEquals(launch.site, launchesModel.currentLaunch.site)
        Assert.assertEquals(launch.isBooked, launchesModel.currentLaunch.isBooked)
        Assert.assertEquals(launch.id, launchesModel.currentLaunch.id)
    }


    @Test
    @Throws(Exception::class)
    fun fetchLaunch_MockFailure() {

        //arrange

        val callProcessor =
            StateBuilder().getLaunchFail(ErrorMessage.INTERNAL_SERVER_ERROR).mockCallWrapperApollo
        val launchesModel = LaunchesModel(
            mockLaunchService,
            callProcessor,
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
            mockFailureWithPayload(eq(ErrorMessage.INTERNAL_SERVER_ERROR))
        }
        Assert.assertEquals(false, launchesModel.isBusy)
        Assert.assertEquals(false, launchesModel.currentLaunch.isBooked)
        Assert.assertEquals(NO_ID, launchesModel.currentLaunch.id)
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

        val mockLaunchesData = createMockLaunchesResponse(launch)

        //arrange
        val callWrapper =
            StateBuilder().getLaunchSuccess(mockLaunchesData).mockCallWrapperApollo
        val launchesModel = LaunchesModel(
            mockLaunchService,
            callWrapper,
            mockAuthenticator,
            logger
        )
        launchesModel.addObserver(mockObserver)


        //act
        launchesModel.fetchLaunches(mockSuccess, mockFailureWithPayload)


        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    companion object {
        private val logger = SystemLogger()

        private fun createMockLaunchesResponse(launch: Launch): LaunchListQuery.Data {

            val mockLaunchesData = mockk<LaunchListQuery.Data>()
            val mockLaunches = mockk<LaunchListQuery.Launches>()

            val missionQuery =
                LaunchListQuery.Mission(name = "mission name", missionPatch = launch.patchImgUrl)
            val launchQuery = LaunchListQuery.Launch(
                id = launch.id,
                site = launch.site,
                mission = missionQuery,
                isBooked = launch.isBooked
            )

            every {
                mockLaunchesData.launches
            } returns mockLaunches
            every {
                mockLaunches.launches
            } returns listOf(launchQuery)

            return mockLaunchesData
        }
    }
}
