package foo.bar.example.foreapollo3.api

import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.net.apollo3.ErrorHandler
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.ApolloParseException
import com.apollographql.apollo3.exception.JsonEncodingException
import foo.bar.example.foreapollo3.message.ErrorMessage
import foo.bar.example.foreapollo3.message.ErrorMessage.*

/**
 * You can probably use this class almost as it is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the fore library
 */
@ExperimentalStdlibApi
class CustomGlobalErrorHandler(private val logger: Logger?) : ErrorHandler<ErrorMessage> {

    override fun handleError(
            t: Throwable?,
            errorResponse: ApolloResponse<*>?
    ): ErrorMessage {

        ForeDelegateHolder.getLogger(logger).e("handleError() t:$t errorResponse:$errorResponse")

        val message = parseSpecificErrors(errorResponse) ?: parseGeneralErrors(t)

        ForeDelegateHolder.getLogger(logger).e("handleError() returning:$message")

        return message
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
                    ForeDelegateHolder.getLogger(logger).e("handleError() HTTP:" + it.statusCode + " " + it.message)
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

    private fun parseSpecificErrors(errorResponse: ApolloResponse<*>?): ErrorMessage? {
        return errorResponse?.let {
            // amazingly GraphQL never had an error code in its standard error
            // block so it usually gets put under the extensions block like this:
            // https://spec.graphql.org/draft/#example-fce18
            it.errors?.first()?.customAttributes?.get("extensions")?.let { extensions ->
                (extensions as? Map<*, *>)?.get("code")?.let { code ->
                    ErrorMessage.createFromName(code as? String)
                }
            }
        }
    }

}
