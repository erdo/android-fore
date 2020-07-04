package foo.bar.example.foreretrofitkt.api.fruits

import co.early.fore.retrofit.MessageProvider
import com.google.gson.annotations.SerializedName
import foo.bar.example.foreretrofitkt.message.UserMessage
import foo.bar.example.foreretrofitkt.message.UserMessage.ERROR_MISC

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
class FruitsCustomError(private val errorCode: ErrorCode?) : MessageProvider<UserMessage> {

    enum class ErrorCode constructor(val userMessage: UserMessage) {

        @SerializedName("FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT")
        LOGIN_CREDENTIALS_INCORRECT(UserMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT),

        @SerializedName("FRUIT_USER_LOCKED")
        USER_LOCKED(UserMessage.ERROR_FRUIT_USER_LOCKED),

        @SerializedName("FRUIT_USER_NOT_ENABLED")
        USER_NOT_ENABLED(UserMessage.ERROR_FRUIT_USER_NOT_ENABLED);

    }

    override fun getMessage(): UserMessage {
        return errorCode?.userMessage ?: ERROR_MISC
    }

}
