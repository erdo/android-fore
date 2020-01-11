package foo.bar.example.foreretrofit.ui.fruit;

import android.content.pm.ActivityInfo;

import org.mockito.ArgumentCaptor;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.SuccessCallbackWithPayload;
import co.early.fore.core.logging.SystemLogger;
import foo.bar.example.foreretrofit.App;
import foo.bar.example.foreretrofit.OG;
import foo.bar.example.foreretrofit.ProgressBarIdler;
import foo.bar.example.foreretrofit.feature.fruit.FruitFetcher;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 *
 */
public class FruitViewRotationTestStateBuilder {

    private final FruitViewRotationTest fruitViewRotationTest;

    FruitViewRotationTestStateBuilder(FruitViewRotationTest fruitViewRotationTest) {
        this.fruitViewRotationTest = fruitViewRotationTest;
    }

    FruitViewRotationTestStateBuilder withDelayedCallProcessor(){

        final ArgumentCaptor<SuccessCallbackWithPayload> callback = ArgumentCaptor.forClass(SuccessCallbackWithPayload.class);
        doAnswer(__ -> {

            // We need to store this callback so that we can trigger it later
            // We can't use any kind of post delayed thing because
            // espresso needs all threads to be idle before proceeding
            // from this method
            fruitViewRotationTest.setCachedSuccessCallback(callback.getValue());

            return null;
        })
        .when(fruitViewRotationTest.mockCallProcessor)
        .processCall(any(), any(), any(), callback.capture(), any());

        return this;
    }

    ActivityTestRule<FruitActivity>  createRule(){

        return new ActivityTestRule<FruitActivity>(FruitActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                new SystemLogger().i("FruitViewRotationTestStateBuilder", "beforeActivityLaunched()");

                App app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
                OG.setApplication(app, WorkMode.SYNCHRONOUS);
                //inject our test model
                OG.putMock(FruitFetcher.class, fruitViewRotationTest.fruitFetcher);

                app.registerActivityLifecycleCallbacks(new ProgressBarIdler());

            }

            @Override
            protected void afterActivityFinished() {
                super.afterActivityFinished();

                this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        };
    }

}
