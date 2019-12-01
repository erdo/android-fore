package co.early.fore.retrofit.coroutine


import co.early.fore.core.WorkMode
import co.early.fore.core.callbacks.FailureWithPayload
import co.early.fore.core.callbacks.SuccessWithPayload
import co.early.fore.core.logging.Logger
import co.early.fore.retrofit.ErrorHandler
import co.early.fore.retrofit.MessageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.io.IOException

/**
 * F - Globally applicable failure message class, like an enum for example
 * S - Success pojo you expect to be returned from the API (or Unit for an empty response)
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
     * @param workMode how to process the call (synchronously or asynchronously)
     * @param successWithPayload call back triggered in the event of a successful call
     * @param failureWithPayload call back triggered in the event of a failed call
     * @param call Retrofit call to be processed
     * @param <S> Successful response body type
    </S> */
    fun <S> processCall(
            workMode: WorkMode,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>,
            call: suspend () -> Response<S>
    ) {
        doProcessCall<S, MessageProvider<F>>(call, workMode, null, successWithPayload, failureWithPayload)
    }

    /**
     *
     * @param workMode how to process the call (synchronously or asynchronously)
     * @param customErrorClazz custom error class expected from the server in the event of an error
     * @param successWithPayload call back triggered in the event of a successful call
     * @param failureWithPayload call back triggered in the event of a failed call
     * @param call Retrofit call to be processed
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
    </CE></S> */
    fun <S, CE : MessageProvider<F>> processCall(
            workMode: WorkMode, customErrorClazz: Class<CE>,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>,
            call: suspend () -> Response<S>
    ) {
        doProcessCall(call, workMode, customErrorClazz, successWithPayload, failureWithPayload)
    }


    private fun <S, CE : MessageProvider<F>> doProcessCall(
            call: suspend () -> Response<S>, workMode: WorkMode, customErrorClazz: Class<CE>?,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>
    ) {

        if (workMode == WorkMode.SYNCHRONOUS) {

            val response: Response<S>

            try {
                response = runBlocking {
                    call()
                }
            } catch (e: IOException) {
                processFailResponse<CE, Any>(e, null, customErrorClazz, failureWithPayload)
                return
            }

            processSuccessResponse(response, customErrorClazz, successWithPayload, failureWithPayload)

        } else {

            val deferredResult: Deferred<Response<S>> = CoroutineScope(Dispatchers.IO).async {
                call()
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    processSuccessResponse(deferredResult.await(), customErrorClazz, successWithPayload, failureWithPayload)
                } catch (t: Throwable) {
                    processFailResponse<CE, Any>(t, null, customErrorClazz, failureWithPayload)
                }
            }
        }
    }

    private fun <CE : MessageProvider<F>, S> processSuccessResponse(
            response: Response<S>, customErrorClass: Class<CE>?,
            successWithPayload: SuccessWithPayload<S>,
            failureWithPayload: FailureWithPayload<F>
    ) {
        val resp: S? = response.body()

        if (response.isSuccessful && resp != null) {
            successWithPayload(resp)
        } else {
            processFailResponse<CE, Any>(null, response, customErrorClass, failureWithPayload)
        }
    }

    private fun <CE : MessageProvider<F>, S> processFailResponse(
            t: Throwable?, errorResponse: Response<*>?, customErrorClass: Class<CE>?,
            failureWithPayload: FailureWithPayload<F>
    ) {

        if (t != null) {
            logger.w(LOG_TAG, "processFailResponse()", t)
        }

        failureWithPayload(globalErrorHandler.handleError(t, errorResponse, customErrorClass, null))
    }

    companion object {
        val LOG_TAG = CallProcessor::class.java.simpleName
    }
}
