
package foo.bar.example.foreretrofit.ui.fruit

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import foo.bar.example.foreretrofit.EspressoTestMatchers.withDrawable
import foo.bar.example.foreretrofit.R
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */

@RunWith(AndroidJUnit4::class)
class FruitViewTest {

    @MockK
    private lateinit var mockFruitFetcher: FruitFetcher


    @Before
    fun setUp() = MockKAnnotations.init(this, relaxed = true)


    @Test
    @Throws(Exception::class)
    fun hasCitrusFruit() {

        //arrange
        val activity = FruitViewTestStateBuilder(mockFruitFetcher)
            .isBusy(false)
            .hasFruit(FruitPojo("testFruit1", true, 45))
            .createRule()
            .launchActivity(null)


        //act


        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_name_textview)).check(matches(withText("testFruit1")))
        onView(withId(R.id.fruit_tastyrating_textview)).check(
            matches(
                withText(
                    activity.getString(R.string.fruit_percent, 45)
                )
            )
        )
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_positive)))
    }


    @Test
    @Throws(Exception::class)
    fun hasNonCitrusFruit() {

        //arrange
        val activity = FruitViewTestStateBuilder(mockFruitFetcher)
            .isBusy(false)
            .hasFruit(FruitPojo("testFruit2", false, 75))
            .createRule()
            .launchActivity(null)


        //act


        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()))
        onView(withId(R.id.fruit_name_textview)).check(matches(withText("testFruit2")))
        onView(withId(R.id.fruit_tastyrating_textview)).check(
            matches(
                withText(
                    activity.getString(R.string.fruit_percent, 75)
                )
            )
        )
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_negative)))
    }


    @Test
    @Throws(Exception::class)
    fun isFetchingFruit() {

        //arrange
        FruitViewTestStateBuilder(mockFruitFetcher)
            .isBusy(true)
            .hasFruit(FruitPojo("testFruit1", true, 45))
            .createRule()
            .launchActivity(null)


        //act


        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(isDisplayed()))
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.fruit_name_textview)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.fruit_citrus_img)).check(matches(not<View>(isDisplayed())))
    }


    @Test
    @Throws(Exception::class)
    fun clickCallsFetchSuccess() {
        //arrange
        FruitViewTestStateBuilder(mockFruitFetcher)
            .isBusy(false)
            .hasFruit(FruitPojo("testFruit2", false, 75))
            .createRule()
            .launchActivity(null)


        //act
        onView(withId(R.id.fruit_fetchsuccess_btn)).perform(click())


        //assert
        verify(exactly = 1) {
            mockFruitFetcher.fetchFruits(any(), any())
        }
    }


    @Test
    @Throws(Exception::class)
    fun clickCallsFetchFailBasic() {
        //arrange
        FruitViewTestStateBuilder(mockFruitFetcher)
            .isBusy(false)
            .hasFruit(FruitPojo("testFruit2", false, 75))
            .createRule()
            .launchActivity(null)


        //act
        onView(withId(R.id.fruit_fetchfailbasic_btn)).perform(click())


        //assert
        verify(exactly = 1) {
            mockFruitFetcher.fetchFruitsButFail(any(), any())
        }
    }


    @Test
    @Throws(Exception::class)
    fun clickCallsFetchFailAdvanced() {
        //arrange
        FruitViewTestStateBuilder(mockFruitFetcher)
            .isBusy(false)
            .hasFruit(FruitPojo("testFruit2", false, 75))
            .createRule()
            .launchActivity(null)


        //act
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).perform(click())


        //assert
        verify(exactly = 1) {
            mockFruitFetcher.fetchFruitsButFailAdvanced(any(), any())
        }
    }

}

