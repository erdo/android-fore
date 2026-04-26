package co.early.fore.net.wrap

import co.early.fore.core.coroutine.asyncMain
import co.early.fore.core.coroutine.awaitIO
import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.threadName
import co.early.fore.core.type.Either
import co.early.fore.core.type.Either.Companion.fail
import co.early.fore.core.type.Either.Companion.success
import co.early.fore.net.MessageProvider
import kotlinx.coroutines.Deferred
import kotlinx.serialization.KSerializer

interface Wrapper<F> {
    suspend fun <S> processCallAwait(
        call: suspend () -> S
    ): Either<F, S>

    suspend fun <S, CE : MessageProvider<F>> processCallAwait(
        kSerializer: KSerializer<CE>,
        call: suspend () -> S
    ): Either<F, S>

    suspend fun <S> processCallAsync(
        call: suspend () -> S
    ): Deferred<Either<F, S>>

    suspend fun <S, CE : MessageProvider<F>> processCallAsync(
        kSerializer: KSerializer<CE>,
        call: suspend () -> S
    ): Deferred<Either<F, S>>
}

/**
 * F - Globally applicable failure message class, like an enum for example
 *
 * CE needs to implement MessageProvider&lt;F&gt; (i.e. it needs to be able to give you a failure
 * message that can be passed back to the application)
 *
 * @param errorHandler Error handler for the service (interpreting HTTP codes etc). This
 * error handler is often the same across a range of services required by the app. However, sometimes
 * the APIs all have different error behaviours (say when the service APIs have been developed
 * at different times, or by different companies, or even teams). In this case it might be easier
 * to use a separate CallProcessor instance (and ErrorHandler) for each micro service.
 * @param logger (optional: ForeDelegateHolder will choose a sensible default)
 *
 * @param <F>  The class type passed back in the event of a failure, Globally applicable
 * failure message class, like an enum for example
 */
class CallWrapper<F>(
    private val errorHandler: ErrorHandler<F>,
    private val logger: Logger? = null
) : Wrapper<F> {

    // this is for iOS target benefit which doesn't like default parameters in constructors
    constructor(errorHandler: ErrorHandler<F>) : this(errorHandler, null)

    /**
     * @param call network call to be processed
     * @param <S> Successful response body type
     */
    override suspend fun <S> processCallAwait(call: suspend () -> S): Either<F, S> {
        return processCallAsync(call).await()
    }

    /**
     * @param call network call to be processed
     * @param <S> Successful response body type
     */
    override suspend fun <S, CE : MessageProvider<F>> processCallAwait(
        kSerializer: KSerializer<CE>,
        call: suspend () -> S
    ): Either<F, S> {
        return processCallAsync(kSerializer, call).await()
    }

    /**
     * @param call network call to be processed
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
     */
    override suspend fun <S> processCallAsync(call: suspend () -> S): Deferred<Either<F, S>> {
        return doCallAsync<S, MessageProvider<F>>(null, call)
    }

    /**
     * @param call network call to be processed
     * @param <S> Successful response body type
     * @param <CE> Class of error expected from server, must implement MessageProvider&lt;F&gt;
     */
    override suspend fun <S, CE : MessageProvider<F>> processCallAsync(
        kSerializer: KSerializer<CE>,
        call: suspend () -> S
    ): Deferred<Either<F, S>> {
        return doCallAsync(kSerializer, call)
    }

    private suspend fun <S, CE : MessageProvider<F>> doCallAsync(
        kSerializer: KSerializer<CE>?,
        call: suspend () -> S
    ): Deferred<Either<F, S>> {

        Fore.getLogger(logger).v("doCallAsync() thread:" + threadName())

        return asyncMain {
            try {

                val result: S = awaitIO {

                    Fore.getLogger(logger)
                        .v("about to make call from io dispatcher, thread:" + threadName())

                    call()
                }

                Fore.getLogger(logger)
                    .v("continuing back on main dispatcher thread:" + threadName())

                success(result)

            } catch (t: Throwable) {

                Fore.getLogger(logger)
                    .w("processFailResponse() thread:${threadName()} ${t.message}")

                fail(errorHandler.handleError(t, kSerializer))
            }
        }
    }
}
