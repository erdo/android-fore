/*
package foo.bar.example.foreretrofit.ui.fruit

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import androidx.test.InstrumentationRegistry
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import co.early.fore.core.WorkMode
import co.early.fore.core.callbacks.FailureWithPayload
import co.early.fore.core.callbacks.Success
import co.early.fore.core.callbacks.SuccessWithPayload
import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.SystemLogger
import co.early.fore.retrofit.coroutine.CallProcessor
import foo.bar.example.foreretrofit.EspressoTestMatchers.withDrawable
import foo.bar.example.foreretrofit.R
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitService
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.ArrayList
import java.util.concurrent.CountDownLatch

*/
/**
 * Here we make sure that when the view is rotated while a long running action is being
 * performed by the model, all the view elements still accurately represent the model's
 * state after rotation is complete, and those view elements are still updated as appropriate
 * when the model completes the long running action.
 *//*

@RunWith(AndroidJUnit4::class)
class FruitViewRotationTest {

    */
/**
     * Here we're testing with a real model,
     * and just mocking its dependencies.
     * This lets us realistically test the
     * interactions between the view
     * and the model during and after
     * a rotation
     *//*

    private lateinit var fruitFetcher: FruitFetcher
    private var logger: Logger = SystemLogger()
    private var fruitPojo = FruitPojo("testFruit1", true, 45)

    @Mock
    private lateinit var mockSuccess: Success
    @Mock
    private lateinit var mockFailureWithPayload: FailureWithPayload<UserMessage>
    @Mock
    private lateinit var mockCallProcessor: CallProcessor<UserMessage>
    @Mock
    private lateinit var mockFruitService: FruitService

    private lateinit var cachedSuccess: SuccessWithPayload<ArrayList<FruitPojo>>
    private val countDownLatch = CountDownLatch(1)

    @Before
    fun setup() {

        logger.i(LOG_TAG, "setup()")

        MockitoAnnotations.initMocks(this)
        //MockitoAnnotations.initMocks(LoginViewTest.this);
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().cacheDir.path)

//        mockSuccess = mock<Success>{}
//        mockFailureWithPayload = mock<FailureWithPayload<*>>
//        mockCallProcessor = mock<CallProcessor<*>>{}
//        mockFruitService = mock(FruitService::class.java)

        //construct a real model with mock dependencies
        fruitFetcher = FruitFetcher(
            mockFruitService,
            mockCallProcessor,
            logger,
            WorkMode.ASYNCHRONOUS
        )

    }

    @Test
    @Throws(Exception::class)
    fun stateSurvivesRotation() {

        logger.i(LOG_TAG, "stateSurvivesRotation()")

        //arrange
        val activity = FruitViewRotationTestStateBuilder(this)
            .withDelayedCallProcessor()
            .createRule()
            .launchActivity(null)

        checkUIBeforeClick(activity)

        //act
        fruitFetcher.fetchFruits(mockSuccess, mockFailureWithPayload)

        checkUIWhenFetching(activity)

        swapOrientation(activity)

        checkUIWhenFetching(activity)

        callCachedSuccessCallback()

        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
        }

        checkUIOnceComplete(activity)
    }


    fun callCachedSuccessCallback() {

        logger.i(LOG_TAG, "callSuccessOnCachedSuccessFailCallback()")

        val fruitList = ArrayList<FruitPojo>()
        fruitList.add(fruitPojo)

        //we need to be back on the UI thread for this
        getInstrumentation().runOnMainSync {
            logger.i(LOG_TAG, "about to call success, id:" + Thread.currentThread().id)

            cachedSuccess(fruitList)
            countDownLatch.countDown()
        }
    }

    fun setCachedSuccessCallback(success: SuccessWithPayload<ArrayList<FruitPojo>>) {
        logger.i(LOG_TAG, "setCachedSuccessFailCallback()")
        this.cachedSuccess = success
    }


    @Synchronized
    private fun checkUIBeforeClick(activity: Activity) {
        logger.i(LOG_TAG, "checkUIBeforeClick()")

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_name_textview)).check(matches(withText("(fruitless)")))
        onView(withId(R.id.fruit_tastyrating_textview)).check(
            matches(
                withText(
                    activity.getString(R.string.fruit_percent, 0)
                )
            )
        )
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_negative)))
    }

    @Synchronized
    private fun checkUIWhenFetching(activity: Activity) {
        logger.i(LOG_TAG, "checkUIWhenFetching()")

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(isDisplayed()))
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.fruit_name_textview)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_tastyrating_textview)).check(
            matches(
                withText(
                    activity.getString(R.string.fruit_percent, 0)
                )
            )
        )
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_negative)))
    }

    @Synchronized
    private fun checkUIOnceComplete(activity: Activity) {
        logger.i(LOG_TAG, "checkUIOnceComplete()")

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_name_textview)).check(matches(withText(fruitPojo.name)))
        onView(withId(R.id.fruit_tastyrating_textview)).check(
            matches(
                withText(
                    activity.getString(R.string.fruit_percent, fruitPojo.tastyPercentScore)
                )
            )
        )
        onView(withId(R.id.fruit_citrus_img)).check(
            matches(
                withDrawable(
                    if (fruitPojo.isCitrus)
                        R.drawable.lemon_positive
                    else
                        R.drawable.lemon_negative
                )
            )
        )
    }

    private fun swapOrientation(activity: Activity) {
        activity.requestedOrientation = if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    companion object {
        val LOG_TAG = FruitViewRotationTest::class.java.simpleName
    }


}
*/
