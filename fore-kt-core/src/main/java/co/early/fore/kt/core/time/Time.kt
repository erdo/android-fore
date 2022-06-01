package co.early.fore.kt.core.time

import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import java.text.DecimalFormat

@Deprecated("this isn't generic enough to be widely useful, so will be removed in the next major version")
val nanosFormat = DecimalFormat("#,###")

/**
 * invokes the function,
 * returns the result,
 * then logs the time taken in a standard format
 */
@Deprecated("this isn't generic enough to be widely useful, so will be removed in the next major version")
inline fun <T> measureNanos(logger: Logger? = null, function: () -> T): T {
    return measureNanos({ nanos ->
        Fore.getLogger(logger).i("operation took: ${nanosFormat.format(nanos)} ns " +
                "thread:${Thread.currentThread().id}")
    }) { function.invoke() }
}

/**
 * invokes the function,
 * invokes timeTaken with the time taken in ns to run the function,
 * returns the result or the function
 */
@Deprecated("this isn't generic enough to be widely useful, so will be removed in the next major version")
inline fun <T> measureNanos(timeTaken: (Long) -> Unit, function: () -> T): T {
    val decoratedResult = measureNanos(function)
    timeTaken(decoratedResult.second)
    return decoratedResult.first
}

/**
 * invokes the function,
 * returns a Pair(first = result of the function, second = time taken in ns)
 */
@Deprecated("this isn't generic enough to be widely useful, so will be removed in the next major version")
inline fun <T> measureNanos(function: () -> T): Pair<T, Long> {
    val startTime = System.nanoTime()
    val result: T = function.invoke()
    return result to (System.nanoTime() - startTime)
}
