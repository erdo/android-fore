package foo.bar.example.forethreading.ui;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import foo.bar.example.forethreading.CustomApp;
import foo.bar.example.forethreading.feature.counter.CounterWithLambdas;
import foo.bar.example.forethreading.feature.counter.CounterWithProgress;

import static org.mockito.Mockito.when;

/**
 *
 */
public class StateBuilder {

    private CounterWithLambdas mockCounterWithLambdas;
    private CounterWithProgress mockCounterWithProgress;

    StateBuilder(CounterWithLambdas mockCounterWithLambdas, CounterWithProgress mockCounterWithProgress) {
        this.mockCounterWithLambdas = mockCounterWithLambdas;
        this.mockCounterWithProgress = mockCounterWithProgress;
    }

    StateBuilder counterBasicIsBusy(boolean busy) {
        when(mockCounterWithLambdas.isBusy()).thenReturn(busy);
        return this;
    }

    StateBuilder counterWithProgressIsBusy(boolean busy) {
        when(mockCounterWithProgress.isBusy()).thenReturn(busy);
        return this;
    }

    StateBuilder counterBasicCount(int count) {
        when(mockCounterWithLambdas.getCount()).thenReturn(count);
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

    ActivityTestRule<CounterActivity>  createRule(){

        return new ActivityTestRule<CounterActivity>(CounterActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(CounterWithLambdas.class, mockCounterWithLambdas);
                customApp.injectMockObject(CounterWithProgress.class, mockCounterWithProgress);
            }

        };
    }

}
