package foo.bar.example.foreapollo3.api

import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.net.apollo3.ErrorHandler
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.ApolloParseException
import com.apollographql.apollo3.exception.JsonEncodingException
import foo.bar.example.foreapollo3.message.ErrorMessage
import foo.bar.example.foreapollo3.message.ErrorMessage.*

/**
 * You can probably use this class almost as it is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes, GraphQL error formats etc, hence it's not
 * in the fore library
 */
class CustomGlobalErrorHandler(private val logger: Logger?) :
    ErrorHandler<ErrorMessage> {

    override fun handleError(
        t: Throwable?,
        errorResponse: ApolloResponse<*>?
    ): ErrorMessage {

        Fore.getLogger(logger).e("handleError() t:$t errorResponse:$errorResponse")

        val message = parseSpecificError(errorResponse?.errors?.first()) ?: parseGeneralErrors(t)

        Fore.getLogger(logger).e("handleError() returning:$message")

        return message
    }

    override fun handlePartialErrors(errors: List<Error?>?): List<ErrorMessage> {
        return errors?.mapNotNull {
            parseSpecificError(it)
        } ?: emptyList()
    }

    private fun parseGeneralErrors(t: Throwable?): ErrorMessage {
        return t?.let {
            when (it) {
                is java.lang.IllegalStateException -> ERROR_ALREADY_EXECUTED
                is ApolloParseException -> ERROR_SERVER
                is JsonEncodingException -> ERROR_SERVER
                is ApolloNetworkException -> ERROR_NETWORK
                is java.net.UnknownServiceException -> ERROR_SECURITY_UNKNOWN
                is java.net.SocketTimeoutException -> ERROR_NETWORK
                is ApolloHttpException -> {
                    Fore.getLogger(logger)
                        .e("handleError() HTTP:" + it.statusCode + " " + it.message)
                    when (it.statusCode) {
                        401 -> ERROR_SESSION_TIMED_OUT
                        400, 405 -> ERROR_CLIENT
                        429 -> ERROR_RATE_LIMITED
                        //realise 404 is officially a "client" error, but in my experience if it happens in prod it is usually the fault of the server ;)
                        404, 500, 503 -> ERROR_SERVER
                        else -> ERROR_MISC
                    }
                }
                else -> ERROR_MISC
            }
        } ?: ERROR_MISC
    }

    private fun parseSpecificError(error: Error?): ErrorMessage? {
        // amazingly GraphQL never had an error code in its standard error
        // block so it usually gets put under the extensions block like this:
        // https://spec.graphql.org/draft/#example-8b658
        return error?.extensions?.let { extensions ->
            (extensions as? Map<*, *>)?.get("code")?.let { code ->
                ErrorMessage.createFromName(code as? String)
            }
        }
    }
}
