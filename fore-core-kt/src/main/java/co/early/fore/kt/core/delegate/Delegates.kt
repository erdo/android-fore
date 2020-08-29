package co.early.fore.kt.core.delegate

import co.early.fore.core.WorkMode
import co.early.fore.core.WorkMode.ASYNCHRONOUS
import co.early.fore.core.WorkMode.SYNCHRONOUS
import co.early.fore.kt.core.logging.AndroidLogger
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.logging.SilentLogger
import co.early.fore.kt.core.logging.SystemLogger

/**
 * Many fore classes take: WorkMode, Logger and/or SystemTimeWrapper as construction parameters.
 * If these parameters are not specified, they will default to null and when they are needed by
 * the fore class they will be exchanged for the default delegate values indicated below.
 *
 * To set your own Delegate e.g. `ForeDelegateHolder.setDelegate(DefaultTestDelegate())`
 */

interface Delegate {
    val workMode: WorkMode
    val logger: Logger
    val systemTimeWrapper: SystemTimeWrapper
}

class DefaultReleaseDelegate (
    override val workMode: WorkMode = ASYNCHRONOUS,
    override val logger: Logger = SilentLogger(),
    override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate

class DefaultDebugDelegate (
        tagPrefix: String? = null,
        override val workMode: WorkMode = ASYNCHRONOUS,
        override val logger: Logger = AndroidLogger(tagPrefix),
        override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate

class DefaultTestDelegate (
        override val workMode: WorkMode = SYNCHRONOUS,
        override val logger: Logger = SystemLogger(),
        override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate


class ForeDelegateHolder {

    companion object {

        private var delegate: Delegate = DefaultReleaseDelegate()

        fun setDelegate(delegate: Delegate){
            this.delegate = delegate
        }

        fun getWorkMode(specified: WorkMode?) : WorkMode {
            return specified ?: delegate.workMode
        }

        fun getLogger(specified: Logger?) : Logger {
            return specified ?: delegate.logger
        }

        fun getSystemTimeWrapper(specified: SystemTimeWrapper?) : SystemTimeWrapper {
            return specified ?: delegate.systemTimeWrapper
        }
    }
}
