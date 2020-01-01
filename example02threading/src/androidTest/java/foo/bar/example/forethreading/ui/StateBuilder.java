package foo.bar.example.forethreading.ui;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import co.early.fore.core.WorkMode;
import foo.bar.example.forethreading.App;
import foo.bar.example.forethreading.OG;
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
                App app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
                OG.setApplication(app, WorkMode.SYNCHRONOUS);

                //inject our mocks so our UI layer will pick them up
                OG.putMock(CounterWithLambdas.class, mockCounterWithLambdas);
                OG.putMock(CounterWithProgress.class, mockCounterWithProgress);
            }

        };
    }

}
