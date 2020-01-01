package foo.bar.example.foreretrofit.message;

import android.os.Parcel;
import android.os.Parcelable;

import foo.bar.example.foreretrofit.App;
import foo.bar.example.foreretrofit.R;

/**
 * As an enum, this value can be passed around the app to indicate various states.
 * If you want to display it to the user you can put it inside a dialog (it implements
 * parcelable). Call getString() for the human readable text.
 */
public enum UserMessage implements Parcelable {

    ERROR_MISC(R.string.msg_error_misc),
    ERROR_NETWORK(R.string.msg_error_network),
    ERROR_SERVER(R.string.msg_error_server),
    ERROR_SECURITY_UNKNOWN(R.string.msg_error_misc),
    ERROR_CLIENT(R.string.msg_error_client),
    ERROR_SESSION_TIMED_OUT(R.string.msg_error_session_timeout),
    ERROR_BUSY(R.string.msg_error_busy),
    ERROR_CANCELLED(R.string.msg_error_cancelled),

    ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT(R.string.msg_error_fruit_user_login_creds_incorrect),
    ERROR_FRUIT_USER_LOCKED(R.string.msg_error_fruit_user_locked),
    ERROR_FRUIT_USER_NOT_ENABLED(R.string.msg_error_fruit_user_locked);


    private String message;
    private int messageResId;

    UserMessage(int messageResId) {
        this.messageResId = messageResId;
    }

    public String getString() {

        if (message == null) {
            message = getString(messageResId);
        }

        return message;
    }

    //this should only be called when a UserMessage is actually displayed to a user, so not during a JUnit test
    private String getString(int resId) {
        return App.getInst().getResources().getString(resId);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<UserMessage> CREATOR = new Creator<UserMessage>() {
        @Override
        public UserMessage createFromParcel(final Parcel source) {
            return UserMessage.values()[source.readInt()];
        }

        @Override
        public UserMessage[] newArray(final int size) {
            return new UserMessage[size];
        }
    };

}
