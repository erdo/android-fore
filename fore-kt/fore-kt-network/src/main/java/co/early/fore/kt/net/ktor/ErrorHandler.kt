package co.early.fore.kt.net.ktor

import co.early.fore.net.MessageProvider

/**
 *
 * @param <F> Failure message class to be used throughout the app (or the data layer in the case of
 * clean architecture) such as an enum or a data class
 */
interface ErrorHandler<F> {
    /**
     *
     * @param t throwable that caused the error
     * @param customErrorClazz custom error class expected from the errorResponse, may be null
     * @param <CE> class type of the custom error if specified
     * @return the parsed error from the server
    </CE> */
    suspend fun <CE : MessageProvider<F>> handleError(t: Throwable, customErrorKlazz: kotlin.reflect.KClass<CE>?): F
}
