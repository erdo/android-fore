package co.early.fore.kt.net.apollo3

import com.apollographql.apollo3.api.ApolloResponse

/**
 * @param <F> Failure message class to be used throughout the app, such as a sealed class or an enum
 */
interface ErrorHandler<F> {

    /**
     * @param t throwable that caused the error, may be null
     * @param errorResponse response from the server, the body of which may represent custom error(s), may be null
     * @return representative failure message
     */
    fun handleError(t: Throwable?, errorResponse: ApolloResponse<*>?): F

    /**
     * @param errors partial graphql error responses from the server, may be null or empty
     * @return list of representative failure messages
     */
    fun handlePartialErrors(errors: List<com.apollographql.apollo3.api.Error?>?): List<F>
}
