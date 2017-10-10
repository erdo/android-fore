package foo.bar.example.asafthreading.feature.counter;


import co.early.asaf.framework.Affirm;
import co.early.asaf.framework.WorkMode;
import co.early.asaf.framework.callbacks.DoThisWithPayloadCallback;
import co.early.asaf.framework.logging.Logger;
import co.early.asaf.framework.observer.ObservableImp;
import co.early.asaf.framework.threading.AsafTaskBuilder;
import co.early.asaf.framework.threading.DoInBackgroundCallback;

/**
 *
 */
public class CounterWithLambdas extends ObservableImp{

    public static final String TAG = CounterWithLambdas.class.getSimpleName();

    private final WorkMode workMode;
    private final Logger logger;

    private boolean isBusy = false;
    private int count;


    public CounterWithLambdas(WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.workMode = Affirm.notNull(workMode);
        this.logger = Affirm.notNull(logger);
    }


    public void increaseBy20(){

        logger.i(TAG, "increaseBy20()");

        isBusy = true;
        notifyObservers();

        //don't forget AsafTaskBuilder lets you use lambda expressions too
        new AsafTaskBuilder<Void, Integer>(workMode)
                .doInBackground(new DoInBackgroundCallback<Void, Integer>() {
                    @Override
                    public Integer doThisAndReturn(Void... input) {
                        return CounterWithLambdas.this.doStuffInBackground(input);
                    }
                })
                .onPostExecute(new DoThisWithPayloadCallback<Integer>() {
                    @Override
                    public void doThis(Integer result) {
                        CounterWithLambdas.this.doThingsWithTheResult(result);
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

            logger.i(TAG, "-tick-");
        }

        return totalIncrease;
    }


    private void doThingsWithTheResult(Integer result){

        logger.i(TAG, "done");

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
