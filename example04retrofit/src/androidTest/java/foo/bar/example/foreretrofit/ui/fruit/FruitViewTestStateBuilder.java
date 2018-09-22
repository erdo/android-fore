package foo.bar.example.foreretrofit.ui.fruit;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import foo.bar.example.foreretrofit.CustomApp;
import foo.bar.example.foreretrofit.ProgressBarIdler;
import foo.bar.example.foreretrofit.api.fruits.FruitPojo;
import foo.bar.example.foreretrofit.feature.fruit.FruitFetcher;

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
