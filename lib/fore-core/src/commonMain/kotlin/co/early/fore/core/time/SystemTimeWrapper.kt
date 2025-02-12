package co.early.fore.core.time

/**
 * This enables us to set a mock the system time for testing.
 */
interface SystemTimeWrapper {
    /**
     * Android / Linux / JVM uses System.currentTimeMillis(),
     * iOS uses NSDate().timeIntervalSince1970,
     * Windows uses GetSystemTimeAsFileTime() [NB:but referenced to the Unix epoch i.e. NOT 1/1/1601]
     */
    fun currentTimeMillis(): Long

    /**
     * intended for measuring time _differences_
     *
     * Android / Linux / JVM uses System.nanoTime(),
     * iOS uses clock_gettime(CLOCK_UPTIME_RAW)
     * Windows uses QueryPerformance
     */
    fun nanoTime(): Long
}

expect fun getSystemTimeWrapper(): SystemTimeWrapper