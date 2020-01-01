package foo.bar.example.forecoroutine.ui

import android.app.Application
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.view.View
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import foo.bar.example.forecoroutine.ProgressBarIdler
import foo.bar.example.forecoroutine.R
import foo.bar.example.forecoroutine.feature.counter.Counter
import foo.bar.example.forecoroutine.feature.counter.CounterWithProgress
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Copyright © 2019 early.co. All rights reserved.
 *
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */
@RunWith(AndroidJUnit4::class)
class CounterViewTest {


    @MockK
    private lateinit var mockCounter: Counter
    @MockK
    private lateinit var mockCounterWithProgress: CounterWithProgress


    @Before
    fun setup() {

        MockKAnnotations.init(this, relaxed = true)

        val application = getInstrumentation().targetContext.applicationContext as Application
        application.registerActivityLifecycleCallbacks(ProgressBarIdler())
    }

    @Test
    @Throws(Exception::class)
    fun initialState() {

        //arrange
        StateBuilder(mockCounter, mockCounterWithProgress)
            .counterWithProgressIsBusy(true)
            .counterWithProgressProgressValue(7)
            .counterWithProgressCount(40)
            .counterBasicIsBusy(false)
            .counterBasicCount(0)
            .createRule()
            .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.counterwprog_increase_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.counterwprog_busy_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.counterwprog_progress_txt)).check(matches(withText("7")))
        onView(withId(R.id.counterwprog_current_txt)).check(matches(withText("40")))
        onView(withId(R.id.counter_increase_btn)).check(matches(isEnabled()))
        onView(withId(R.id.counter_busy_progress)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.counter_current_txt)).check(matches(withText("0")))
    }

    @Test
    @Throws(Exception::class)
    fun rotationState() {

        //arrange
        val activity = StateBuilder(mockCounter, mockCounterWithProgress)
            .counterWithProgressIsBusy(true)
            .counterWithProgressProgressValue(7)
            .counterWithProgressCount(40)
            .counterBasicIsBusy(false)
            .counterBasicCount(0)
            .createRule()
            .launchActivity(null)
        activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        //act
        activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        //assert
        onView(withId(R.id.counterwprog_increase_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.counterwprog_busy_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.counterwprog_progress_txt)).check(matches(withText("7")))
        onView(withId(R.id.counterwprog_current_txt)).check(matches(withText("40")))
        onView(withId(R.id.counter_increase_btn)).check(matches(isEnabled()))
        onView(withId(R.id.counter_busy_progress)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.counter_current_txt)).check(matches(withText("0")))
    }

    @Test
    @Throws(Exception::class)
    fun clickCallsBasicModel() {
        //arrange
        StateBuilder(mockCounter, mockCounterWithProgress)
            .counterBasicIsBusy(false)
            .createRule()
            .launchActivity(null)

        //act
        onView(withId(R.id.counter_increase_btn)).perform(click())

        //assert
        verify(exactly = 1) {
            mockCounter.increaseBy20()
        }
    }

    @Test
    @Throws(Exception::class)
    fun clickCallsWithProgressModel() {
        //arrange
        StateBuilder(mockCounter, mockCounterWithProgress)
            .counterWithProgressIsBusy(false)
            .createRule()
            .launchActivity(null)

        //act
        onView(withId(R.id.counterwprog_increase_btn)).perform(click())

        //assert
        verify(exactly = 1) {
            mockCounterWithProgress.increaseBy20()
        }
    }
}
