package foo.bar.example.asafadapters2.api;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import co.early.fore.core.logging.Logger;
import co.early.fore.retrofit.ErrorHandler;
import co.early.fore.retrofit.MessageProvider;
import foo.bar.example.asafadapters2.message.UserMessage;
import okhttp3.Request;
import retrofit2.Response;

import static co.early.fore.core.Affirm.notNull;
import static foo.bar.example.asafadapters2.message.UserMessage.ERROR_CLIENT;
import static foo.bar.example.asafadapters2.message.UserMessage.ERROR_MISC;
import static foo.bar.example.asafadapters2.message.UserMessage.ERROR_NETWORK;
import static foo.bar.example.asafadapters2.message.UserMessage.ERROR_SERVER;
import static foo.bar.example.asafadapters2.message.UserMessage.ERROR_SESSION_TIMED_OUT;

/**
 * You can probably use this class almost as it is for your own app, but you might want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the asaf library
 */
public class CustomGlobalErrorHandler implements ErrorHandler<UserMessage> {

    private static final String TAG = CustomGlobalErrorHandler.class.getSimpleName();

    private final Logger logWrapper;

    public CustomGlobalErrorHandler(Logger logWrapper) {
        this.logWrapper = notNull(logWrapper);
    }


    @Override
    public <CE extends MessageProvider<UserMessage>> UserMessage handleError(Throwable t, Response errorResponse, @Nullable Class<CE> customErrorClazz, Request originalRequest) {

        UserMessage message = ERROR_MISC;

        if (errorResponse != null) {

            logWrapper.e(TAG, "handleError() HTTP:" + errorResponse.code());

            switch (errorResponse.code()) {

                case 401:
                    message = ERROR_SESSION_TIMED_OUT;
                    break;

                case 400:
                case 405:
                    message = ERROR_CLIENT;
                    break;

                case 404://realise this is officially a "client" error, but in my experience this is usually the fault of the server ;)
                case 500:
                case 503:
                    message = ERROR_SERVER;
                    break;
            }

            if (customErrorClazz != null) {
                //let's see if we can get more specifics about the error
                message = parseCustomError(message, errorResponse, customErrorClazz);
            }

        } else {//non HTTP error, probably some connection problem, but might be JSON parsing related also

            logWrapper.e(TAG, "handleError() throwable:" + t);

            if (t != null) {

                if (t instanceof com.google.gson.stream.MalformedJsonException) {
                    message = ERROR_SERVER;
                } else {
                    message = ERROR_NETWORK;
                }
                t.printStackTrace();
            }
        }


        logWrapper.e(TAG, "handleError() returning:" + message);

        return message;
    }


    private <CE extends MessageProvider<UserMessage>> UserMessage parseCustomError(UserMessage provisionalErrorMessage, Response errorResponse, Class<CE> customErrorClazz) {

        Gson gson = new Gson();

        CE customError = null;

        try {
            customError = gson.fromJson(new InputStreamReader(errorResponse.errorBody().byteStream(), "UTF-8"), customErrorClazz);
        } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
            logWrapper.e(TAG, "parseCustomError() No more error details", e);
        } catch (com.google.gson.JsonSyntaxException e) {//the server probably gave us something that is not JSON
            logWrapper.e(TAG, "parseCustomError() Problem parsing customServerError", e);
            return ERROR_SERVER;
        }

        if (customError == null) {
            return provisionalErrorMessage;
        } else {
            return customError.getMessage();
        }
    }


}
