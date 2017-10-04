package co.early.asaf.framework.threading;

/**
 *
 */
public interface DoInBackgroundCallback<Input, Progress, Result> {
    Result doThisAndReturn(PublishProgressCallback<Progress> progressPublisher, Input... inputs);
}
