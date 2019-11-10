package foo.bar.example.foreretrofit.ui.fruit;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import foo.bar.example.foreretrofitcoroutine.CustomApp;
import foo.bar.example.foreretrofit.ProgressBarIdler;
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo;
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher;
import foo.bar.example.foreretrofitcoroutine.ui.fruit.FruitActivity;

import static org.mockito.Mockito.when;

/**
 *
 */
public class FruitViewTestStateBuilder {

    private FruitFetcher mockFruitFetcher;

    FruitViewTestStateBuilder(FruitFetcher mockFruitFetcher) {
        this.mockFruitFetcher = mockFruitFetcher;
    }

    FruitViewTestStateBuilder isBusy(boolean busy) {
        when(mockFruitFetcher.isBusy()).thenReturn(busy);
        return this;
    }

    FruitViewTestStateBuilder hasFruit(FruitPojo fruitPojo) {
        when(mockFruitFetcher.getCurrentFruit()).thenReturn(fruitPojo);
        return this;
    }

    ActivityTestRule<FruitActivity>  createRule(){

        return new ActivityTestRule<FruitActivity>(FruitActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(FruitFetcher.class, mockFruitFetcher);
                customApp.registerActivityLifecycleCallbacks(new ProgressBarIdler());
            }

        };
    }

}
