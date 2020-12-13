package co.early.fore.net.apollo;


/**
 * F - Globally applicable failure message class, like an enum for example
 * S - Success pojo you expect to be returned from the API
 * CE - Custom error class that you expect from the specific API in the event of an error
 *
 * CE needs to implement MessageProvider&lt;F&gt; (i.e. it needs to be able to give you a failure
 * message that can be passed back to the application)
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 *           failure message class, like an enum for example
 */
public class ApolloCallProcessor<F> {

//    public static final String TAG = CallProcessor.class.getSimpleName();
//
//    private final ErrorHandler<F> globalErrorHandler;
//    private final Logger logger;
//
//
//    public CallProcessor(ErrorHandler<F> globalErrorHandler, Logger logger) {
//        this.globalErrorHandler = Affirm.notNull(globalErrorHandler);
//        this.logger = Affirm.notNull(logger);
//    }
//
//    /**
//     *
//     * @param call Retrofit call to be processed
//     * @param workMode how to process the call (synchronously or asynchronously)
//     * @param successCallbackWithPayload call back triggered in the event of a successful call
//     * @param failureCallbackWithPayload call back triggered in the event of a failed call
//     * @param <S> Successful response body type
//     */
//    public <S> void processCall(Call<S> call, WorkMode workMode,
//                                final SuccessCallbackWithPayload<S> successCallbackWithPayload,
//                                final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
//        doProcessCall(call, workMode, successCallbackWithPayload, failureCallbackWithPayload);
//    }
//
//
//    private <S> void doProcessCall(Call<S> call, WorkMode workMode,
//                                              final SuccessCallbackWithPayload<S> successCallbackWithPayload,
//                                              final FailureCallbackWithPayload<F> failureCallbackWithPayload){
//        Affirm.notNull(call);
//        Affirm.notNull(workMode);
//        Affirm.notNull(successCallbackWithPayload);
//        Affirm.notNull(failureCallbackWithPayload);
//
//        if (workMode == WorkMode.SYNCHRONOUS) {
//
//            Response<S> response = null;
//
//            try {
//                response = call.execute();
//            } catch (IOException e) {
//                processFailResponse(e, null, call.request(), failureCallbackWithPayload);
//                return;
//            }
//
//            processSuccessResponse(response, call.request(), successCallbackWithPayload, failureCallbackWithPayload);
//
//        } else {
//
//            call.enqueue(new retrofit2.Callback<S>() {
//
//                @Override
//                public void onResponse(Call<S> call, Response<S> response) {
//                    processSuccessResponse(response, call.request(), successCallbackWithPayload, failureCallbackWithPayload);
//                }
//
//                @Override
//                public void onFailure(Call<S> call, Throwable t) {
//                    processFailResponse(t, null, call.request(), failureCallbackWithPayload);
//                }
//            });
//        }
//    }
//
//    private <S> void processSuccessResponse(Response<S> response,
//                                            Request originalRequest,
//                                            final SuccessCallbackWithPayload<S> successCallbackWithPayload,
//                                            final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
//        if (response.isSuccessful()) {
//            successCallbackWithPayload.success(response.body());
//        } else {
//            processFailResponse(null, response, originalRequest, failureCallbackWithPayload);
//        }
//    }
//
//    private <S> void processFailResponse(Throwable t, Response errorResponse,
//                                         final FailureCallbackWithPayload<F> failureCallbackWithPayload) {
//
//        if (t != null) {
//            logger.w(TAG, "processFailResponse()", t);
//        }
//
//        failureCallbackWithPayload.fail(globalErrorHandler.handleError(t, errorResponse));
//    }

}