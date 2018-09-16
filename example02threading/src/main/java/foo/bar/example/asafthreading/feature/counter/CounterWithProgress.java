package foo.bar.example.asafthreading.feature.counter;


import android.annotation.SuppressLint;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.threading.Async;

/**
 *
 */
public class CounterWithProgress extends ObservableImp{

    public static final String TAG = CounterWithProgress.class.getSimpleName();

    private final WorkMode workMode;
    private final Logger logger;

    private boolean isBusy = false;
    private int count;
    private int progress;


    public CounterWithProgress(WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.workMode = Affirm.notNull(workMode);
        this.logger = Affirm.notNull(logger);
    }


    @SuppressLint("StaticFieldLeak")
    public void increaseBy20(){

        logger.i(TAG, "increaseBy20()");

        isBusy = true;
        notifyObservers();

        new Async<Void, Integer, Integer>(workMode) {
            @Override
            protected Integer doInBackground(Void... voids) {

                int totalIncrease = 0;

                for (int ii=0; ii<20; ii++) {
                    synchronized (this) {
                        try {
                            wait(workMode == WorkMode.SYNCHRONOUS ? 1 : 100);
                        } catch (InterruptedException e) {
                        }
                    }

                    publishProgressTask(++totalIncrease);
                }

                return totalIncrease;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

                logger.i(TAG, "-tick-");

                progress = values[0];
                notifyObservers();
            }

            @Override
            protected void onPostExecute(Integer result) {

                logger.i(TAG, "done");

                count = count + result;
                isBusy = false;
                progress = 0;
                notifyObservers();
            }

        }.executeTask((Void)null);

    }


    public boolean isBusy() {
        return isBusy;
    }

    public int getCount() {
        return count;
    }

    public int getProgress() {
        return progress;
    }
}
