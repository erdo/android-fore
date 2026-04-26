package co.early.fore.core.time

actual fun getSystemTimeWrapper(): SystemTimeWrapper = SystemTimeWrapperAndroid()

class SystemTimeWrapperAndroid : SystemTimeWrapper {
    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun nanoTime(): Long {
        return System.nanoTime()
    }
}
