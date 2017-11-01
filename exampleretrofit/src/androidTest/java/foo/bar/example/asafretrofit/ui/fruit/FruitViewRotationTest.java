package foo.bar.example.asafretrofit.ui.fruit;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallBack;
import co.early.asaf.core.callbacks.SuccessCallbackWithPayload;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.logging.SystemLogger;
import co.early.asaf.core.observer.Observer;
import co.early.asaf.retrofit.CallProcessor;
import foo.bar.example.asafretrofit.R;
import foo.bar.example.asafretrofit.api.fruits.FruitPojo;
import foo.bar.example.asafretrofit.api.fruits.FruitService;
import foo.bar.example.asafretrofit.feature.fruit.FruitFetcher;
import foo.bar.example.asafretrofit.message.UserMessage;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static foo.bar.example.asafretrofit.EspressoTestsMatchers.withDrawable;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

/**
 * Here we make sure that when the view is rotated while a long running action is being
 * performed by the model, all the view elements still accurately represent the model's
 * state after rotation is complete, and those view elements are still updated as appropriate
 * when the model completes the long running action.
 */
@RunWith(AndroidJUnit4.class)
public class FruitViewRotationTest {

    public static final String TAG = FruitViewRotationTest.class.getSimpleName();

    /**
     * Here we're testing with a real model,
     * and just mocking its dependencies.
     * This lets us realistically test the
     * interactions between the view
     * and the model during and after
     * a rotation
     */
    FruitFetcher fruitFetcher;
    Logger logger = new SystemLogger();
    FruitPojo fruitPojo = new FruitPojo("testFruit1", true, 45);

    SuccessCallBack mockSuccessCallBack;
    FailureCallbackWithPayload mockFailureCallbackWithPayload;
    CallProcessor<UserMessage> mockCallProcessor;
    FruitService mockFruitService;
    Observer mockObserver;

    SuccessCallbackWithPayload cachedSuccessCallback;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Before
    public void setup(){

        logger.i(TAG, "setup()");

        //MockitoAnnotations.initMocks(LoginViewTest.this);
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        mockSuccessCallBack = mock(SuccessCallBack.class);
        mockFailureCallbackWithPayload = mock(FailureCallbackWithPayload.class);
        mockCallProcessor = mock(CallProcessor.class);
        mockFruitService = mock(FruitService.class);

        //construct a real model with mock dependencies
        fruitFetcher = new FruitFetcher(
                mockFruitService,
                mockCallProcessor,
                logger,
                WorkMode.ASYNCHRONOUS);

    }

    @Test
    public void stateSurvivesRotation() throws Exception {

        logger.i(TAG, "stateSurvivesRotation()");

        //arrange
        Activity activity = new FruitViewRotationTestStateBuilder(this)
                .withDelayedCallProcessor()
                .createRule()
                .launchActivity(null);

        checkUIBeforeClick(activity);

        //act
        fruitFetcher.fetchFruits(mockSuccessCallBack, mockFailureCallbackWithPayload);

        checkUIWhenFetching(activity);

        swapOrientation(activity);

        checkUIWhenFetching(activity);

        callCachedSuccessCallback();

        try{
            countDownLatch.await();
        }catch(InterruptedException e){
        }

        checkUIOnceComplete(activity);
    }



    public void callCachedSuccessCallback() {

        logger.i(TAG, "callSuccessOnCachedSuccessFailCallback()");

        List<FruitPojo> fruitList = new ArrayList<>();
        fruitList.add(fruitPojo);

        //we need to be back on the UI thread for this
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {

                logger.i(TAG, "about to call success, id:" + Thread.currentThread().getId());

                cachedSuccessCallback.success(fruitList);
                countDownLatch.countDown();
            }
        });
    }

    public void setCachedSuccessCallback(SuccessCallbackWithPayload successCallBack) {
        logger.i(TAG, "setCachedSuccessFailCallback()");
        this.cachedSuccessCallback = successCallBack;
    }


    private synchronized void checkUIBeforeClick(Activity activity) {
        logger.i(TAG, "checkUIBeforeClick()");

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_name_textview)).check(matches(withText("(fruitless)")));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(withText(
                activity.getString(R.string.fruit_percent, 0)
        )));
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_negative)));
    }

    private synchronized void checkUIWhenFetching(Activity activity) {
        logger.i(TAG, "checkUIWhenFetching()");

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_name_textview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(withText(
                activity.getString(R.string.fruit_percent, 0)
        )));
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_negative)));
    }

    private synchronized void checkUIOnceComplete(Activity activity) {
        logger.i(TAG, "checkUIOnceComplete()");

        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_name_textview)).check(matches(withText(fruitPojo.name)));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(withText(
                activity.getString(R.string.fruit_percent, fruitPojo.tastyPercentScore)
        )));
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(fruitPojo.isCitrus ?
                R.drawable.lemon_positive : R.drawable.lemon_negative)));
    }

    private void swapOrientation(Activity activity){
        activity.setRequestedOrientation(activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }



}
