package co.early.fore.net.wrap

import co.early.fore.core.type.Either
import co.early.fore.net.MessageProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


fun <F, S> S.toFakeSuccess(): Either<Throwable, Either<F, S>> = Either.success(Either.success(this))
fun <F, S> F.toFakeFail(): Either<Throwable, Either<F, S>> = Either.success(Either.fail(this))
fun <F, S> Throwable.toFakeThrowable(): Either<Throwable, Either<F, S>> = Either.fail(this)


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
 * fakeCallWrapper = FakeCallWrapper(
 *     success1.toFakeSuccess(),
 *     success2.toFakeSuccess(),
 *     ErrorMessage.ERROR_NETWORK.toFakeFail(),
 *     success3.toFakeSuccess(),
 *     RuntimeException().toFakeThrowable()
 * )
 */
class FakeCallWrapper<F>(
    vararg fakeResponses: Either<Throwable, Either<F, *>>,
) : Wrapper<F> {

    private val pendingFakeResponses: MutableList<Either<Throwable, Either<F, *>>> =
        fakeResponses.toMutableList()
    private val mutex = Mutex()

    init {
        require(fakeResponses.isNotEmpty()){
            "you must include at least one fake response in the constructor"
        }
    }

    override suspend fun <S> processCallAwait(call: suspend () -> S): Either<F, S> {
        return processCallAsync(call).await()
    }

    override suspend fun <S, CE : MessageProvider<F>> processCallAwait(
        customErrorKlazz: kotlin.reflect.KClass<CE>,
        call: suspend () -> S
    ): Either<F, S> {
        return processCallAsync(customErrorKlazz, call).await()
    }

    override suspend fun <S> processCallAsync(call: suspend () -> S): Deferred<Either<F, S>> {
        return doCallAsync<S, MessageProvider<F>>(null, call)
    }

    override suspend fun <S, CE : MessageProvider<F>> processCallAsync(
        customErrorKlazz: kotlin.reflect.KClass<CE>,
        call: suspend () -> S
    ): Deferred<Either<F, S>> {
        return doCallAsync(customErrorKlazz, call)
    }

    private suspend fun <S, CE : MessageProvider<F>> doCallAsync(
        customErrorKlazz: kotlin.reflect.KClass<CE>?,
        call: suspend () -> S
    ): Deferred<Either<F, S>> {
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
                        @Suppress("UNCHECKED_CAST") (fakeResponse as Either<F, S>)
                    )
                }
            }
        }
    }
}
