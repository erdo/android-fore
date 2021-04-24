package co.early.fore.net.apollo;

import com.apollographql.apollo.ApolloCall;

import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallbackWithPayload;

public interface ApolloCaller<F> {
    <S> void processCall(ApolloCall<S> call,
                         SuccessCallbackWithPayload<CallProcessorApollo.SuccessResult<S>> successCallbackWithPayload,
                         FailureCallbackWithPayload<F> failureCallbackWithPayload);
}
