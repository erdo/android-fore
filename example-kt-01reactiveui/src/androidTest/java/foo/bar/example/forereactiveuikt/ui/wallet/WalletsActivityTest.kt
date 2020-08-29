package foo.bar.example.forereactiveuikt.ui.wallet

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import foo.bar.example.forereactiveuikt.R
import foo.bar.example.forereactiveuikt.feature.wallet.Wallet
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */
@RunWith(AndroidJUnit4::class)
class WalletsActivityTest {

    @MockK
    private lateinit var mockWallet: Wallet

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }


    @Test
    @Throws(Exception::class)
    fun emptyMobileWallet() {

        //arrange
        StateBuilder(mockWallet)
                .withMobileWalletEmpty(100)
                .createRule()
                .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.wallet_decrease_btn)).check(matches(Matchers.not(ViewMatchers.isEnabled())))
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(ViewMatchers.withText("0")))
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(ViewMatchers.withText("100")))
    }

    @Test
    @Throws(Exception::class)
    fun fullMobileWallet() {

        //arrange
        StateBuilder(mockWallet)
                .withMobileWalletMaximum(100)
                .createRule()
                .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(Matchers.not(ViewMatchers.isEnabled())))
        onView(withId(R.id.wallet_decrease_btn)).check(matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(ViewMatchers.withText("100")))
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(ViewMatchers.withText("0")))
    }

    @Test
    @Throws(Exception::class)
    fun halfFullMobileWallet() {

        //arrange
        StateBuilder(mockWallet)
                .withMobileWalletHalfFull(32, 8)
                .createRule()
                .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.wallet_decrease_btn)).check(matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(ViewMatchers.withText("8")))
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(ViewMatchers.withText("32")))
    }

    @Test
    @Throws(Exception::class)
    fun stateMaintainedAfterRotation() {

        //arrange
        val activity: Activity = StateBuilder(mockWallet)
                .withMobileWalletHalfFull(200, 10)
                .createRule()
                .launchActivity(null)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(ViewMatchers.withText("10")))

        //act
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.wallet_decrease_btn)).check(matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(ViewMatchers.withText("10")))
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(ViewMatchers.withText("200")))
    }

    @Test
    @Throws(Exception::class)
    fun clickIncreaseCallsModel() {
        //arrange
        StateBuilder(mockWallet)
                .withMobileWalletHalfFull(32, 8)
                .createRule()
                .launchActivity(null)
        //act
        onView(withId(R.id.wallet_increase_btn)).perform(click())

        //assert
        verify(exactly = 1) {
            mockWallet.increaseMobileWallet()
        }
    }

    @Test
    @Throws(Exception::class)
    fun clickDecreaseCallsModel() {
        //arrange
        StateBuilder(mockWallet)
                .withMobileWalletHalfFull(32, 8)
                .createRule()
                .launchActivity(null)
        //act
        onView(withId(R.id.wallet_decrease_btn)).perform(click())

        //assert
        verify(exactly = 1) {
            mockWallet.decreaseMobileWallet()
        }
    }
}
