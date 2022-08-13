package co.early.fore.kt.net.apollo

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.coroutine.asyncMain
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.type.Either
import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.net.apollo.ErrorHandler
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.Deferred
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface CallerApollo<F> {
    suspend fun <S> processCallAwait(
        call: () -> ApolloCall<S>
    ): Either<F, CallWrapperApollo.SuccessResult<S>>
    suspend fun <S> processCallAsync(
        call: () -> ApolloCall<S>
    ): Deferred<Either<F, CallWrapperApollo.SuccessResult<S>>>
}

/**
 * @param errorHandler Error handler for the service (interpreting HTTP codes, GraphQL errors
 * etc). This error handler is often the same across a range of services required by the app.
 * However, sometimes the APIs all have different error behaviours (say when the service APIs have
 * been developed at different times, or by different teams or different third parties). In this
 * case it might be easier to use a separate CallProcessorApollo instance (and ErrorHandler) for
 * each micro service.
 * @param logger (optional: ForeDelegateHolder will choose a sensible default)
 * @param workMode (optional: ForeDelegateHolder will choose a sensible default) Testing: Apollo
 * library is NOT setup to be able to run calls in a synchronous manner (unlike Retrofit), for this
 * reason when testing the fore ApolloCallProcessor we still need to use count down latches even with
 * workMode = SYNCHRONOUS
 * @param allowPartialSuccesses (defaults to false) The GraphQL spec allows for success responses
 * with qualified errors, like this: https://spec.graphql.org/draft/#example-90475 if true, you will
 * receive these responses as successes, together with the list of partial errors that were attached
 * in the response. If allowPartialSuccesses=false, these types of responses will be delivered as
 * errors and the successful parts of the response will be dropped. Setting it to true is going to
 * make keeping your domain layer and api layers separate a lot harder, and apart from in some highly
 * optimised situations I'd recommend you keep it set to false.
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 * failure message class, like an enum for example
 */
class CallWrapperApollo<F>(
    private val errorHandler: ErrorHandler<F>,
    private val logger: Logger? = null,
    private val workMode: WorkMode? = null,
    private val allowPartialSuccesses: Boolean = false
) : CallerApollo<F> {

    data class SuccessResult<S>(
            val data: S,
            val partialErrors: List<Error> = listOf(),
            val extensions: Map<String, Any?> = hashMapOf(),
            val isFromCache: Boolean = false
    )

    /**
     * @param call functional type that returns a fresh instance of the ApolloCall to be processed
     * @param <S> Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @returns Either<F, SuccessResult<S>>
     */
    override suspend fun <S> processCallAwait(call: () -> ApolloCall<S>): Either<F, SuccessResult<S>> {
        return processCallAsync(call).await()
    }

    /**
     * @param call functional type that returns a fresh instance of the ApolloCall to be processed
     * @param <S> Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @returns Deferred<Either<F, SuccessResult<S>>>
     */
    override suspend fun <S> processCallAsync(call: () -> ApolloCall<S>): Deferred<Either<F, SuccessResult<S>>> {

        return asyncMain(Fore.getWorkMode(workMode)) {
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
                success(SuccessResult(data, response.errors
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
            Fore.getLogger(logger).e("processFailResponse() t:" + Thread.currentThread(), t)
        }

        return fail(errorHandler.handleError(t, errorResponse))
    }
}
