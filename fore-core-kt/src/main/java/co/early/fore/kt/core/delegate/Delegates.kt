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

class ReleaseDelegateDefault (
    override val workMode: WorkMode = ASYNCHRONOUS,
    override val logger: Logger = SilentLogger(),
    override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate

class DebugDelegateDefault (
        tagPrefix: String? = null,
        override val workMode: WorkMode = ASYNCHRONOUS,
        override val logger: Logger = AndroidLogger(tagPrefix),
        override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate

class TestDelegateDefault (
        override val workMode: WorkMode = SYNCHRONOUS,
        override val logger: Logger = SystemLogger(),
        override val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper()
) : Delegate


class ForeDelegateHolder {

    companion object {

        private var delegate: Delegate = ReleaseDelegateDefault()

        /**
         * For release builds you will generally not need to call this function -
         * (ReleaseDelegateDefault() is the default delegate)
         *
         * For debug builds you may optionally set the DebugDelegateDefault() here - this will
         * give you debug information, which the ReleaseDelegateDefault won't
         *
         * For running tests you will most likely want to set the TestDelegateDefault() here -
         * this will give you logging output to the console rather than Android Logs, and also uses
         * WorkMode.SYNCHRONOUS rather than the WorkMode.ASYNCHRONOUS provided by the Release
         * and Debug versions
         *
         * These defaults are just a convenience and the if you pass WorkMode/Logger/SystemTimeWrapper
         * parameters manually to any fore component via the constructor, they will be used in
         * preference to the defaults specified here
         *
         * Keep in mind that this is a global operation, so if you are using it with tests that
         * run in parallel they will need to be running in separate JVMs to avoid synchronization
         * issues. If this is a problem for your set up, you can revert to passing the parameters
         * to the fore component via the constructor - this is the technique used by the majority
         * of the sample apps in the fore repo
         */
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
