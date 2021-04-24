package co.early.fore.core.callbacks;

public interface SuccessCallbackWithPayload<S> {
    void success(S successResponse);
}
