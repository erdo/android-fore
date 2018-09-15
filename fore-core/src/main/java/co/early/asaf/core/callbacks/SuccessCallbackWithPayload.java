package co.early.asaf.core.callbacks;

public interface SuccessCallbackWithPayload<S> {
    void success(S successResponse);
}