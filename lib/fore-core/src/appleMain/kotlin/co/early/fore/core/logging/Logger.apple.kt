package co.early.fore.core.logging

import platform.Foundation.NSLog

private const val maxChunkSize = 800

internal actual fun verbose(tag: String, message: String, throwable: Throwable?) {
    log("(V) $tag|", message, throwable)
}

internal actual fun debug(tag: String, message: String, throwable: Throwable?) {
    log("(D) $tag|", message, throwable)
}

internal actual fun info(tag: String, message: String, throwable: Throwable?) {
    log("(I) $tag|", message, throwable)
}

internal actual fun warning(tag: String, message: String, throwable: Throwable?) {
    log("(W) $tag|", message, throwable)
}

internal actual fun error(tag: String, message: String, throwable: Throwable?) {
    log("(E) $tag|", message, throwable)
}

private fun log(preLog: String, message: String, throwable: Throwable?){
    if (message.length > maxChunkSize){
        NSLog(preLog)
        longLog(message)
    } else NSLog("$preLog$message")
    throwable?.let {
        NSLog(it.toString())
    }
}

private fun longLog(message: String, chunkSize: Int = maxChunkSize) {
    var start = 0
    while (start < message.length) {
        val end = (start + chunkSize).coerceAtMost(message.length)
        val chunk = message.substring(start, end)
        NSLog(chunk)
        start = end
    }
}
