package co.early.asaf.retrofit;

import okhttp3.Request;
import retrofit2.Response;

/**
 *
 * @param <M> Error message class to be used throughout the app, such as an enum
 */
public interface ErrorHandler<M> {
    /**
     *
     * @param t throwable that caused the error
     * @param errorResponse error response from the server, the body of which may represent a custom error
     * @param customErrorClazz custom error class expected from the errorResponse
     * @param <CE> class type of the custom error
     * @return the parsed error from the server
     */
    <CE extends MessageProvider<M>> M handleError(Throwable t, Response errorResponse, Class<CE> customErrorClazz, Request originalRequest);
}
