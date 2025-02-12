package co.early.fore.core.time

actual fun getSystemTimeWrapper(): SystemTimeWrapper = SystemTimeWrapperLinux()

class SystemTimeWrapperLinux : SystemTimeWrapper {
    override fun currentTimeMillis(): Long {
        return kotlin.system.getTimeMillis()
    }

    override fun nanoTime(): Long {
        return kotlin.system.getTimeNanos()
    }
}