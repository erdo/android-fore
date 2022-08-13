package co.early.fore.net.retrofit2;

import co.early.fore.core.WorkMode;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallbackWithPayload;
import co.early.fore.net.MessageProvider;
import retrofit2.Call;

public interface Retrofit2Caller<F> {
    <S> void processCall(Call<S> call, WorkMode workMode,
                         SuccessCallbackWithPayload<S> successCallbackWithPayload,
                         FailureCallbackWithPayload<F> failureCallbackWithPayload);

    <S, CE extends MessageProvider<F>> void processCall(Call<S> call, WorkMode workMode, Class<CE> customErrorClazz,
                                                        SuccessCallbackWithPayload<S> successCallbackWithPayload,
                                                        FailureCallbackWithPayload<F> failureCallbackWithPayload);
}
