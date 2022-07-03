package foo.bar.example.forektorkt.api

import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.ErrorHandler
import co.early.fore.net.MessageProvider
import foo.bar.example.forektorkt.message.ErrorMessage
import foo.bar.example.forektorkt.message.ErrorMessage.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.UnknownServiceException
import java.nio.charset.CoderMalfunctionError

/**
 * You can probably use this class almost as is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the fore library
 */
class CustomGlobalErrorHandler(private val logWrapper: Logger) : ErrorHandler<ErrorMessage> {

    override suspend fun <CE : MessageProvider<ErrorMessage>> handleError(
            t: Throwable?,
            customErrorClazz: Class<CE>?
    ): ErrorMessage {

        if (t == null) {
            logWrapper.d("handling error in global error handler")
        } else {
            logWrapper.d("handling error in global error handler", t)
        }

        val errorMessage = when (t) {

            is ResponseException -> {

                //initial error type (we use ERROR_SERVER as our "try again later" message to the user
                var msg = when (t) {
                    is ClientRequestException -> { ERROR_SERVER } // in 400..499
                    is RedirectResponseException -> { ERROR_SERVER } //in 300..399
                    is ServerResponseException -> { ERROR_SERVER } //in 500..599
                    else -> { ERROR_NETWORK } //something else
                }

                val response = t.response

                logWrapper.e("handleError() HTTP:" + response.status)

                //get more specific with the error type
                msg = when (response.status.value) {
                    401 -> ERROR_SESSION_TIMED_OUT
                    400, 405 -> ERROR_CLIENT
                    429 -> ERROR_RATE_LIMITED
                    404 -> ERROR_SERVER //if this happens in prod, it's usually a server config issue
                    else -> null
                } ?: msg

                //let's get even more specifics about the error
                customErrorClazz?.let { clazz ->
                    msg = parseCustomError(msg, response, clazz)
                }

                msg
            }
            is NoTransformationFoundException -> ERROR_SERVER // content type is probably wrong, check response from server in app logs
            is SerializationException -> ERROR_SERVER //parsing issue, maybe response is not json, or does not match expected type, or is empty
            is IOException -> ERROR_NETWORK //airplane mode is on, no network coverage etc
            is UnknownServiceException -> ERROR_SECURITY_UNKNOWN //most likely https related, check for usesCleartextTraffic if required
            else -> ERROR_NETWORK
        }

        logWrapper.d("replyWithFailure() returning:$errorMessage")
        return errorMessage
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <CE : MessageProvider<ErrorMessage>> parseCustomError(
            provisionalErrorMessage: ErrorMessage,
            errorResponse: HttpResponse,
            customErrorClazz: Class<CE>
    ): ErrorMessage {

        var customError: ErrorMessage = provisionalErrorMessage

        try {

            val bodyContent = errorResponse.readText(Charsets.UTF_8)
            logWrapper.d("parseCustomError() attempting to parse this content:\n $bodyContent")
            val errorClass = Json.decodeFromString(serializer(customErrorClazz), bodyContent) as CE
            customError = errorClass.message

        } catch (t: Throwable) {

            logWrapper.e("parseCustomError() unexpected issue" + t)

            when (t) {
                is IllegalStateException, is CoderMalfunctionError -> {logWrapper.e("01")} //problem reading body text
                is SerializationException -> {logWrapper.e("02")} //parsing error, @Serializable missing, wrong error class specified etc
                is UnsupportedEncodingException -> {logWrapper.e("03")}
                is NullPointerException -> {logWrapper.e("04")}
                else -> {logWrapper.e("05")}
            }
        }

        logWrapper.d("parseCustomError() returning:$customError")
        return customError
    }
}
