package co.early.fore.retrofit;

import androidx.annotation.Nullable;
import okhttp3.Request;
import retrofit2.Response;

/**
 *
 * @param <F> Failure message class to be used throughout the app, such as an enum
 */
public interface ErrorHandler<F> {
    /**
     *
     * @param t throwable that caused the error, may be null
     * @param errorResponse error response from the server, the body of which may represent a custom error, may be null
     * @param customErrorClazz custom error class expected from the errorResponse, may be null
     * @param originalRequest in case it's needed (if you need access to the headers for example), may be null
     * @param <CE> class type of the custom error, may be null
     * @return the parsed error from the server
     */
    <CE extends MessageProvider<F>> F handleError(@Nullable Throwable t, @Nullable Response errorResponse, @Nullable Class<CE> customErrorClazz, @Nullable Request originalRequest);
}
