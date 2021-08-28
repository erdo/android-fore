package co.early.fore.kt.net.apollo3;

import com.apollographql.apollo3.api.ApolloResponse;

import org.jetbrains.annotations.Nullable;


/**
 *
 * @param <F> Failure message class to be used throughout the app, such as a sealed class or an enum
 */
public interface ErrorHandler<F> {
    /**
     *
     * @param t throwable that caused the error, may be null
     * @param errorResponse error response from the server, the body of which may represent a custom error, may be null
     * @return the parsed error from the server
     */
    @SuppressWarnings("rawtypes")
    F handleError(@Nullable Throwable t, @Nullable ApolloResponse errorResponse);
}
