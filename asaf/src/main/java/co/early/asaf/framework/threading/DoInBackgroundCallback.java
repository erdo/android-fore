package co.early.asaf.framework.threading;

/**
 *
 */
public interface DoInBackgroundCallback<Input, Result> {
    Result doThisAndReturn(Input... inputs);
}
