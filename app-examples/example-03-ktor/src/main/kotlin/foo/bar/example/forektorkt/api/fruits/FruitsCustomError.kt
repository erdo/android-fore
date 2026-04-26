package foo.bar.example.forektorkt.api.fruits

import co.early.fore.net.MessageProvider
import foo.bar.example.forektorkt.message.ErrorMessage
import foo.bar.example.forektorkt.message.ErrorMessage.ERROR_MISC
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
 * example http://www.mocky.io/v2/59ef2a6c2e00002a1a1c5dea
 *
 */
@Serializable
class FruitsCustomError(private val errorCode: ErrorCode?) : MessageProvider<ErrorMessage> {

    @Serializable
    enum class ErrorCode constructor(val errorMessage: ErrorMessage) {

        @SerialName("FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT")
        LOGIN_CREDENTIALS_INCORRECT(ErrorMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT),

        @SerialName("FRUIT_USER_LOCKED")
        USER_LOCKED(ErrorMessage.ERROR_FRUIT_USER_LOCKED),

        @SerialName("FRUIT_USER_NOT_ENABLED")
        USER_NOT_ENABLED(ErrorMessage.ERROR_FRUIT_USER_NOT_ENABLED);

    }

    override val message: ErrorMessage = errorCode?.errorMessage ?: ERROR_MISC
}
