package foo.bar.example.foreretrofitkt.api

import co.early.fore.kt.core.logging.Logger
import co.early.fore.net.MessageProvider
import com.google.gson.Gson
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import foo.bar.example.foreretrofitkt.message.ErrorMessage.*
import okhttp3.Request
import retrofit2.Response
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException

/**
 * You can probably use this class almost as it is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the fore library
 */
class CustomGlobalErrorHandler(private val logWrapper: Logger) : co.early.fore.net.retrofit2.ErrorHandler<ErrorMessage> {

    override fun <CE : MessageProvider<ErrorMessage>> handleError(
            t: Throwable?,
            errorResponse: Response<*>?,
            customErrorClazz: Class<CE>?,
            originalRequest: Request?
    ): ErrorMessage {

        var message = ERROR_MISC

        if (errorResponse != null) {

            logWrapper.e("handleError() HTTP:" + errorResponse.code())

            when (errorResponse.code()) {

                401 -> message = ERROR_SESSION_TIMED_OUT

                400, 405 -> message = ERROR_CLIENT

                429 -> message = ERROR_RATE_LIMITED

                //realise 404 is officially a "client" error, but in my experience if it happens in prod it is usually the fault of the server ;)
                404, 500, 503 -> message = ERROR_SERVER
            }

            if (customErrorClazz != null) {
                //let's see if we can get more specifics about the error
                message = parseCustomError(message, errorResponse, customErrorClazz)
            }

        } else {//non HTTP error, probably some connection problem, but might be JSON parsing related also

            logWrapper.e("handleError() throwable:$t")

            if (t != null) {

                message = when (t) {
                    is com.google.gson.stream.MalformedJsonException -> ERROR_SERVER
                    is java.net.UnknownServiceException -> ERROR_SECURITY_UNKNOWN
                    else -> ERROR_NETWORK
                }
                t.printStackTrace()
            }
        }


        logWrapper.e("handleError() returning:$message")

        return message
    }


    private fun <CE : MessageProvider<ErrorMessage>> parseCustomError(
            provisionalErrorMessage: ErrorMessage,
            errorResponse: Response<*>,
            customErrorClazz: Class<CE>
    ): ErrorMessage {

        val gson = Gson()

        var customError: CE? = null

        try {
            customError = gson.fromJson(InputStreamReader(errorResponse.errorBody()!!.byteStream(), "UTF-8"), customErrorClazz)
        } catch (e: UnsupportedEncodingException) {
            logWrapper.e("parseCustomError() No more error details", e)
        } catch (e: IllegalStateException) {
            logWrapper.e("parseCustomError() No more error details", e)
        } catch (e: NullPointerException) {
            logWrapper.e("parseCustomError() No more error details", e)
        } catch (e: com.google.gson.JsonSyntaxException) {//the server probably gave us something that is not JSON
            logWrapper.e("parseCustomError() Problem parsing customServerError", e)
            return ERROR_SERVER
        }

        return customError?.message ?: provisionalErrorMessage
    }
}
