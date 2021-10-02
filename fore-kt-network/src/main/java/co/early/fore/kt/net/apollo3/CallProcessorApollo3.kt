package co.early.fore.kt.net.apollo3

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.coroutine.asyncMain
import co.early.fore.kt.core.coroutine.awaitIO
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.logging.Logger
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.ExecutionContext
import com.apollographql.apollo3.api.Operation
import kotlinx.coroutines.Deferred

@ExperimentalStdlibApi
interface Apollo3Caller<F> {
    suspend fun <S : Operation.Data> processCallAwait(
        call: suspend () -> ApolloResponse<S>
    ): Either<F, CallProcessorApollo3.SuccessResult<S>>
    suspend fun <S : Operation.Data> processCallAsync(
        call: suspend () -> ApolloResponse<S>
    ): Deferred<Either<F, CallProcessorApollo3.SuccessResult<S>>>
}

/**
 * @property errorHandler Error handler for the service (interpreting HTTP codes, GraphQL errors
 * etc). This error handler is often the same across a range of services required by the app.
 * However, sometimes the APIs all have different error behaviours (say when the service APIs have
 * been developed at different times, or by different teams or different third parties). In this
 * case it might be easier to use a separate CallProcessorApollo3 instance (and ErrorHandler) for
 * each micro service.
 * @property logger (optional: ForeDelegateHolder will choose a sensible default)
 * @property workMode (optional: ForeDelegateHolder will choose a sensible default)
 * @property allowPartialSuccesses (defaults to false) The GraphQL spec allows for success responses
 * with qualified errors, like this: https://spec.graphql.org/draft/#example-90475 if true, you will
 * receive these responses as successes, together with the list of partial errors that were attached
 * in the response. If allowPartialSuccesses=false, these types of responses will be delivered as
 * errors and the successful parts of the response will be dropped. Setting it to true is going to
 * make keeping your domain layer and api layers separate a lot harder, and apart from in some highly
 * optimised situations I'd recommend you keep it set to false.
 *
 * @param F  The class type passed back in the event of a failure, Globally applicable
 * failure message class, like an enum for example
 */
@ExperimentalStdlibApi
class CallProcessorApollo3<F>(
    private val errorHandler: ErrorHandler<F>,
    private val logger: Logger? = null,
    private val workMode: WorkMode? = null,
    private val allowPartialSuccesses: Boolean = false
) : Apollo3Caller<F> {

    data class SuccessResult<S>(
        val data: S,
        val partialErrors: List<Error> = listOf(),
        val extensions: Map<String, Any?> = hashMapOf(),
        val executionContext: ExecutionContext = ExecutionContext.Empty
    )

    /**
     * @param call functional type that returns the result of an ApolloRequest
     * @param S Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @return Either<F, SuccessResult<S>>
     */
    override suspend fun <S : Operation.Data> processCallAwait(call: suspend () -> ApolloResponse<S>): Either<F, SuccessResult<S>> {
        return processCallAsync(call).await()
    }

    /**
     * @param call functional type that returns the result of an ApolloRequest
     * @param S Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @return Deferred<Either<F, SuccessResult<S>>>
     */
    override suspend fun <S : Operation.Data> processCallAsync(call: suspend () -> ApolloResponse<S>): Deferred<Either<F, SuccessResult<S>>> {

        return asyncMain(ForeDelegateHolder.getWorkMode(workMode)) {
            try {

                val result: ApolloResponse<S> = awaitIO(ForeDelegateHolder.getWorkMode(workMode)) {

                    ForeDelegateHolder.getLogger(logger).d("about to make call from io dispatcher, thread:" + Thread.currentThread())

                    call()
                }

                processSuccessResponse(result)

            } catch (t: Throwable) {
                processFailResponse(t, null)
            }
        }
    }

    private fun <S : Operation.Data> processSuccessResponse(
            response: ApolloResponse<S>
    ): Either<F, SuccessResult<S>> {

        val data: S? = response.data

        return if (data != null) {
            if (!response.hasErrors() || allowPartialSuccesses) {
                Either.right(SuccessResult(data, response.errors
                        ?: mutableListOf(), response.extensions, response.executionContext))
            } else {
                processFailResponse(null, response)
            }
        } else {
            processFailResponse(null, response)
        }
    }

    private fun <S> processFailResponse(
            t: Throwable?, errorResponse: ApolloResponse<*>?
    ): Either<F, SuccessResult<S>> {

        if (t != null) {
            ForeDelegateHolder.getLogger(logger).e("processFailResponse() t:" + Thread.currentThread(), t)
        }

        return Either.left(errorHandler.handleError(t, errorResponse))
    }
}
