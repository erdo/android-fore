package foo.bar.example.foreretrofitcoroutine.api

import co.early.fore.core.logging.Logger
import co.early.fore.retrofit.ErrorHandler
import co.early.fore.retrofit.MessageProvider
import com.google.gson.Gson
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import foo.bar.example.foreretrofitcoroutine.message.UserMessage.ERROR_CLIENT
import foo.bar.example.foreretrofitcoroutine.message.UserMessage.ERROR_MISC
import foo.bar.example.foreretrofitcoroutine.message.UserMessage.ERROR_NETWORK
import foo.bar.example.foreretrofitcoroutine.message.UserMessage.ERROR_SERVER
import foo.bar.example.foreretrofitcoroutine.message.UserMessage.ERROR_SESSION_TIMED_OUT
import okhttp3.Request
import retrofit2.Response
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException

/**
 * You can probably use this class almost as it is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the fore library
 */
class CustomGlobalErrorHandler(private val logWrapper: Logger) : ErrorHandler<UserMessage> {


    override fun <CE : MessageProvider<UserMessage>> handleError(
            t: Throwable?,
            errorResponse: Response<*>?,
            customErrorClazz: Class<CE>?,
            originalRequest: Request?
    ): UserMessage {

        var message = ERROR_MISC

        if (errorResponse != null) {

            logWrapper.e(LOG_TAG, "handleError() HTTP:" + errorResponse.code())

            when (errorResponse.code()) {

                401 -> message = ERROR_SESSION_TIMED_OUT

                400, 405 -> message = ERROR_CLIENT

                //realise 404 is officially a "client" error, but in my experience if it happens in prod it is usually the fault of the server ;)
                404, 500, 503 -> message = ERROR_SERVER
            }

            if (customErrorClazz != null) {
                //let's see if we can get more specifics about the error
                message = parseCustomError(message, errorResponse, customErrorClazz)
            }

        } else {//non HTTP error, probably some connection problem, but might be JSON parsing related also

            logWrapper.e(LOG_TAG, "handleError() throwable:$t")

            if (t != null) {

                message = if (t is com.google.gson.stream.MalformedJsonException) {
                    ERROR_SERVER
                } else {
                    ERROR_NETWORK
                }
                t.printStackTrace()
            }
        }


        logWrapper.e(LOG_TAG, "handleError() returning:$message")

        return message
    }


    private fun <CE : MessageProvider<UserMessage>> parseCustomError(
            provisionalErrorMessage: UserMessage,
            errorResponse: Response<*>,
            customErrorClazz: Class<CE>
    ): UserMessage {

        val gson = Gson()

        var customError: CE? = null

        try {
            customError = gson.fromJson(InputStreamReader(errorResponse.errorBody()!!.byteStream(), "UTF-8"), customErrorClazz)
        } catch (e: UnsupportedEncodingException) {
            logWrapper.e(LOG_TAG, "parseCustomError() No more error details", e)
        } catch (e: IllegalStateException) {
            logWrapper.e(LOG_TAG, "parseCustomError() No more error details", e)
        } catch (e: NullPointerException) {
            logWrapper.e(LOG_TAG, "parseCustomError() No more error details", e)
        } catch (e: com.google.gson.JsonSyntaxException) {//the server probably gave us something that is not JSON
            logWrapper.e(LOG_TAG, "parseCustomError() Problem parsing customServerError", e)
            return ERROR_SERVER
        }

        return if (customError == null) {
            provisionalErrorMessage
        } else {
            customError.message
        }
    }

    companion object {
        private val LOG_TAG = CustomGlobalErrorHandler::class.java.simpleName
    }
}
