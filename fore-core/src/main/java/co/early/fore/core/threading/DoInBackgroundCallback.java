package co.early.fore.core.threading;


public interface DoInBackgroundCallback<Input, Result> {
    Result doThisAndReturn(Input... inputs);
}
