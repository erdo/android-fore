package foo.bar.example.foreretrofit.ui.fruit;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import foo.bar.example.foreretrofit.R;
import foo.bar.example.foreretrofit.api.fruits.FruitPojo;
import foo.bar.example.foreretrofit.feature.fruit.FruitFetcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static foo.bar.example.foreretrofit.EspressoTestMatchers.withDrawable;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */
@RunWith(AndroidJUnit4.class)
public class FruitViewTest {


    private FruitFetcher mockFruitFetcher;


    @Before
    public void setup(){

        //MockitoAnnotations.initMocks(CounterView.this);
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        mockFruitFetcher = mock(FruitFetcher.class);

    }


    @Test
    public void hasCitrusFruit() throws Exception {

        //arrange
        Activity activity = new FruitViewTestStateBuilder(mockFruitFetcher)
                .isBusy(false)
                .hasFruit(new FruitPojo("testFruit1", true, 45))
                .createRule()
                .launchActivity(null);


        //act


        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_name_textview)).check(matches(withText("testFruit1")));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(withText(
                activity.getString(R.string.fruit_percent, 45)
        )));
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_positive)));
    }


    @Test
    public void hasNonCitrusFruit() throws Exception {

        //arrange
        Activity activity = new FruitViewTestStateBuilder(mockFruitFetcher)
                .isBusy(false)
                .hasFruit(new FruitPojo("testFruit2", false, 75))
                .createRule()
                .launchActivity(null);


        //act


        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(isEnabled()));
        onView(withId(R.id.fruit_name_textview)).check(matches(withText("testFruit2")));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(withText(
                activity.getString(R.string.fruit_percent, 75)
        )));
        onView(withId(R.id.fruit_citrus_img)).check(matches(withDrawable(R.drawable.lemon_negative)));
    }


    @Test
    public void isFetchingFruit() throws Exception {

        //arrange
        new FruitViewTestStateBuilder(mockFruitFetcher)
                .isBusy(true)
                .hasFruit(new FruitPojo("testFruit1", true, 45))
                .createRule()
                .launchActivity(null);


        //act


        //assert
        onView(withId(R.id.fruit_busy_progbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fruit_fetchsuccess_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_fetchfailbasic_btn)).check(matches(not(isEnabled())));
        onView(withId(R.id.fruit_name_textview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_tastyrating_textview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fruit_citrus_img)).check(matches(not(isDisplayed())));
    }


    @Test
    public void clickCallsFetchSuccess() throws Exception {
        //arrange
        new FruitViewTestStateBuilder(mockFruitFetcher)
                .isBusy(false)
                .hasFruit(new FruitPojo("testFruit2", false, 75))
                .createRule()
                .launchActivity(null);


        //act
        onView(withId(R.id.fruit_fetchsuccess_btn)).perform(click());


        //assert
        verify(mockFruitFetcher).fetchFruits(any(), any());
    }


    @Test
    public void clickCallsFetchFailBasic() throws Exception {
        //arrange
        new FruitViewTestStateBuilder(mockFruitFetcher)
                .isBusy(false)
                .hasFruit(new FruitPojo("testFruit2", false, 75))
                .createRule()
                .launchActivity(null);


        //act
        onView(withId(R.id.fruit_fetchfailbasic_btn)).perform(click());


        //assert
        verify(mockFruitFetcher).fetchFruitsButFailBasic(any(), any());
    }


    @Test
    public void clickCallsFetchFailAdvanced() throws Exception {
        //arrange
        new FruitViewTestStateBuilder(mockFruitFetcher)
                .isBusy(false)
                .hasFruit(new FruitPojo("testFruit2", false, 75))
                .createRule()
                .launchActivity(null);


        //act
        onView(withId(R.id.fruit_fetchfailadvanced_btn)).perform(click());


        //assert
        verify(mockFruitFetcher).fetchFruitsButFailAdvanced(any(), any());
    }

}
