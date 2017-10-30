package foo.bar.example.asafretrofit.ui.fruit;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.logging.SystemLogger;
import co.early.asaf.core.observer.Observer;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.CustomApp;
import foo.bar.example.asafretrofit.ProgressBarIdler;
import foo.bar.example.asafretrofit.R;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.feature.fruit.FruitFetcher;
import foo.bar.example.asafretrofit.message.UserMessage;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Here we make sure that when the view is rotated while a long running action is being
 * performed by the model, all the view elements still accurately represent the model's
 * state after rotation is complete, and those view elements are still updated as appropriate
 * when the model completes the long running action.
 */
@RunWith(AndroidJUnit4.class)
public class FruitViewRotationTest {

    /**
     * Here we're testing with a real model,
     * and just mocking its dependencies.
     * This lets us realistically test the
     * interactions between the view
     * and the model during and after
     * a rotation
     */
    private FruitFetcher fruitFetcher;
    private Logger logger = new SystemLogger();


    private SuccessCallBack mockSuccessCallBack;
    private FailureCallbackWithPayload mockFailureCallbackWithPayload;
    private CallProcessor<UserMessage> mockCallProcessor;
    private FruitService mockFruitService;
    private Observer mockObserver;


    @Rule
    public ActivityTestRule<FruitActivity> rule = new ActivityTestRule<FruitActivity>(FruitActivity.class) {
        @Override
        protected void beforeActivityLaunched() {

            //MockitoAnnotations.initMocks(LoginViewTest.this);
            System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());
            mockSuccessCallBack = mock(SuccessCallBack.class);
            mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
            mockCallProcessor = mock(CallProcessor.class);

            CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
            customApp.injectSynchronousObjectGraph();
            customApp.registerActivityLifecycleCallbacks(new ProgressBarIdler());

            //inject our model constructed with mock dependencies
            fruitFetcher = new FruitFetcher(
                    mockFruitService,
                    mockCallProcessor,
                    logger,
                    WorkMode.SYNCHRONOUS);

            customApp.injectMockObject(FruitFetcher.class, fruitFetcher);
        }

        @Override
        protected void afterActivityFinished() {
            super.afterActivityFinished();

            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    };


    @Test
    public void stateSurvivesRotation() throws Exception {

        //arrange
        new AuthenticationStateBuilder(mockSession, mockMerchant);
        switchOrientationAndSwapProgressSpinners(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        onView(withId(R.id.login_username_edittext)).perform(clearText());
        onView(withId(R.id.login_password_edittext)).perform(clearText());

        onView(withId(R.id.login_username_edittext)).perform(typeText("username"));
        onView(withId(R.id.login_password_edittext)).perform(typeText("password"));
        final ArgumentCaptor<SuccessFailCallback> callback = ArgumentCaptor.forClass(SuccessFailCallback.class);
        doAnswer(__ -> {

            // We need to store this callback so that we can trigger it later
            // We can't use any kind of post delayed thing because
            // espresso needs all threads to be idle before proceeding
            // from this method
            setCachedSuccessFailCallback(callback.getValue());

            return null;
        })
                .when(mockSession)
                .createSessionToken(any(String.class), any(String.class), callback.capture());

        //act
        onView(withId(R.id.login_login_btn)).perform(click());

        runSomeTestsBeforeSuccesFailCallbackIsInvoked();

        callSuccessOnCachedSuccessFailCallback();

        try{
            countDownLatch.await();
        }catch(InterruptedException e){
        }

        runSomeTestsAfterCompletingClick();
    }

    private synchronized void runSomeTestsBeforeSuccesFailCallbackIsInvoked() {
        System.out.println("runSomeTestsBeforeSuccesFailCallbackIsInvoked()");

        //assert - should be showing spinner now
        onView(withId(R.id.login_busy_progressbar)).check(matches(isDisplayed()));

        //act - switch orientation
        switchOrientationAndSwapProgressSpinners(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //assert - should still be showing spinner in new orientation
        onView(withId(R.id.login_busy_progressbar)).check(matches(isDisplayed()));
    }


    private synchronized void runSomeTestsAfterCompletingClick() {
        System.out.println("runSomeTestsAfterCompletingClick()");

        //assert - is the view still correct
        onView(withId(R.id.login_busy_progressbar)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.login_state_textview)).check(matches(withText(containsString("LOGGED IN"))));

        //act - switch orientation back
        switchOrientationAndSwapProgressSpinners(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //assert - is the view still correct
        onView(withId(R.id.login_busy_progressbar)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.login_state_textview)).check(matches(withText(containsString("LOGGED IN"))));
    }


    rule.getActivity().setRequestedOrientation(screenOrientation);



    @Test
    public void rotationStateIsFetching() throws Exception {

        //arrange
        Activity activity = new StateBuilder(mockFruitFetcher)
                .isBusy(true)
                .hasFruit(new FruitPojo("testFruit1", true, 45))
                .createRule()
                .launchActivity(null);
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);

        //act
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(isDisplayed()));

        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(not(isEnabled())));

        onView(withId(R.id.fruit_name_textview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_citrus_img)).check(matches(not(isDisplayed())));
    }


}
