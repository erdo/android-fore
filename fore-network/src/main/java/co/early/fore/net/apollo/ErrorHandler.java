package co.early.fore.net.apollo;

import com.apollographql.apollo.api.Response;

import org.jetbrains.annotations.Nullable;


/**
 *
 * @param <F> Failure message class to be used throughout the app, such as an enum
 */
public interface ErrorHandler<F> {
    /**
     *
     * @param t throwable that caused the error, may be null
     * @param errorResponse error response from the server, the body of which may represent a custom error, may be null
     * @return the parsed error from the server
     */
    F handleError(@Nullable Throwable t, @Nullable Response errorResponse);
}
