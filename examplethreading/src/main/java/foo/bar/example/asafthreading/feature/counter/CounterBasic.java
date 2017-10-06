package foo.bar.example.asafthreading.feature.counter;


import co.early.asaf.framework.WorkMode;
import co.early.asaf.framework.callbacks.DoThisWithPayloadCallback;
import co.early.asaf.framework.logging.Logger;
import co.early.asaf.framework.observer.ObservableImp;
import co.early.asaf.framework.threading.AsafTaskBuilder;
import co.early.asaf.framework.threading.DoInBackgroundCallback;

/**
 *
 */
public class CounterBasic extends ObservableImp{

    private final WorkMode workMode;

    private boolean isBusy = false;
    private int count;


    public CounterBasic(WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.workMode = workMode;
    }


    public void increaseBy20(){

        isBusy = true;
        notifyObservers();

        new AsafTaskBuilder<Void, Integer>(workMode)
                .doInBackground(new DoInBackgroundCallback<Void, Integer>() {
                    @Override
                    public Integer doThisAndReturn(Void... input) {
                        return CounterBasic.this.doStuffInBackground(input);
                    }
                })
                .onPostExecute(new DoThisWithPayloadCallback<Integer>() {
                    @Override
                    public void doThis(Integer result) {
                        CounterBasic.this.doThingsWithTheResult(result);
                    }
                })
                .execute((Void) null);

    }


    private Integer doStuffInBackground(Void... inputs){
        int totalIncrease = 0;

        for (int ii=0; ii<20; ii++) {
            synchronized (this) {
                try {
                    wait(workMode == WorkMode.SYNCHRONOUS ? 1 : 100);
                } catch (InterruptedException e) {
                }
            }
            ++totalIncrease;
        }

        return totalIncrease;
    }


    private void doThingsWithTheResult(Integer result){
        count = count + result;
        isBusy = false;
        notifyObservers();
    }


    public boolean isBusy() {
        return isBusy;
    }

    public int getCount() {
        return count;
    }

}
