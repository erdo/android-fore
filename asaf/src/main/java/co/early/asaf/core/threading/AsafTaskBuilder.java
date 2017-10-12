package co.early.asaf.core.threading;


import co.early.asaf.core.callbacks.DoThisWithPayloadCallback;
import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.DoThisCallback;

/**
 * Note that this class while a lot less verbose than AsafTask, doesn't support progress updates,
 * for that you need to use AsafTask directly
 */
public class AsafTaskBuilder <Input, Result>{

    private final WorkMode workMode;

    private DoInBackgroundCallback<Input, Result> doInBackground;
    private DoThisCallback onPreExecute;
    private DoThisWithPayloadCallback<Result> onPostExecute;

    private AsafTask<Input, Void, Result> asafTask = null;

    public AsafTaskBuilder(WorkMode workMode) {
        this.workMode = Affirm.notNull(workMode);
    }

    public AsafTaskBuilder<Input, Result> doInBackground(DoInBackgroundCallback<Input, Result> doInBackground){
        this.doInBackground = Affirm.notNull(doInBackground);
        return this;
    }

    public AsafTaskBuilder<Input, Result> onPreExecute(DoThisCallback onPreExecute){
        this.onPreExecute = Affirm.notNull(onPreExecute);
        return this;
    }

    public AsafTaskBuilder<Input, Result> onPostExecute(DoThisWithPayloadCallback<Result> onPostExecute){
        this.onPostExecute = Affirm.notNull(onPostExecute);
        return this;
    }

    public AsafTask<Input, Void, Result> execute(Input... inputs){

        if (asafTask != null){
            throw new IllegalStateException("Please construct a new AsafTaskBuilder, as with AsyncTask these instances can only be executed once");
        }

        if (doInBackground == null) {
            throw new IllegalStateException("You must call at least doInBackground() before calling execute");
        }

        asafTask = new AsafTask<Input, Void, Result>(workMode) {

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

        asafTask.executeTask(inputs);

        return asafTask;
    }


}
