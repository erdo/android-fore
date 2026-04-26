package foo.bar.example.forektorkt.api

import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.Logger
import co.early.fore.net.MessageProvider
import co.early.fore.net.wrap.ErrorHandler
import foo.bar.example.forektorkt.message.ErrorMessage
import foo.bar.example.forektorkt.message.ErrorMessage.*
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * KMP-compatible error handler for REST API calls.
 *
 * Something like this will be suitable for most HTTP services,
 * consider customizing for the particularities of your own server side
 *
 * Retry logic is handled in KtorBuilder
 */
class GlobalErrorHandler(private val logWrapper: Logger? = null) : ErrorHandler<ErrorMessage> {

    override suspend fun <CE : MessageProvider<ErrorMessage>> handleError(
        t: Throwable,
        kSerializer: KSerializer<CE>?,
    ): ErrorMessage {

        Fore.getLogger(logWrapper).e("handling error in global error handler", t)

        val errorMessage = when (t) {

            is ResponseException -> {

                //initial error type
                var msg = when (t) {
                    is ClientRequestException -> {
                        ERROR_SERVER
                    } // in 400..499
                    is RedirectResponseException -> {
                        ERROR_SERVER
                    } //in 300..399
                    is ServerResponseException -> {
                        ERROR_SERVER
                    } //in 500..599
                    else -> {
                        ERROR_NETWORK
                    } //something else
                }

                val response = t.response

                Fore.getLogger(logWrapper).e("handleError() HTTP:" + response.status)

                //get more specific with the error type
                msg = when (response.status.value) {
                    401 -> ERROR_SESSION_TIMED_OUT
                    400, 405 -> ERROR_CLIENT
                    429 -> ERROR_RATE_LIMITED
                    404 -> ERROR_SERVER //if this happens in prod, it's usually a server config issue
                    else -> null
                } ?: msg

                //let's get even more specifics about the error
                kSerializer?.let { serializer ->
                    msg = parseCustomError(msg, response, serializer)
                }

                msg
            }

            is NoTransformationFoundException -> ERROR_SERVER // content type is probably wrong, check response from server in app logs
            is SerializationException -> ERROR_SERVER //parsing issue, maybe response is not json, or does not match expected type, or is empty
            is TimeoutCancellationException -> ERROR_NETWORK // network timeout
            is CancellationException -> ERROR_CLIENT // user cancellation, lifecycle takedown

            else -> {
                when {
                    isProbablyNetworkError(t) -> ERROR_NETWORK
                    isProbablySecurityError(t) -> ERROR_SECURITY_UNKNOWN
                    else -> ERROR_NETWORK
                }
            }
        }

        Fore.getLogger(logWrapper).w("replyWithFailure() returning:$errorMessage")
        return errorMessage
    }

    // this works a little better in KMP where we don't have unified exceptions
    // because of platform differences
    private fun isProbablyNetworkError(throwable: Throwable): Boolean {
        val message = throwable.message?.lowercase() ?: ""
        val className = throwable::class.simpleName?.lowercase() ?: ""

        return message.contains("network") ||
                message.contains("connection") ||
                message.contains("timeout") ||
                message.contains("unreachable") ||
                className.contains("network") ||
                className.contains("connection") ||
                className.contains("timeout") ||
                className.contains("io")
    }

    // this works a little better in KMP where we don't have unified exceptions
    // because of platform differences
    private fun isProbablySecurityError(throwable: Throwable): Boolean {
        val message = throwable.message?.lowercase() ?: ""
        val className = throwable::class.simpleName?.lowercase() ?: ""

        return message.contains("ssl") ||
                message.contains("tls") ||
                message.contains("certificate") ||
                message.contains("security") ||
                className.contains("ssl") ||
                className.contains("tls") ||
                className.contains("security")
    }


    @Suppress("UNCHECKED_CAST")
    private suspend fun <CE : MessageProvider<ErrorMessage>> parseCustomError(
        provisionalErrorMessage: ErrorMessage,
        errorResponse: HttpResponse,
        customErrorSerializer: KSerializer<CE>
    ): ErrorMessage {

        var customError: ErrorMessage = provisionalErrorMessage

        try {

            val bodyContent = errorResponse.bodyAsText()
            Fore.getLogger(logWrapper)
                .w("parseCustomError() attempting to parse this content:\n $bodyContent")

            val errorClass = Json.decodeFromString(customErrorSerializer, bodyContent)
            customError = errorClass.message

        } catch (t: Throwable) {

            Fore.getLogger(logWrapper).e("parseCustomError() unexpected issue $t")

            when (t) {
                is IllegalStateException -> {
                    Fore.getLogger(logWrapper).e("01") // problem reading body text
                }
                is SerializationException -> {
                    Fore.getLogger(logWrapper).e("02") // parsing error, @Serializable missing, wrong error class specified etc
                }
                is IllegalArgumentException -> {
                    Fore.getLogger(logWrapper).e("03") // encoding or argument issues
                }
                is NullPointerException -> {
                    Fore.getLogger(logWrapper).e("04")
                }
                else -> {
                    Fore.getLogger(logWrapper).e("05")
                }
            }
        }

        Fore.getLogger(logWrapper).w("parseCustomError() returning:$customError")
        return customError
    }
}
