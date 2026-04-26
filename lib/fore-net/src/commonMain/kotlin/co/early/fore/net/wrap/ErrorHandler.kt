package co.early.fore.net.wrap

import co.early.fore.net.MessageProvider
import kotlinx.serialization.KSerializer

/**
 *
 * @param <F> Failure message class to be used throughout the app (or the data layer in the case of
 * clean architecture) such as an enum or a data class
 */
interface ErrorHandler<F> {
    /**
     *
     * @param t throwable that caused the error
     * @param KSerializer serializer for the custom error class expected from the errorResponse, may be null
     * @param <CE> class type of the custom error if specified
     * @return the parsed error from the server
    </CE> */
    suspend fun <CE : MessageProvider<F>> handleError(t: Throwable, kSerializer: KSerializer<CE>?): F
}
