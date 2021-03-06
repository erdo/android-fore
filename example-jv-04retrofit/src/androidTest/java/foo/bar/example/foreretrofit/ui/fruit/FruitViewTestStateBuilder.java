package foo.bar.example.foreretrofit.ui.fruit;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import co.early.fore.core.WorkMode;
import foo.bar.example.foreretrofit.App;
import foo.bar.example.foreretrofit.OG;
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
                App app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
                OG.setApplication(app, WorkMode.SYNCHRONOUS);

                //inject our mocks so our UI layer will pick them up
                OG.putMock(FruitFetcher.class, mockFruitFetcher);

                app.registerActivityLifecycleCallbacks(new ProgressBarIdler());
            }

        };
    }

}
