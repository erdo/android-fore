package co.early.fore.core.logging

import android.util.Log

internal actual fun verbose(tag: String, message: String, throwable: Throwable?) {
    Log.v(tag, message, throwable)
}

internal actual fun debug(tag: String, message: String, throwable: Throwable?) {
    Log.d(tag, message, throwable)
}

internal actual fun info(tag: String, message: String, throwable: Throwable?) {
    Log.i(tag, message, throwable)
}

internal actual fun warning(tag: String, message: String, throwable: Throwable?) {
    Log.w(tag, message, throwable)
}

internal actual fun error(tag: String, message: String, throwable: Throwable?) {
    Log.e(tag, message, throwable)
}
