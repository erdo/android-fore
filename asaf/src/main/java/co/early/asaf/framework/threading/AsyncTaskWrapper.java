package co.early.asaf.framework.threading;

import android.os.AsyncTask;

import co.early.asaf.framework.Affirm;
import co.early.asaf.framework.WorkMode;


public abstract class AsyncTaskWrapper<Input, Progress, Result> extends AsyncTask<Input, Progress, Result> {

    protected final WorkMode workMode;

    /**
     * In all cases you need to use the executeWrapper() method rather than
     * execute(), or you will get regular AsycTask behaviour which can't then be
     * mocked for tests
     * <p/>
     * if SYNCHRONOUS, onPreExecute(), doInBackground(), onPostExecute()
     * will be done synchronously on the same thread, so the executeWrapper call will block.
     * In this case cancelAfter() will be ignored!
     * <p/>
     * if ASYNCHRONOUS, a regular AsyncTask will be used to perform the execute
     * method as expected.
     *
     * @param workMode
     */
    public AsyncTaskWrapper(WorkMode workMode) {
        this.workMode = Affirm.notNull(workMode);
    }

    public void executeWrapper(Input... params) {
        if (workMode == WorkMode.SYNCHRONOUS) {
            onPreExecute();
            onPostExecute(doInBackground(params));
        } else {
            super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }

    @Override
    protected void onPreExecute() {
        //not calling super because that now throws a runtime (not mocked) exception during tests. yay android
    }

    @Override
    protected void onPostExecute(Result result) {
        //not calling super because that now throws a runtime (not mocked) exception during tests. yay android
    }
}
