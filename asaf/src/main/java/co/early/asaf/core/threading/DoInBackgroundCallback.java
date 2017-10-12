package co.early.asaf.core.threading;

/**
 *
 */
public interface DoInBackgroundCallback<Input, Result> {
    Result doThisAndReturn(Input... inputs);
}
