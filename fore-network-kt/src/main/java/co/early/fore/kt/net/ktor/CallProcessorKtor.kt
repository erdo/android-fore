package co.early.fore.kt.net.ktor

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.coroutine.asyncMain
import co.early.fore.kt.core.coroutine.awaitIO
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.net.MessageProvider
import kotlinx.coroutines.Deferred

/**
 * F - Globally applicable failure message class, like an enum for example
 * S - Success pojo you expect to be returned from the API (or Unit for an empty response)
 * CE - Custom error class that you expect from the specific API in the event of an error
 *
 * CE needs to implement MessageProvider&lt;F&gt; (i.e. it needs to be able to give you a failure
 * message that can be passed back to the application)
 *
 * @param globalErrorHandler Error handler for the service (interpreting HTTP codes etc). This
 * error handler is often the same across a range of services required by the app. However, sometimes
 * the APIs all have different error behaviours (say when the service APIs have been developed
 * at different times, or by different companies, or even teams). In this case a separate CallProcessor
 * instance (and ErrorHandler) will be required for each micro service.
 * @param workMode (optional: ForeDelegateHolder will choose a sensible default)  SYNCHRONOUS means
 * everything is run sequentially in a blocking manner
 * and on whatever thread the caller is on (suitable for running unit tests for example).
 * ASYNCHRONOUS gives you the co-routine behaviour you would expect - for this class that
 * means network requests are run on Dispatchers.IO
 * @param logger (optional: ForeDelegateHolder will choose a sensible default)
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 * failure message class, like an enum for example
 */
class CallProcessorKtor<F>(
        private val globalErrorHandler: ErrorHandler<F>,
        private val workMode: WorkMode? = null,
        private val logger: Logger? = null
) {

    /**
     * @param call Retrofit call to be processed
     * @param <S> Successful response body type
     */
    suspend fun <S> processCallAwait(call: suspend () -> S): Either<F, S> {
        return processCallAsync(call).await()
    }

    /**
     * @param call Retrofit call to be processed
     * @param <S> Successful response body type
     */
    suspend fun <S, CE : MessageProvider<F>> processCallAwait(
            customErrorClazz: Class<CE>,
            call: suspend () -> S
    ): Either<F, S> {
        return processCallAsync(customErrorClazz, call).await()
    }

    /**
     * @param call Retrofit call to be processed
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
     */
    suspend fun <S> processCallAsync(call: suspend () -> S): Deferred<Either<F, S>> {
        return doCallAsync<S, MessageProvider<F>>(null, call)
    }

    /**
     * @param call Retrofit call to be processed
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
     */
    suspend fun <S, CE : MessageProvider<F>> processCallAsync(
            customErrorClazz: Class<CE>,
            call: suspend () -> S
    ): Deferred<Either<F, S>> {
        return doCallAsync(customErrorClazz, call)
    }

    private suspend fun <S, CE : MessageProvider<F>> doCallAsync(
            customErrorClazz: Class<CE>?,
            call: suspend () -> S
    ): Deferred<Either<F, S>> {

        ForeDelegateHolder.getLogger(logger).d("doCallAsync() thread:" + Thread.currentThread())

        return asyncMain(ForeDelegateHolder.getWorkMode(workMode)) {
            try {

                val result: S = awaitIO(ForeDelegateHolder.getWorkMode(workMode)) {

                    ForeDelegateHolder.getLogger(logger).d("about to make call from io dispatcher, thread:" + Thread.currentThread())

                    call()
                }

                ForeDelegateHolder.getLogger(logger).d("continuing back on main dispatcher thread:" + Thread.currentThread())

                Either.right(result)

            } catch (t: Throwable) {

                ForeDelegateHolder.getLogger(logger).w("processFailResponse() thread:${Thread.currentThread()} ${t.message}")

                Either.left(globalErrorHandler.handleError(t, customErrorClazz))
            }
        }
    }
}
