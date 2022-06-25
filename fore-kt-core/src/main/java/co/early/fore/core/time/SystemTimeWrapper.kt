package co.early.fore.core.time

/**
 * This enables us to set a mock the system time for testing.
 */
class SystemTimeWrapper {
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun nanoTime(): Long {
        return System.nanoTime()
    }
}