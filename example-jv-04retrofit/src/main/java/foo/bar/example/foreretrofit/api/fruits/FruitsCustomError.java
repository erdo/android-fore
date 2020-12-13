package foo.bar.example.foreretrofit.api.fruits;

import com.google.gson.annotations.SerializedName;

import co.early.fore.core.Affirm;
import co.early.fore.net.retrofit2.MessageProvider;
import foo.bar.example.foreretrofit.message.UserMessage;

import static foo.bar.example.foreretrofit.message.UserMessage.ERROR_MISC;

/**
 *
 * <Code>
 *
 *  The server returns custom error codes in this form:
 *
 *  {
 *    "errorCode":"FRUIT_USER_LOCKED"
 *  }
 *
 * </Code>
 *
 *
 */
public class FruitsCustomError implements MessageProvider<UserMessage> {

    private ErrorCode errorCode;

    public FruitsCustomError(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public enum ErrorCode {

        @SerializedName("FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT")
        LOGIN_CREDENTIALS_INCORRECT(UserMessage.ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT),
        @SerializedName("FRUIT_USER_LOCKED")
        USER_LOCKED(UserMessage.ERROR_FRUIT_USER_LOCKED),
        @SerializedName("FRUIT_USER_NOT_ENABLED")
        USER_NOT_ENABLED(UserMessage.ERROR_FRUIT_USER_NOT_ENABLED);

        public final UserMessage userMessage;

        ErrorCode(UserMessage userMessage) {
            this.userMessage = Affirm.notNull(userMessage);
        }
    }

    public UserMessage getMessage() {
        if (errorCode == null) {
            return ERROR_MISC;
        } else {
            return errorCode.userMessage;
        }
    }

}
