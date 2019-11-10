package foo.bar.example.foreretrofitcoroutine.message

import android.os.Parcel
import android.os.Parcelable
import foo.bar.example.foreretrofit.R
import foo.bar.example.foreretrofitcoroutine.CustomApp

/**
 * As an enum, this value can be passed around the app to indicate various states.
 * If you want to display it to the user you can put it inside a dialog (it implements
 * parcelable). Call getString() for the human readable text.
 */
enum class UserMessage constructor(private val messageResId: Int) : Parcelable {

    ERROR_MISC(R.string.msg_error_misc),
    ERROR_NETWORK(R.string.msg_error_network),
    ERROR_SERVER(R.string.msg_error_server),
    ERROR_CLIENT(R.string.msg_error_client),
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

    //this should only be called when a UserMessage is actually displayed to a user, so not during a JUnit test
    private fun getString(resId: Int): String {
        return CustomApp.inst.resources.getString(resId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(ordinal)
    }

    companion object CREATOR : Parcelable.Creator<UserMessage> {
        override fun createFromParcel(parcel: Parcel): UserMessage {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<UserMessage?> {
            return arrayOfNulls(size)
        }
    }

}
