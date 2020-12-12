package foo.bar.example.foreapollokt.feature.launch

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.net.apollo.ApolloCallProcessor
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
class LaunchFetcherUnitTest {

    private val launch = Launch("123", "site", true, 50)

    @MockK
    private lateinit var mockSuccess: Success

    @MockK
    private lateinit var mockFailureWithPayload: FailureWithPayload<ErrorMessage>

    @MockK
    private lateinit var mockCallProcessor: ApolloCallProcessor<ErrorMessage>

    @MockK
    private lateinit var mockLaunchService: LaunchService

    @MockK
    private lateinit var mockObserver: Observer


    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)


    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val launchFetcher = LaunchFetcher(
                mockLaunchService,
                mockCallProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )

        //act

        //assert
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(0, launchFetcher.currentLaunch.tastyPercentScore.toLong())
        Assert.assertEquals(false, launchFetcher.currentLaunch.isCitrus)
    }


    @Test
    @Throws(Exception::class)
    fun fetchLaunch_MockSuccess() {

        val mockLaunchesData = createMockLaunchesResponse("123", "site")

        //arrange
        val callProcessor = StateBuilder().getLaunchSuccess(mockLaunchesData).mockApolloCallProcessor
        val launchFetcher = LaunchFetcher(
                mockLaunchService,
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )


        //act
        launchFetcher.fetchLaunchesAsync(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 1) {
            mockSuccess()
        }
        verify(exactly = 0) {
            mockFailureWithPayload(any())
        }
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(launch.site, launchFetcher.currentLaunch.site)
        Assert.assertEquals(launch.isCitrus, launchFetcher.currentLaunch.isCitrus)
        Assert.assertEquals(launch.tastyPercentScore.toLong(), launchFetcher.currentLaunch.tastyPercentScore.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun fetchLaunch_MockFailure() {

        //arrange

        val callProcessor = StateBuilder().getLaunchFail(ErrorMessage.INTERNAL_SERVER_ERROR).mockApolloCallProcessor
        val launchFetcher = LaunchFetcher(
                mockLaunchService,
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )


        //act
        launchFetcher.fetchLaunchesButFailAdvanced(mockSuccess, mockFailureWithPayload)


        //assert
        verify(exactly = 0) {
            mockSuccess()
        }
        verify(exactly = 1) {
            mockFailureWithPayload(eq(ErrorMessage.INTERNAL_SERVER_ERROR))
        }
        Assert.assertEquals(false, launchFetcher.isBusy)
        Assert.assertEquals(false, launchFetcher.currentLaunch.isCitrus)
        Assert.assertEquals(0, launchFetcher.currentLaunch.tastyPercentScore.toLong())
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

        val mockLaunchesData = createMockLaunchesResponse("123", "site")

        //arrange
        val callProcessor = StateBuilder().getLaunchSuccess(mockLaunchesData).mockApolloCallProcessor
        val launchFetcher = LaunchFetcher(
                mockLaunchService,
                callProcessor,
                logger,
                WorkMode.SYNCHRONOUS
        )
        launchFetcher.addObserver(mockObserver)


        //act
        launchFetcher.fetchLaunchesAsync(mockSuccess, mockFailureWithPayload)


        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    companion object {
        private val logger = SystemLogger()

        private fun createMockLaunchesResponse (id: String, site: String) : LaunchListQuery.Data{

            val mockLaunchesData =  mockk<LaunchListQuery.Data>()
            val mockLaunches =  mockk<LaunchListQuery.Launches>()
            val mockLaunch =  mockk<LaunchListQuery.Launch>()

            every {
                mockLaunchesData.launches
            } returns mockLaunches
            every {
                mockLaunches.launches
            } returns listOf(mockLaunch)
            every {
                mockLaunch.id
            } returns id
            every {
                mockLaunch.site
            } returns site

            return mockLaunchesData
        }
    }
}
