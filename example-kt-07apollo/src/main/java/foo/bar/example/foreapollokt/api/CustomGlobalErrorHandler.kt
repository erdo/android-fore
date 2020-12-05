package foo.bar.example.foreapollokt.api

import co.early.fore.kt.core.logging.Logger
import co.early.fore.net.apollo.ErrorHandler
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloHttpException
import foo.bar.example.foreapollokt.message.ErrorMessage
import foo.bar.example.foreapollokt.message.ErrorMessage.*

/**
 * You can probably use this class almost as it is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the fore library
 */
class CustomGlobalErrorHandler(private val logger: Logger?) : ErrorHandler<ErrorMessage> {

    override fun handleError(
            t: Throwable?,
            errorResponse: Response<*>?
    ): ErrorMessage {

        val message = parseSpecificErrors(errorResponse) ?: parseGeneralErrors(t)

        ForeDelegateHolder.getLogger(logger).e("handleError() returning:$message")
        return message
    }

    private fun parseGeneralErrors(t: Throwable?) : ErrorMessage {
        return t?.let {
            when (it) {
                is ApolloHttpException -> {

                    ForeDelegateHolder.getLogger(logger).e("handleError() HTTP:" + it.code() + " " + it.message())

                    when (it.code()) {
                        401 -> ERROR_SESSION_TIMED_OUT
                        400, 405 -> ERROR_CLIENT
                        429 -> ERROR_RATE_LIMITED
                        //realise 404 is officially a "client" error, but in my experience if it happens in prod it is usually the fault of the server ;)
                        404, 500, 503 -> ERROR_SERVER
                        else -> ERROR_MISC
                    }
                }
                is java.lang.IllegalStateException -> ERROR_ALREADY_EXECUTED
                is com.apollographql.apollo.exception.ApolloParseException -> ERROR_SERVER
                is java.net.UnknownServiceException -> ERROR_SECURITY_UNKNOWN
                is java.net.SocketTimeoutException -> ERROR_NETWORK
                else -> ERROR_MISC
            }
        } ?: ERROR_MISC
    }

    private fun parseSpecificErrors(errorResponse: Response<*>?) :  ErrorMessage? {
        return errorResponse?.let {
            // amazingly GraphQL never had an error code in its standard error
            // block so it usually gets put under the extensions block like this:
            // https://spec.graphql.org/draft/#example-fce18
            it.errors?.first()?.customAttributes?.get("code")?.let { code ->
                ErrorMessage.createFromName(code as? String)
            }
        }
    }

}
