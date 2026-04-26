package co.early.fore.core.time

import kotlin.time.TimeSource

actual fun getSystemTimeWrapper(): SystemTimeWrapper = SystemTimeWrapperLinux()

class SystemTimeWrapperLinux : SystemTimeWrapper {
    override fun currentTimeMillis(): Long {
        return TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds
    }

    override fun nanoTime(): Long {
        return TimeSource.Monotonic.markNow().elapsedNow().inWholeNanoseconds
    }
}