package co.early.fore.retrofit.coroutine


import co.early.fore.core.WorkMode
import co.early.fore.core.callbacks.FailureWithPayload
import co.early.fore.core.callbacks.SuccessWithPayload
import co.early.fore.core.logging.Logger
import co.early.fore.retrofit.ErrorHandler
import co.early.fore.retrofit.MessageProvider
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

/**
 * F - Globally applicable failure message class, like an enum for example
 * S - Success pojo you expect to be returned from the API
 * CE - Custom error class that you expect from the specific API in the event of an error
 *
 * CE needs to implement MessageProvider&lt;F&gt; (i.e. it needs to be able to give you a failure
 * message that can be passed back to the application)
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 * failure message class, like an enum for example
</F> */
class CallProcessor<F>(private val globalErrorHandler: ErrorHandler<F>, private val logger: Logger) {

    /**
     *
     * @param call Retrofit call to be processed
     * @param workMode how to process the call (synchronously or asynchronously)
     * @param successWithPayload call back triggered in the event of a successful call
     * @param failureWithPayload call back triggered in the event of a failed call
     * @param <S> Successful response body type
    </S> */
    fun <S> processCall(
            call: Call<S>, workMode: WorkMode,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>
    ) {
        doProcessCall<S, MessageProvider<F>>(call, workMode, null, successWithPayload, failureWithPayload)
    }

    /**
     *
     * @param call Retrofit call to be processed
     * @param workMode how to process the call (synchronously or asynchronously)
     * @param customErrorClazz custom error class expected from the server in the event of an error
     * @param successWithPayload call back triggered in the event of a successful call
     * @param failureWithPayload call back triggered in the event of a failed call
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
    </CE></S> */
    fun <S, CE : MessageProvider<F>> processCall(
            call: Call<S>, workMode: WorkMode, customErrorClazz: Class<CE>,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>
    ) {
        doProcessCall(call, workMode, customErrorClazz, successWithPayload, failureWithPayload)
    }


    private fun <S, CE : MessageProvider<F>> doProcessCall(
            call: Call<S>, workMode: WorkMode, customErrorClazz: Class<CE>?,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>
    ) {

        if (workMode == WorkMode.SYNCHRONOUS) {

            val response: Response<S>?

            try {
                response = call.execute()
            } catch (e: IOException) {
                processFailResponse<CE, Any>(e, null, customErrorClazz, call.request(), failureWithPayload)
                return
            }

            processSuccessResponse(response!!, customErrorClazz, call.request(), successWithPayload, failureWithPayload)

        } else {

            call.enqueue(object : retrofit2.Callback<S> {

                override fun onResponse(call: Call<S>, response: Response<S>) {
                    processSuccessResponse(response, customErrorClazz, call.request(), successWithPayload, failureWithPayload)
                }

                override fun onFailure(call: Call<S>, t: Throwable) {
                    processFailResponse<CE, Any>(t, null, customErrorClazz, call.request(), failureWithPayload)
                }
            })
        }
    }

    private fun <CE : MessageProvider<F>, S> processSuccessResponse(
            response: Response<S>, customErrorClass: Class<CE>?,
            originalRequest: Request,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>
    ) {
        if (response.isSuccessful) {
            successWithPayload(response.body())
        } else {
            processFailResponse<CE, Any>(null, response, customErrorClass, originalRequest, failureWithPayload)
        }
    }

    private fun <CE : MessageProvider<F>, S> processFailResponse(
            t: Throwable?, errorResponse: Response<*>?, customErrorClass: Class<CE>?,
            originalRequest: Request,
            failureWithPayload: FailureWithPayload<F>
    ) {

        if (t != null) {
            logger.w(LOG_TAG, "processFailResponse()", t)
        }

        failureWithPayload(globalErrorHandler.handleError(t, errorResponse, customErrorClass, originalRequest))
    }

    companion object {
        val LOG_TAG = CallProcessor::class.java.simpleName
    }

}
