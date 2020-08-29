package co.early.fore.kt.core.time

import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.logging.Logger
import java.text.DecimalFormat

val nanosFormat = DecimalFormat("#,###")

inline fun <T> measureNanos(logger: Logger? = null, function: () -> T): T {
    return measureNanos({ nanos ->
        ForeDelegateHolder.getLogger(logger).i("operation took: ${nanosFormat.format(nanos)} ns " +
                "thread:${Thread.currentThread().id}")
    }) { function.invoke() }
}

inline fun <T> measureNanos(callback: (Long) -> Unit, function: () -> T): T {
    val startTime = System.nanoTime()
    val result: T = function.invoke()
    callback(System.nanoTime() - startTime)
    return result
}
