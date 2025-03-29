package co.early.fore.net.wrap.apollo

import co.early.fore.core.type.Either
import co.early.fore.net.wrap.apollo.CallWrapperApollo.SuccessResult
import co.early.fore.net.wrap.apollo.WrapperApollo
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * @param fakeResponses For simple test cases, just specify one fakeResponse here which will
 * always be returned whenever this class is called. You can also specify multiple responses,
 * in that case the responses will be made in order from first to last, once the last
 * fakeResponse has been used it will be re-used for all subsequent calls.
 *
 * Be careful with the Success(S) generic of the fake responses you list here, there'll be a
 * ClassCastException if it doesn't match what is expected by the client code
 *
 * Example use (see example apps in repo)
 *
 * fakeCallWrapperApollo = FakeCallWrapperApollo(
 *     success1.toFakeSuccess(),
 *     success2.toFakeSuccess(),
 *     ErrorMessage.ERROR_NETWORK.toFakeFail(),
 *     success3.toFakeSuccess(),
 *     RuntimeException().toFakeThrowable()
 * )
 *
 */
class FakeCallWrapperApollo<F>(
    vararg fakeResponses: Either<Throwable, Either<F, SuccessResult<*, F>>>,
) : WrapperApollo<F> {

    private val pendingFakeResponses: MutableList<Either<Throwable, Either<F, SuccessResult<*, F>>>> =
        fakeResponses.toMutableList()
    private val mutex = Mutex()

    init {
        require(fakeResponses.isNotEmpty()){
            "you must include at least one fake response in the constructor"
        }
    }

    /**
     * @param call functional type that returns the result of an ApolloRequest
     * @param S Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @return Either<F, SuccessResult<S>>
     */
    override suspend fun <S : Operation.Data> processCallAwait(call: suspend () -> ApolloResponse<S>): Either<F, SuccessResult<S, F>> {
        return processCallAsync(call).await()
    }

    /**
     * @param call functional type that returns the result of an ApolloRequest
     * @param S Success class you expect to be returned from the call (or Unit for an empty response)
     *
     * @return Deferred<Either<F, SuccessResult<S>>>
     */
    override suspend fun <S : Operation.Data> processCallAsync(call: suspend () -> ApolloResponse<S>): Deferred<Either<F, SuccessResult<S, F>>> {

        mutex.withLock {
            val fakeResponse = if (pendingFakeResponses.size > 1) {
                pendingFakeResponses.removeFirst()
            } else {
                pendingFakeResponses.first()
            }

            return when (fakeResponse) {
                is Either.Fail<*> -> throw fakeResponse.value as Throwable
                is Either.Success<*> -> {
                    CompletableDeferred(
                        @Suppress("UNCHECKED_CAST") (fakeResponse.value as Either<F, SuccessResult<S, F>>)
                    )
                }
            }
        }
    }
}
