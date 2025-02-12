package co.early.fore.core.time

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alignOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import platform.windows.FILETIME
import platform.windows.GetSystemTime
import platform.windows.LARGE_INTEGER
import platform.windows.QueryPerformanceCounter
import platform.windows.QueryPerformanceFrequency
import platform.windows.SYSTEMTIME
import platform.windows.SystemTimeToFileTime

actual fun getSystemTimeWrapper(): SystemTimeWrapper = MingwSystemTimeWrapper()

class MingwSystemTimeWrapper : SystemTimeWrapper {

    private val EPOCH_DIFFERENCE_MS = 11644473600000L

    @OptIn(ExperimentalForeignApi::class)
    override fun currentTimeMillis(): Long {
        memScoped {

            val systemTime = alloc(sizeOf<SYSTEMTIME>(), alignOf<SYSTEMTIME>())
            val systemTimePtr = systemTime.reinterpret<SYSTEMTIME>().ptr
            GetSystemTime(systemTimePtr)

            val fileTime = alloc(sizeOf<FILETIME>(), alignOf<FILETIME>())
            val fileTimePtr = fileTime.reinterpret<FILETIME>().ptr
            SystemTimeToFileTime(systemTimePtr, fileTimePtr)

            // Combine high and low parts of FILETIME into a single 64-bit long
            val high = fileTimePtr.pointed.dwHighDateTime.toLong() shl 32  // convert to 64-bits, then shift 32 bits to the left
            val low = fileTimePtr.pointed.dwLowDateTime.toLong()  // dwLowDateTime is already unsigned 32-bit integer, convert to 64-bits
            val fileTimeIn100Nano = high or low // bitwise OR

            return (fileTimeIn100Nano / 10_000) - EPOCH_DIFFERENCE_MS
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun nanoTime(): Long {
        memScoped {

            val performanceCounter = alloc(sizeOf<LARGE_INTEGER>(), alignOf<LARGE_INTEGER>())
            val performanceCounterPtr = performanceCounter.reinterpret<LARGE_INTEGER>().ptr

            val performanceFrequency = alloc(sizeOf<LARGE_INTEGER>(), alignOf<LARGE_INTEGER>())
            val performanceFrequencyPtr = performanceFrequency.reinterpret<LARGE_INTEGER>().ptr

            // Get the frequency of the performance counter (counts per second)
            QueryPerformanceFrequency(performanceFrequencyPtr)

            // Get the current performance counter value
            QueryPerformanceCounter(performanceCounterPtr)

            // Convert the counter value to nanoseconds (as the counter is in counts, and we know the frequency)
            val counts = performanceCounterPtr.pointed.QuadPart
            val countsPerSecond = performanceFrequencyPtr.pointed.QuadPart

            // Convert the time to nanoseconds
            return (counts * 1_000_000_000) / countsPerSecond
        }
    }
}
