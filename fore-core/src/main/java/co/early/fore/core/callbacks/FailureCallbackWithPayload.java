package co.early.fore.core.callbacks;

public interface FailureCallbackWithPayload<F> {
    void fail(F failureMessage);
}
