package foo.bar.example.foreretrofitkt.api.fruits

import co.early.fore.net.MessageProvider
import com.google.gson.annotations.SerializedName
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import foo.bar.example.foreretrofitkt.message.ErrorMessage.ERROR_MISC

/**
 *
 * <Code>
 *
 * The server returns custom error codes in this form:
 *
 * {
 * "errorCode":"FRUIT_USER_LOCKED"
 * }
 *
 * </Code>
 *
 *
 */
class FruitsCustomError(private val errorCode: ErrorCode?) : MessageProvider<ErrorMessage> {

    enum class ErrorCode constructor(val errorMessage: ErrorMessage) {

        @SerializedName("FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT")
        LOGIN_CREDENTIALS_INCORRECT(ErrorMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT),

        @SerializedName("FRUIT_USER_LOCKED")
        USER_LOCKED(ErrorMessage.ERROR_FRUIT_USER_LOCKED),

        @SerializedName("FRUIT_USER_NOT_ENABLED")
        USER_NOT_ENABLED(ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED);

    }

    override fun getMessage(): ErrorMessage {
        return errorCode?.errorMessage ?: ERROR_MISC
    }

}
