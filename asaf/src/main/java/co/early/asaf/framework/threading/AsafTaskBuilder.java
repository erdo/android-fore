package co.early.asaf.framework.threading;

import co.early.asaf.framework.Affirm;
import co.early.asaf.framework.WorkMode;
import co.early.asaf.framework.callbacks.DoThisCallback;
import co.early.asaf.framework.callbacks.DoThisWithPayloadCallback;
import co.early.asaf.framework.callbacks.DoThisWithVarargPayloadCallback;

/**
 *
 */
public class AsafTaskBuilder <Input, Progress, Result>{

    private final WorkMode workMode;

    private final DoInBackgroundCallback<Input, Progress, Result> doInBackground;
    private DoThisCallback onPreExecute;
    private DoThisWithVarargPayloadCallback<Progress> onProgressUpdate;
    private DoThisWithPayloadCallback<Result> onPostExecute;

    private AsafTask<Input, Progress, Result> asafTask = null;

    public AsafTaskBuilder(WorkMode workMode, DoInBackgroundCallback<Input, Progress, Result> doInBackground) {
        this.workMode = Affirm.notNull(workMode);
        this.doInBackground = Affirm.notNull(doInBackground);
    }

    public AsafTaskBuilder<Input, Progress, Result> onPreExecute(DoThisCallback onPreExecute){
        this.onPreExecute = Affirm.notNull(onPreExecute);
        return this;
    }

    public AsafTaskBuilder<Input, Progress, Result> onProgressUpdate(DoThisWithVarargPayloadCallback<Progress> onProgressUpdate){
        this.onProgressUpdate = Affirm.notNull(onProgressUpdate);
        return this;
    }

    public AsafTaskBuilder<Input, Progress, Result> onPostExecute(DoThisWithPayloadCallback<Result> onPostExecute){
        this.onPostExecute = Affirm.notNull(onPostExecute);
        return this;
    }

    public AsafTask<Input, Progress, Result> execute(Input... inputs){

        if (asafTask == null){
            throw new IllegalStateException("Please construct a new AsafTaskBuilder, as with AsyncTask these instances can only be executed once");
        }else {

            asafTask = new AsafTask<Input, Progress, Result>(workMode) {

                @Override
                protected void onPreExecute() {
                    if (onPreExecute != null) {
                        onPreExecute.doThis();
                    }
                }

                @Override
                protected Result doInBackground(Input... inputs) {

                    PublishProgressCallback<Progress> progressPublisher = new PublishProgressCallback<Progress>() {
                        @Override
                        public void publishProgress(Progress... payload) {
                            asafTask.doPublishProgress(payload);
                        }
                    };

                    return doInBackground.doThisAndReturn(progressPublisher, inputs);
                }

                @Override
                protected void onProgressUpdate(Progress... values) {
                    if (onProgressUpdate != null) {
                        onProgressUpdate.doThis(values);
                    }
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

}
