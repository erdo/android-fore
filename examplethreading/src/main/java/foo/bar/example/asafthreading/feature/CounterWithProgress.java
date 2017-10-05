package foo.bar.example.asafthreading.feature;


import co.early.asaf.framework.WorkMode;
import co.early.asaf.framework.logging.Logger;
import co.early.asaf.framework.observer.ObservableImp;
import co.early.asaf.framework.threading.AsafTask;

/**
 *
 */
public class CounterWithProgress extends ObservableImp{

    private final WorkMode workMode;

    private boolean isBusy = false;
    private int count;
    private int progress;


    public CounterWithProgress(WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.workMode = workMode;
    }


    public void increaseBy20(){

        isBusy = true;
        notifyObservers();

        new AsafTask<Void, Integer, Integer>(workMode) {
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
                progress = values[0];
                notifyObservers();
            }

            @Override
            protected void onPostExecute(Integer result) {
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
