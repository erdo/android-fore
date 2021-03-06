package foo.bar.example.foredb.feature.bossmode;


import android.annotation.SuppressLint;

import java.util.Random;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.threading.Async;
import co.early.fore.core.time.SystemTimeWrapper;
import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.db.todoitems.RandomTodoCreator;
import foo.bar.example.foredb.feature.todoitems.TodoItem;
import foo.bar.example.foredb.feature.todoitems.TodoListModel;

/**
 * Your boss randomly adds todos to your list over a period of time {@link #startBossModeFor(int)}
 */
public class BossMode extends ObservableImp{

    public static final String LOG_TAG = BossMode.class.getSimpleName();

    //notice how we use the TodoListModel, we don't go directly to the db layer
    private final TodoListModel todoListModel;
    private final SystemTimeWrapper systemTimeWrapper;
    private final WorkMode workMode;
    private final Logger logger;

    private boolean bossModeOn = false;
    private int progressPercent;

    private static final int STEPS = 100;
    private static final Random RANDOM = new Random();
    private static final String BOSS = App.getInst().getString(R.string.todo_boss);


    public BossMode(TodoListModel todoListModel, SystemTimeWrapper systemTimeWrapper, WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.todoListModel = Affirm.notNull(todoListModel);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        this.workMode = Affirm.notNull(workMode);
        this.logger = Affirm.notNull(logger);
    }


    @SuppressLint("StaticFieldLeak")
    public void startBossModeFor(final int durationMs){

        logger.i(LOG_TAG, "startBossModeFor() durationMs:" + durationMs);

        bossModeOn = true;
        notifyObservers();

        new Async<Void, Integer, Void>(workMode) {
            @Override
            protected Void doInBackground(Void... voids) {

                int count = 1;

                for (int ii=0; ii<STEPS; ii++) {

                    synchronized (this) {
                        try {
                            wait(workMode == WorkMode.SYNCHRONOUS ? 1 : durationMs/STEPS);
                        } catch (InterruptedException e) {
                        }
                    }

                    if(RANDOM.nextInt(6)<1) { //about 20% of the time the boss creates a todoitem
                        todoListModel.add(new TodoItem(systemTimeWrapper.currentTimeMillis(), BOSS + RandomTodoCreator.createLabel()));
                    }

                    publishProgressTask(++count);
                }

                return (Void)null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

                logger.i(LOG_TAG, "-tick- " + values[0]);

                progressPercent = values[0];
                notifyObservers();
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                logger.i(LOG_TAG, "boss mode finished");

                bossModeOn = false;
                progressPercent = 0;
                notifyObservers();
            }

        }.executeTask((Void)null);

    }

    public boolean isBossModeOn() {
        return bossModeOn;
    }

    public int getProgressPercent() {
        return progressPercent;
    }
}
