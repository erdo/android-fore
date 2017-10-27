package co.early.asaf.retrofit;


import java.io.IOException;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.callbacks.FailureCallbackWithPayload;
import co.early.asaf.core.callbacks.SuccessCallbackWithPayload;
import co.early.asaf.core.logging.Logger;
import retrofit2.Call;
import retrofit2.Response;

/**
 * F - Globally applicable failure message class, like an enum for example
 * S - Success pojo you expect to be returned from the API
 * CE - Custom error class that you expect from the specific API in the event of an error
 *
 * CE needs to implement MessageProvider&lt;F&gt; (i.e. it needs to be able to give you a failure
 * message that can be passed back to the application)
 *
 * Note: this callProcessor won't enable you to access the headers in the response, if you need that
 * you'll have to write your own, or use a global response interceptor
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 *           failure message class, like an enum for example
 */
public class CallProcessor<F> {

    public static final String TAG = CallProcessor.class.getSimpleName();

    private final ErrorHandler<F> globalErrorHandler;
    private final Logger logger;


    public CallProcessor(ErrorHandler<F> globalErrorHandler, Logger logger) {
        this.globalErrorHandler = Affirm.notNull(globalErrorHandler);
        this.logger = Affirm.notNull(logger);
    }

    /**
     *
     * @param call Retrofit call to be processed
     * @param workMode how to process the call (synchronously or asynchronously)
     * @param successCallbackWithPayload call back triggered in the event of a successful call
     * @param failureCallbackWithPayload call back triggered in the event of a failed call
     * @param <S> Successful response body type
     */
    public <S> void processCall(Call<S> call, WorkMode workMode,
                                final SuccessCallbackWithPayload<S> successCallbackWithPayload,
                                final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
        doProcessCall(call, workMode, null, successCallbackWithPayload, failureCallbackWithPayload);
    }

    /**
     *
     * @param call Retrofit call to be processed
     * @param workMode how to process the call (synchronously or asynchronously)
     * @param customErrorClazz custom error class expected from the server in the event of an error
     * @param successCallbackWithPayload call back triggered in the event of a successful call
     * @param failureCallbackWithPayload call back triggered in the event of a failed call
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
     */
    public <S, CE extends MessageProvider<F>> void processCall(Call<S> call, WorkMode workMode, final Class<CE> customErrorClazz,
                                           final SuccessCallbackWithPayload<S> successCallbackWithPayload,
                                           final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
        doProcessCall(call, workMode, Affirm.notNull(customErrorClazz), successCallbackWithPayload, failureCallbackWithPayload);
    }


    private <S, CE extends MessageProvider<F>> void doProcessCall(Call<S> call, WorkMode workMode, final Class<CE> customErrorClazz,
                                              final SuccessCallbackWithPayload<S> successCallbackWithPayload,
                                              final FailureCallbackWithPayload<F> failureCallbackWithPayload){
        Affirm.notNull(call);
        Affirm.notNull(workMode);
        Affirm.notNull(successCallbackWithPayload);
        Affirm.notNull(failureCallbackWithPayload);

        if (workMode == WorkMode.SYNCHRONOUS) {

            Response<S> response = null;

            try {
                response = call.execute();
            } catch (IOException e) {
                processFailResponse(e, null, customErrorClazz, failureCallbackWithPayload);
                return;
            }

            processSuccessResponse(response, customErrorClazz, successCallbackWithPayload, failureCallbackWithPayload);

        } else {

            call.enqueue(new retrofit2.Callback<S>() {

                @Override
                public void onResponse(Call<S> call, Response<S> response) {
                    processSuccessResponse(response, customErrorClazz, successCallbackWithPayload, failureCallbackWithPayload);
                }

                @Override
                public void onFailure(Call<S> call, Throwable t) {
                    processFailResponse(t, null, customErrorClazz, failureCallbackWithPayload);
                }
            });
        }
    }

    private <CE extends MessageProvider<F>, S> void processSuccessResponse(Response<S> response, Class<CE> customErrorClass,
                                            final SuccessCallbackWithPayload<S> successCallbackWithPayload,
                                            final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
        if (response.isSuccessful()) {
            successCallbackWithPayload.success(response.body());
        } else {
            processFailResponse(null, response, customErrorClass, failureCallbackWithPayload);
        }
    }

    private <CE extends MessageProvider<F>, S> void processFailResponse(Throwable t, Response errorResponse, Class<CE>  customErrorClass,
                                                                        final FailureCallbackWithPayload<F> failureCallbackWithPayload) {

        if (t != null) {
            logger.w(TAG, "processFailResponse()", t);
        }

        failureCallbackWithPayload.fail(globalErrorHandler.handleError(t, errorResponse, customErrorClass));
    }

}
