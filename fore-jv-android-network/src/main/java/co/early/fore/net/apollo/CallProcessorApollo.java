package co.early.fore.net.apollo;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import com.apollographql.apollo.api.Error;
import java.util.List;
import java.util.Map;

import co.early.fore.core.Affirm;
import co.early.fore.core.callbacks.FailureCallbackWithPayload;
import co.early.fore.core.callbacks.SuccessCallbackWithPayload;
import co.early.fore.core.logging.Logger;

/**
 * F - Globally applicable failure message class, like an enum for example
 * S - Success pojo you expect to be returned from the API
 * CE - Custom error class that you expect from the specific API in the event of an error
 * <p>
 * CE needs to implement MessageProvider&lt;F&gt; (i.e. it needs to be able to give you a failure
 * message that can be passed back to the application)
 *
 * Testing: Apollo is not setup to be able to run calls in a synchronous manner (unlike Retrofit),
 * for this reason when testing the fore ApolloCallProcessor we need to use count down latches
 * and there are no WorkMode options specified here
 *
 * @param <F> The class type passed back in the event of a failure, Globally applicable
 *            failure message class, like an enum for example
 */
public class CallProcessorApollo<F> implements ApolloCaller<F> {

    public static final String TAG = CallProcessorApollo.class.getSimpleName();

    private final ErrorHandler<F> globalErrorHandler;
    private final Logger logger;
    private final boolean allowPartialSuccesses;

    /**
     * @param globalErrorHandler does what it says
     * @param logger does what it says
     */
    public CallProcessorApollo(ErrorHandler<F> globalErrorHandler, Logger logger) {
        this(globalErrorHandler, logger, false);
    }

    /**
     * @param globalErrorHandler does what it says
     * @param logger does what it says
     * @param allowPartialSuccesses (defaults to false) The GraphQL spec allows for success responses
     * with qualified errors, like this: https://spec.graphql.org/draft/#example-90475 if true, you will
     * receive these responses as successes, together with the list of partial errors that were attached
     * in the response. If allowPartialSuccesses=false, these types of responses will be delivered as
     * errors and the successful parts of the response will be dropped. Setting it to true is going to
     * make keeping your domain layer and api layers separate a lot harder, and apart from in some highly
     * optimised situations I'd recommend you keep it set to false.
     */
    public CallProcessorApollo(ErrorHandler<F> globalErrorHandler, Logger logger, boolean allowPartialSuccesses) {
        this.globalErrorHandler = Affirm.notNull(globalErrorHandler);
        this.logger = Affirm.notNull(logger);
        this.allowPartialSuccesses = allowPartialSuccesses;
    }

    /**
     * @param call                       Apollo call to be processed
     * @param successCallbackWithPayload call back triggered in the event of a successful call
     * @param failureCallbackWithPayload call back triggered in the event of a failed call
     * @param <S>                        Successful response body type
     */
    @Override
    public <S> void processCall(ApolloCall<S> call,
                                final SuccessCallbackWithPayload<SuccessResult<S>> successCallbackWithPayload,
                                final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
        Affirm.notNull(call);
        Affirm.notNull(successCallbackWithPayload);
        Affirm.notNull(failureCallbackWithPayload);

        call.enqueue(new ApolloCall.Callback<S>() {

            @Override
            public void onResponse(@NotNull Response<S> response) {
                processSuccessResponse(response, successCallbackWithPayload, failureCallbackWithPayload);
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                processFailResponse(e, null, failureCallbackWithPayload);
            }
        });
    }

    private <S> void processSuccessResponse(Response<S> response,
                                            final SuccessCallbackWithPayload<SuccessResult<S>> successCallbackWithPayload,
                                            final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
        S data = response.getData();

        if (data != null) {
            if (!response.hasErrors() || allowPartialSuccesses) {
                successCallbackWithPayload.success(
                        new SuccessResult<S>(
                                data,
                                response.getErrors(),
                                response.getExtensions(),
                                response.isFromCache()
                        )
                );
            } else {
                processFailResponse(null, response, failureCallbackWithPayload);
            }
        } else {
            processFailResponse(null, response, failureCallbackWithPayload);
        }
    }

    private <S> void processFailResponse(Throwable t, Response<S> errorResponse,
                                         final FailureCallbackWithPayload<F> failureCallbackWithPayload) {

        if (t != null) {
            logger.w(TAG, "processFailResponse()", t);
        }

        failureCallbackWithPayload.fail(globalErrorHandler.handleError(t, errorResponse));
    }

    public static class SuccessResult<S> {

        final S data;
        final List<Error> partialErrors;
        final Map<String, Object> extensions;
        final Boolean isFromCache;

        public SuccessResult(S data, List<Error> partialErrors, Map<String, Object> extensions, Boolean isFromCache) {
            this.data = data;
            this.partialErrors = partialErrors;
            this.extensions = extensions;
            this.isFromCache = isFromCache;
        }
    }
}
