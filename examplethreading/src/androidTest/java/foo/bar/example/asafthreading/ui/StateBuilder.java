package foo.bar.example.asafthreading.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import foo.bar.example.asafthreading.CustomApp;
import foo.bar.example.asafthreading.feature.counter.CounterBasic;
import foo.bar.example.asafthreading.feature.counter.CounterWithProgress;

import static org.mockito.Mockito.when;

/**
 *
 */
public class StateBuilder {

    private CounterBasic mockCounterBasic;
    private CounterWithProgress mockCounterWithProgress;

    StateBuilder(CounterBasic mockCounterBasic, CounterWithProgress mockCounterWithProgress) {
        this.mockCounterBasic = mockCounterBasic;
        this.mockCounterWithProgress = mockCounterWithProgress;
    }

    StateBuilder counterBasicIsBusy(boolean busy) {
        when(mockCounterBasic.isBusy()).thenReturn(busy);
        return this;
    }

    StateBuilder counterWithProgressIsBusy(boolean busy) {
        when(mockCounterWithProgress.isBusy()).thenReturn(busy);
        return this;
    }

    StateBuilder counterBasicCount(int count) {
        when(mockCounterBasic.getCount()).thenReturn(count);
        return this;
    }

    StateBuilder counterWithProgressCount(int count) {
        when(mockCounterWithProgress.getCount()).thenReturn(count);
        return this;
    }

    StateBuilder counterWithProgressProgressValue(int value) {
        when(mockCounterWithProgress.getProgress()).thenReturn(value);
        return this;
    }

    ActivityTestRule<ThreadingExampleActivity>  createRule(){

        return new ActivityTestRule<ThreadingExampleActivity>(ThreadingExampleActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(CounterBasic.class, mockCounterBasic);
                customApp.injectMockObject(CounterWithProgress.class, mockCounterWithProgress);
            }

        };
    }

}
