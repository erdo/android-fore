package foo.bar.example.foreapollokt.message

import android.os.Parcel
import android.os.Parcelable
import foo.bar.example.foreapollokt.App
import foo.bar.example.foreapollokt.R

/**
 * As an enum, this value can be passed around the app to indicate various error states.
 * If you want to display it to the user you can put it inside a dialog (it implements
 * parcelable). #localisedMessage provides human readable text.
 */
enum class ErrorMessage constructor(private val messageResId: Int) : Parcelable {

    ERROR_MISC(R.string.msg_error_misc),
    ERROR_NETWORK(R.string.msg_error_network),
    ERROR_SECURITY_UNKNOWN(R.string.msg_error_misc),
    ERROR_SERVER(R.string.msg_error_server),
    ERROR_ALREADY_EXECUTED(R.string.msg_error_already_executed),
    ERROR_CLIENT(R.string.msg_error_client),
    ERROR_RATE_LIMITED(R.string.msg_rate_limited),
    ERROR_SESSION_TIMED_OUT(R.string.msg_error_session_timeout),
    ERROR_BUSY(R.string.msg_error_busy),
    ERROR_CANCELLED(R.string.msg_error_cancelled),
    ERROR_NO_LAUNCH(R.string.msg_error_no_launch),

    ERROR_BLANK_EMAIL(R.string.msg_error_blank_email),
    LAUNCH_SERVICE_SAYS_NO_ERROR(R.string.msg_error_launch_server_says_no),
    INTERNAL_SERVER_ERROR(R.string.msg_error_launch_internal_server);

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

        fun createFromName(name: String?): ErrorMessage? {
            return if (!name.isNullOrBlank()) {
                values().find { it.name == name }
            } else {
                null
            }
        }
    }
}
