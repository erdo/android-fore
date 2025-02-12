package co.early.fore.core.logging

import platform.Foundation.NSLog

internal actual fun verbose(tag: String, message: String, throwable: Throwable?) {
    NSLog("(V) $tag|$message")
    throwable?.let {
        NSLog(it.toString())
    }
}

internal actual fun debug(tag: String, message: String, throwable: Throwable?) {
    NSLog("(D) $tag|$message")
    throwable?.let {
        NSLog(it.toString())
    }
}

internal actual fun info(tag: String, message: String, throwable: Throwable?) {
    NSLog("(I) $tag|$message")
    throwable?.let {
        NSLog(it.toString())
    }
}

internal actual fun warning(tag: String, message: String, throwable: Throwable?) {
    NSLog("(W) $tag|$message")
    throwable?.let {
        NSLog(it.toString())
    }
}

internal actual fun error(tag: String, message: String, throwable: Throwable?) {
    NSLog("(E) $tag|$message")
    throwable?.let {
        NSLog(it.toString())
    }
}
