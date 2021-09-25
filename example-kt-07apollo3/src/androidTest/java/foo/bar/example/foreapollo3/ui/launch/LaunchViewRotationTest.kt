package foo.bar.example.foreapollo3.ui.launch

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import co.early.fore.kt.core.Either
import co.early.fore.kt.net.apollo3.CallProcessorApollo3
import foo.bar.example.foreapollo3.LaunchListQuery
import foo.bar.example.foreapollokt.R
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.feature.launch.Launch
import foo.bar.example.foreapollo3.feature.launch.LaunchService
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import foo.bar.example.foreapollo3.feature.launch.NO_ID
import foo.bar.example.foreapollo3.message.ErrorMessage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.ArrayList
import java.util.concurrent.CountDownLatch


/**
 * Here we make sure that when the view is rotated while a long running action is being
 * performed by the model, all the view elements still accurately represent the model's
 * state after rotation is complete, and those view elements are still updated as appropriate
 * when the model completes the long running action.
 */

@ExperimentalStdlibApi
@RunWith(AndroidJUnit4::class)
class LaunchViewRotationTest {

    /**
     * Here we're testing with a real model,
     * and just mocking its dependencies.
     * This lets us realistically test the
     * interactions between the view
     * and the model during and after
     * a rotation
     */

    @MockK
    private lateinit var mockSuccess: Success

    @MockK
    private lateinit var mockFailureWithPayload: FailureWithPayload<ErrorMessage>

    @MockK
    lateinit var mockCallProcessorApollo: CallProcessorApollo3<ErrorMessage>

    @MockK
    private lateinit var mockLaunchService: LaunchService

    @MockK
    private lateinit var mockAuthenticator: Authenticator

    lateinit var launchesModel: LaunchesModel
    private var logger: Logger = SystemLogger()

    private val launch = Launch("123", "site", true, "http://www.test.com/someimage.png")

    private lateinit var deferredResult: CompletableDeferred<Either<ErrorMessage, CallProcessorApollo3.SuccessResult<LaunchListQuery.Data>>>
    private val countDownLatch = CountDownLatch(1)

    @Before
    fun setUp() {

        logger.i("setup()")

        MockKAnnotations.init(this, relaxed = true)

        //construct a real model with mock dependencies
        launchesModel = LaunchesModel(
                mockLaunchService,
                mockCallProcessorApollo,
                mockAuthenticator,
                logger,
                WorkMode.ASYNCHRONOUS
        )
    }

    @Test
    @Throws(Exception::class)
    fun stateSurvivesRotation() {

        logger.i("stateSurvivesRotation()")

        //arrange
        val activity = LaunchViewRotationTestStateBuilder(this)
                .withDelayedCallProcessor()
                .createRule()
                .launchActivity(null)

        checkUIBeforeClick()

        //act
        launchesModel.fetchLaunches(mockSuccess, mockFailureWithPayload)

        checkUIWhenFetching()

        swapOrientation(activity)

        checkUIWhenFetching()

        completeDeferredResult()

        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
        }

        checkUIOnceComplete()
    }


    fun completeDeferredResult() {

        logger.i("callSuccessOnCachedSuccessFailCallback()")

        val launchList = ArrayList<Launch>()
        launchList.add(launch)

        //we need to be back on the UI thread for this
        getInstrumentation().runOnMainSync {
            logger.i("about to call success, id:" + Thread.currentThread().id)

            deferredResult.complete(Either.right(CallProcessorApollo3.SuccessResult(createMockLaunchesResponse(launch))))
            countDownLatch.countDown()
        }
    }

    fun setDeferredResult(deferredResult: CompletableDeferred<Either<ErrorMessage, CallProcessorApollo3.SuccessResult<LaunchListQuery.Data>>>) {
        logger.i("setDeferredResult()")
        this.deferredResult = deferredResult
    }


    @Synchronized
    private fun checkUIBeforeClick() {
        logger.i("checkUIBeforeClick()")

        //assert
        onView(withId(R.id.launch_busy_progbar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.launch_fetch_btn)).check(matches(isEnabled()))
        onView(withId(R.id.launch_id_textview)).check(matches(withText(containsString(NO_ID))))
    }

    @Synchronized
    private fun checkUIWhenFetching() {
        logger.i("checkUIWhenFetching()")

        //assert
        onView(withId(R.id.launch_busy_progbar)).check(matches(isDisplayed()))
        onView(withId(R.id.launch_fetch_btn)).check(matches(not(isEnabled())))
        onView(withId(R.id.launch_id_textview)).check(matches(not(isDisplayed())))
    }

    @Synchronized
    private fun checkUIOnceComplete() {
        logger.i("checkUIOnceComplete()")

        //assert
        onView(withId(R.id.launch_busy_progbar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.launch_fetch_btn)).check(matches(isEnabled()))
        onView(withId(R.id.launch_id_textview)).check(matches(withText(containsString(launch.id))))
    }

    private fun swapOrientation(activity: Activity) {
        activity.requestedOrientation = if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun createMockLaunchesResponse (launch: Launch) : LaunchListQuery.Data {

        val mockLaunchesData =  mockk<LaunchListQuery.Data>()
        val mockLaunches =  mockk<LaunchListQuery.Launches>()

        val missionQuery = LaunchListQuery.Mission(name ="mission name", missionPatch = launch.patchImgUrl)
        val launchQuery = LaunchListQuery.Launch(id = launch.id, site = launch.site, mission = missionQuery, isBooked = launch.isBooked)

        every {
            mockLaunchesData.launches
        } returns mockLaunches
        every {
            mockLaunches.launches
        } returns listOf(launchQuery)

        return mockLaunchesData
    }

}
