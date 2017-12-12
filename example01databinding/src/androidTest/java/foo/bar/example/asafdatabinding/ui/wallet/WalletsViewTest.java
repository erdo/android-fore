package foo.bar.example.asafdatabinding.ui.wallet;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import foo.bar.example.asafdatabinding.R;
import foo.bar.example.asafdatabinding.feature.wallet.Wallet;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */
@RunWith(AndroidJUnit4.class)
public class WalletsViewTest {


    private Wallet mockWallet;


    @Before
    public void setup(){

        //MockitoAnnotations.initMocks(WalletView.this);
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        mockWallet = mock(Wallet.class);

    }

    @Test
    public void emptyMobileWallet() throws Exception {

        //arrange
        new StateBuilder(mockWallet)
                .withMobileWalletEmpty(100)
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(isEnabled()));
        onView(withId(R.id.wallet_decrease_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(withText("0")));
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(withText("100")));
    }

    @Test
    public void fullMobileWallet() throws Exception {

        //arrange
        new StateBuilder(mockWallet)
                .withMobileWalletMaximum(100)
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.wallet_decrease_btn)).check(matches(isEnabled()));
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(withText("100")));
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(withText("0")));
    }

    @Test
    public void halfFullMobileWallet() throws Exception {

        //arrange
        new StateBuilder(mockWallet)
                .withMobileWalletHalfFull(32, 8)
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(isEnabled()));
        onView(withId(R.id.wallet_decrease_btn)).check(matches(isEnabled()));
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(withText("8")));
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(withText("32")));
    }

    @Test
    public void stateMaintainedAfterRotation() throws Exception {

        //arrange
        Activity activity = new StateBuilder(mockWallet)
                        .withMobileWalletHalfFull(200, 10)
                        .createRule()
                        .launchActivity(null);
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(withText("10")));

        //act
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        //assert
        onView(withId(R.id.wallet_increase_btn)).check(matches(isEnabled()));
        onView(withId(R.id.wallet_decrease_btn)).check(matches(isEnabled()));
        onView(withId(R.id.wallet_mobileamount_txt)).check(matches(withText("10")));
        onView(withId(R.id.wallet_savingsamount_txt)).check(matches(withText("200")));
    }

    @Test
    public void clickIncreaseCallsModel() throws Exception {
        //arrange
        new StateBuilder(mockWallet)
                .withMobileWalletHalfFull(32, 8)
                .createRule()
                .launchActivity(null);
        //act
        onView(withId(R.id.wallet_increase_btn)).perform(click());

        //assert
        verify(mockWallet).increaseMobileWallet();
    }

    @Test
    public void clickDecreaseCallsModel() throws Exception {
        //arrange
        new StateBuilder(mockWallet)
                .withMobileWalletHalfFull(32, 8)
                .createRule()
                .launchActivity(null);
        //act
        onView(withId(R.id.wallet_decrease_btn)).perform(click());

        //assert
        verify(mockWallet).decreaseMobileWallet();
    }

}
