package co.early.fore.kt.apollo


import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.coroutine.asyncMain
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.apollo.ErrorHandler
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.Deferred
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @param globalErrorHandler Error handler for the service (interpreting HTTP codes, GraphQL errors
 * etc). This error handler is often the same across a range of services required by the app.
 * However, sometimes the APIs all have different error behaviours (say when the service APIs have
 * been developed at different times, or by different teams or different third parties). In this
 * case a separate ApolloCallProcessor instance (and ErrorHandler) will be required for each micro
 * service.
 * @param logger (optional: ForeDelegateHolder will choose a sensible default)
 * @param allowPartialSuccesses (defaults to false) The GraphQL spec allows for success responses
 * with qualified errors, like this: https://spec.graphql.org/draft/#example-90475 if true, you will
 * receive these responses as successes, together with the list of partial errors that were attached
 * in the response. If allowPartialSuccesses=false, these types of responses will be delivered as
 * errors and the successful parts of the response will be dropped. Setting it to true is going to
 * make keeping your domain layer and api layers separate a lot harder, and apart from in some highly
 * optimised situations I'd recommend you keep it set to false.
 *
 * Testing: Apollo library is NOT setup to be able to run calls in a synchronous manner (unlike
 * Retrofit), for this reason the fore ApolloCallProcessor does not accept a WorkMode parameter to
 * switch the operation to Synchronous mode for unit tests. The correct way to test Apollo calls
 * is to make use of the ApolloClient.idleCallback() function (see the sample app in the fore
 * repo for example test strategies)
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 * failure message class, like an enum for example
 */
class CallProcessor<F>(
        private val globalErrorHandler: ErrorHandler<F>,
        private val logger: Logger?,
        private val allowPartialSuccesses: Boolean = false
) {

    data class SuccessResult<S>(
        val data: S,
        val partialErrors: List<Error>,
        val extensions: Map<String, Any?>,
        val isFromCache: Boolean
    )

    /**
     * @param call functional type that returns a fresh instance of the ApolloCall to be processed
     * @param <S> Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @returns Either<F, SuccessResult<S>>
     */
    suspend fun <S> processCallAwait(call: () -> ApolloCall<S>): Either<F, SuccessResult<S>> {
        return processCallAsync(call).await()
    }

    /**
     * @param call functional type that returns a fresh instance of the ApolloCall to be processed
     * @param <S> Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @returns Deferred<Either<F, SuccessResult<S>>>
     */
    suspend fun <S> processCallAsync(call: () -> ApolloCall<S>): Deferred<Either<F, SuccessResult<S>>> {

        return asyncMain(WorkMode.ASYNCHRONOUS) {
            try {
                suspendCoroutine { continuation ->
                    call().enqueue(object : ApolloCall.Callback<S>() {
                        override fun onResponse(response: Response<S>) {
                            continuation.resume(processSuccessResponse(response))
                        }
                        override fun onFailure(e: ApolloException) {
                            continuation.resume(processFailResponse(e, null))
                        }
                    })
                }
            } catch (t: Throwable) {
                ForeDelegateHolder.getLogger(logger).e("Has the ApolloCall already been executed? you cannot use an ApolloCall more than once")
                processFailResponse(t, null)
            }
        }
    }

    private fun <S> processSuccessResponse(
            response: Response<S>
    ): Either<F, SuccessResult<S>> {

        val data: S? = response.data

        return if (data != null) {
            if (!response.hasErrors() || allowPartialSuccesses) {
                Either.right(SuccessResult(data, response.errors
                        ?: mutableListOf(), response.extensions, response.isFromCache))
            } else {
                processFailResponse(null, response)
            }
        } else {
            processFailResponse(null, response)
        }
    }

    private fun <S> processFailResponse(
            t: Throwable?, errorResponse: Response<*>?
    ): Either<F, SuccessResult<S>> {

        if (t != null) {
            ForeDelegateHolder.getLogger(logger).w("processFailResponse() t:" + Thread.currentThread(), t)
        }

        return Either.left(globalErrorHandler.handleError(t, errorResponse))
    }
}
