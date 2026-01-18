package co.early.fore.core.delegate

import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.MultiplatformLogger
import co.early.fore.core.logging.SilentLogger
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.core.time.getSystemTimeWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * Many classes take: Dispatchers, Loggers and/or SystemTimeWrappers as construction parameters.
 * These can be provided by the Fore delegates (and swapped out globally during tests)
 *
 * To set your own Delegate e.g. `Fore.setDelegate(DefaultTestDelegate())`
 */
interface Delegate {
    val logger: Logger
    val systemTimeWrapper: SystemTimeWrapper
    val isTest: Boolean

    val ioScope: CoroutineScope
    val defaultScope: CoroutineScope
    val mainScope: CoroutineScope
    val mainImmediateScope: CoroutineScope

    val exceptionHandler: CoroutineExceptionHandler?
}

abstract class DelegateBase : Delegate {

  //  protected open val delegateJob = SupervisorJob()

    override val isTest = false

    override val exceptionHandler: CoroutineExceptionHandler? = null

    override val ioScope: CoroutineScope by lazy {
        createScope(Dispatchers.IO + SupervisorJob())
    }

    override val defaultScope: CoroutineScope by lazy {
        createScope(Dispatchers.Default + SupervisorJob())
    }

    override val mainScope: CoroutineScope by lazy {
        val dispatcher = try {
            Dispatchers.Main
        } catch (e: Exception) {
            val message = """
            Running on a KMP platform that doesn't support Dispatchers.Main or Dispatchers.Main.immediate.
            Using Dispatchers.Default, consider specifying a dispatcher in the ObservableImp constructor
        """.trimIndent()
            Fore.w(message)
            Dispatchers.Default
        }
        createScope(dispatcher + SupervisorJob())
    }

    override val mainImmediateScope: CoroutineScope by lazy {
        val dispatcher = try {
            Dispatchers.Main.immediate
        } catch (e: Exception) {
            val message = """
            Running on a KMP platform that doesn't support Dispatchers.Main or Dispatchers.Main.immediate.
            Using Dispatchers.Default, consider specifying a dispatcher in the ObservableImp constructor
        """.trimIndent()
            Fore.w(message)
            Dispatchers.Default
        }
        createScope(dispatcher + SupervisorJob())
    }

    protected fun createScope(context: CoroutineContext): CoroutineScope {
        val combinedContext = exceptionHandler?.let { context + it } ?: context
        return CoroutineScope(combinedContext)
    }
}

class DelegateDebug(
    tagPrefix: String? = null,
    override val logger: Logger = MultiplatformLogger(tagPrefix),
    override val systemTimeWrapper: SystemTimeWrapper = getSystemTimeWrapper(),
    override val exceptionHandler: CoroutineExceptionHandler? = null
) : DelegateBase() {

    // this is for iOS target benefit which doesn't like default parameters in constructors
    constructor(tagPrefix: String) : this(
        tagPrefix = tagPrefix,
        logger = MultiplatformLogger(tagPrefix),
        systemTimeWrapper = getSystemTimeWrapper(),
        exceptionHandler = null
    )

    constructor() : this(
        tagPrefix = null,
        logger = MultiplatformLogger(null),
        systemTimeWrapper = getSystemTimeWrapper(),
        exceptionHandler = null
    )
}

class DelegateRelease(
    override val logger: Logger = SilentLogger(),
    override val systemTimeWrapper: SystemTimeWrapper = getSystemTimeWrapper(),
    override val exceptionHandler: CoroutineExceptionHandler? = null
) : DelegateBase() {

    // this is for iOS target benefit which doesn't like default parameters in constructors
    constructor() : this(SilentLogger(), getSystemTimeWrapper(), null)
}


class Fore {

    companion object {

        private var delegate: Delegate = DelegateRelease()

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
         * to the fore component via the constructor
         */
        fun setDelegate(delegate: Delegate) {
            Companion.delegate = delegate
        }

        fun getLogger(specified: Logger? = null): Logger {
            return specified ?: delegate.logger
        }

        fun getSystemTimeWrapper(specified: SystemTimeWrapper? = null): SystemTimeWrapper {
            return specified ?: delegate.systemTimeWrapper
        }

        fun scopeIo(): CoroutineScope = delegate.ioScope
        fun scopeDefault(): CoroutineScope = delegate.defaultScope
        fun scopeMain(): CoroutineScope = delegate.mainScope
        fun scopeMainImm(): CoroutineScope = delegate.mainImmediateScope

        /**
         * convenience function, same as calling: Fore.getLogger(null).e()
         */
        fun e(message: String) {
            delegate.logger.e(message)
        }

        /**
         * convenience function, same as calling: Fore.getLogger(null).w()
         */
        fun w(message: String) {
            delegate.logger.w(message)
        }

        /**
         * convenience function, same as calling: Fore.getLogger(null).i()
         */
        fun i(message: String) {
            delegate.logger.i(message)
        }

        /**
         * convenience function, same as calling: Fore.getLogger(null).d()
         */
        fun d(message: String) {
            delegate.logger.d(message)
        }

        /**
         * convenience function, same as calling: Fore.getLogger(null).w()
         */
        fun v(message: String) {
            delegate.logger.w(message)
        }
    }
}
