package foo.bar.example.asafretrofit.ui.fruit;

import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.mockito.ArgumentCaptor;

import co.early.fore.core.callbacks.SuccessCallbackWithPayload;
import co.early.fore.core.logging.SystemLogger;
import foo.bar.example.asafretrofit.CustomApp;
import foo.bar.example.asafretrofit.ProgressBarIdler;
import foo.bar.example.asafretrofit.feature.fruit.FruitFetcher;

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

                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();
                //inject our test model
                customApp.injectMockObject(FruitFetcher.class, fruitViewRotationTest.fruitFetcher);
                customApp.registerActivityLifecycleCallbacks(new ProgressBarIdler());

            }

            @Override
            protected void afterActivityFinished() {
                super.afterActivityFinished();

                this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        };
    }

}
