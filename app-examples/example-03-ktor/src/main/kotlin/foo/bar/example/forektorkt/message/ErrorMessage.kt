package foo.bar.example.forektorkt.message

import android.os.Parcel
import android.os.Parcelable
import foo.bar.example.forektorkt.App
import foo.bar.example.forektorkt.R

/**
 * As an enum, this value can be passed around the app to indicate various states.
 * If you want to display it to the user you can put it inside a dialog (it implements
 * parcelable). Call getString() for the human readable text.
 */
enum class ErrorMessage constructor(private val messageResId: Int) : Parcelable {

    ERROR_MISC(R.string.msg_error_misc),
    ERROR_NETWORK(R.string.msg_error_network),
    ERROR_SECURITY_UNKNOWN(R.string.msg_error_misc),
    ERROR_SERVER(R.string.msg_error_server),
    ERROR_CLIENT(R.string.msg_error_client),
    ERROR_RATE_LIMITED(R.string.msg_rate_limited),
    ERROR_SESSION_TIMED_OUT(R.string.msg_error_session_timeout),
    ERROR_BUSY(R.string.msg_error_busy),
    ERROR_CANCELLED(R.string.msg_error_cancelled),

    ERROR_FRUIT_USER_LOGIN_CREDENTIALS_INCORRECT(R.string.msg_error_fruit_user_login_creds_incorrect),
    ERROR_FRUIT_USER_LOCKED(R.string.msg_error_fruit_user_locked),
    ERROR_FRUIT_USER_NOT_ENABLED(R.string.msg_error_fruit_user_locked);

    val localisedMessage: String by lazy {
        getString(messageResId)
    }

    constructor(parcel: Parcel) : this(parcel.readInt())

    //this should only be called when an ErrorMessage is actually displayed to a user, so not during a JUnit test
    private fun getString(resId: Int): String {
        return App.inst.resources.getString(resId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(ordinal)
    }

    companion object CREATOR : Parcelable.Creator<ErrorMessage> {
        override fun createFromParcel(parcel: Parcel): ErrorMessage {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<ErrorMessage?> {
            return arrayOfNulls(size)
        }
    }
}
