package co.early.asaf.core.callbacks;

public interface FailureCallbackWithPayload<F> {
    void fail(F failureMessage);
}
