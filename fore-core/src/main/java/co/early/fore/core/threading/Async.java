package co.early.fore.core.threading;

import android.os.AsyncTask;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;


public abstract class Async<Input, Progress, Result> extends AsyncTask<Input, Progress, Result> {

    protected final WorkMode workMode;

    /**
     * In all cases you need to use the executeTask() method rather than
     * execute(), or you will get regular AsyncTask behaviour which can't then be
     * mocked for tests.
     * <p>
     * In addition, if you want to use publishProgress(), you need to call publishProgressTask()
     * instead or again you will get regular AsyncTask behaviour which can't then be mocked for tests.
     * Sorry. I'm not the one who made those AsyncTask methods final :/
     * <p>
     *
     * @param workMode if SYNCHRONOUS, onPreExecute(), doInBackground(), onProgressUpdate(), onPostExecute()
     * will all be run synchronously on the same thread, so the executeTask() call will block.
     * This means that if you call cancel() sometime later on in the code, when you are running
     * tests (i.e. using SYNCHRONOUS), the whole thing will have already completed right the way
     * through to onPostExecute() by the time you get to the cancel() part of your code of course.
     * <p>
     * If you're planning on doing any network io or anything that will take a long time in
     * doInBackground() don't forget you also need to mock out that kind of behaviour when you are
     * doing tests, or at best your tests will take ages, at worst your tests will be dependent on
     * a network ad fail when it's not available.
     * <p>
     * if ASYNCHRONOUS, a regular AsyncTask will be used to perform the execute
     * method as expected.
     *
     */
    public Async(WorkMode workMode) {
        this.workMode = Affirm.notNull(workMode);
    }

    public void executeTask(Input... params) {
        if (workMode == WorkMode.SYNCHRONOUS) {
            onPreExecute();
            onPostExecute(doInBackground(params));
        } else {
            super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }

    protected void publishProgressTask(Progress... values) {
        if (workMode == WorkMode.SYNCHRONOUS) {
            onProgressUpdate(values);
        } else {
            super.publishProgress(values);
        }
    }

    void doPublishProgress(Progress... values){
        publishProgress(values);
    }

    @Override
    protected void onPreExecute() {
        //no opp
    }

    @Override
    protected void onPostExecute(Result result) {
        //no opp
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        //no opp
    }
}
