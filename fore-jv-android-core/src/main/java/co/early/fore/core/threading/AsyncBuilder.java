package co.early.fore.core.threading;

import android.annotation.SuppressLint;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.DoThisCallback;
import co.early.fore.core.callbacks.DoThisWithPayloadCallback;

/**
 * Note that this class while a lot less verbose than Async, doesn't support progress updates,
 * for that you need to use Async directly
 */
public class AsyncBuilder<Input, Result>{

    private final WorkMode workMode;

    private DoInBackgroundCallback<Input, Result> doInBackground;
    private DoThisCallback onPreExecute;
    private DoThisWithPayloadCallback<Result> onPostExecute;

    private Async<Input, Void, Result> async = null;

    public AsyncBuilder(WorkMode workMode) {
        this.workMode = Affirm.notNull(workMode);
    }

    public AsyncBuilder<Input, Result> doInBackground(DoInBackgroundCallback<Input, Result> doInBackground){
        this.doInBackground = Affirm.notNull(doInBackground);
        return this;
    }

    public AsyncBuilder<Input, Result> onPreExecute(DoThisCallback onPreExecute){
        this.onPreExecute = Affirm.notNull(onPreExecute);
        return this;
    }

    public AsyncBuilder<Input, Result> onPostExecute(DoThisWithPayloadCallback<Result> onPostExecute){
        this.onPostExecute = Affirm.notNull(onPostExecute);
        return this;
    }

    @SuppressLint("StaticFieldLeak")
    public Async<Input, Void, Result> execute(Input... inputs){

        if (async != null){
            throw new IllegalStateException("Please construct a new AsyncBuilder, as with Async these instances can only be executed once");
        }

        if (doInBackground == null) {
            throw new IllegalStateException("You must call at least doInBackground() before calling execute");
        }

        async = new Async<Input, Void, Result>(workMode) {

            @Override
            protected void onPreExecute() {
                if (onPreExecute != null) {
                    onPreExecute.doThis();
                }
            }

            @Override
            protected Result doInBackground(Input... inputs) {
                return doInBackground.doThisAndReturn(inputs);
            }

            @Override
            protected void onPostExecute(Result result) {
                if (onPostExecute != null) {
                    onPostExecute.doThis(result);
                }
            }
        };

        async.executeTask(inputs);

        return async;
    }

    public interface DoInBackgroundCallback<Input, Result> {
        Result doThisAndReturn(Input... inputs);
    }
}
