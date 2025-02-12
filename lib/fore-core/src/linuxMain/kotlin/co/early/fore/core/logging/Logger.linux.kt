package co.early.fore.core.logging

internal actual fun verbose(tag: String, message: String, throwable: Throwable?) {
    println(
        buildString {
            append("(V) $tag|$message")
            throwable?.let {
                append("\n")
                append(it.stackTraceToString())
            }
        }
    )
}

internal actual fun debug(tag: String, message: String, throwable: Throwable?) {
    println(
        buildString {
            append("(D) $tag|$message")
            throwable?.let {
                append("\n")
                append(it.stackTraceToString())
            }
        }
    )
}

internal actual fun info(tag: String, message: String, throwable: Throwable?) {
    println(
        buildString {
            append("(I) $tag|$message")
            throwable?.let {
                append("\n")
                append(it.stackTraceToString())
            }
        }
    )
}

internal actual fun warning(tag: String, message: String, throwable: Throwable?) {
    println(
        buildString {
            append("(W) $tag|$message")
            throwable?.let {
                append("\n")
                append(it.stackTraceToString())
            }
        }
    )
}

internal actual fun error(tag: String, message: String, throwable: Throwable?) {
    println(
        buildString {
            append("(E) $tag|$message")
            throwable?.let {
                append("\n")
                append(it.stackTraceToString())
            }
        }
    )
}
