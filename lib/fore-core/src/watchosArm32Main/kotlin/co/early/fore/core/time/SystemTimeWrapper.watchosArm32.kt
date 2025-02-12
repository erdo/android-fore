package co.early.fore.core.time

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.clock_gettime
import platform.posix.timespec
import platform.posix.CLOCK_UPTIME_RAW

actual fun getSystemTimeWrapper(): SystemTimeWrapper = SystemTimeWrapperWatchosArm32()

class SystemTimeWrapperWatchosArm32 : SystemTimeWrapper {
    override fun currentTimeMillis(): Long {
        return NSDate().timeIntervalSince1970.toLong() * 1000
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun nanoTime(): Long {
        memScoped {
            val timeSpec = alloc<timespec>()
            clock_gettime(CLOCK_UPTIME_RAW.toUInt(), timeSpec.ptr)
            return (timeSpec.tv_sec.toLong() * 1_000_000_000) + timeSpec.tv_nsec.toLong()
        }
    }
}
