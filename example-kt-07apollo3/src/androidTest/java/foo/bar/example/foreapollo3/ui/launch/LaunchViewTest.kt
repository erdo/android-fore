
package foo.bar.example.foreapollo3.ui.launch

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.foreapollokt.R
import foo.bar.example.foreapollo3.feature.launch.Launch
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */

@ExperimentalStdlibApi
@RunWith(AndroidJUnit4::class)
class LaunchViewTest {

    @MockK
    private lateinit var mockLaunchesModel: LaunchesModel

    private val launch = Launch("123", "site")

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }


    @Test
    @Throws(Exception::class)
    fun hasLaunch() {

        //arrange
        val activity = LaunchViewTestStateBuilder(mockLaunchesModel)
            .isBusy(false)
            .hasLaunch(launch)
            .createRule()
            .launchActivity(null)


        //act


        //assert
        onView(withId(R.id.launch_busy_progbar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.launch_fetch_btn)).check(matches(isEnabled()))
        onView(withId(R.id.launch_id_textview)).check(matches(withText(containsString(launch.id))))
    }

    @Test
    @Throws(Exception::class)
    fun isFetchingLaunch() {

        //arrange
        LaunchViewTestStateBuilder(mockLaunchesModel)
            .isBusy(true)
            .hasLaunch(launch)
            .createRule()
            .launchActivity(null)


        //act


        //assert
        onView(withId(R.id.launch_busy_progbar)).check(matches(isDisplayed()))
        onView(withId(R.id.launch_fetch_btn)).check(matches(not(isEnabled())))
        onView(withId(R.id.launch_id_textview)).check(matches(not(isDisplayed())))
    }


    @Test
    @Throws(Exception::class)
    fun clickCallsFetchSuccess() {
        //arrange
        LaunchViewTestStateBuilder(mockLaunchesModel)
            .isBusy(false)
            .hasLaunch(launch)
            .createRule()
            .launchActivity(null)


        //act
        onView(withId(R.id.launch_fetch_btn)).perform(click())


        //assert
        verify(exactly = 1) {
            mockLaunchesModel.fetchLaunches(any(), any())
        }
    }
}

