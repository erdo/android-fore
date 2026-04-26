package co.early.fore.core

import co.early.fore.core.delegate.DelegateBase
import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.SystemLogger
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.core.time.getSystemTimeWrapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

/**
 * copied from fore-test-fixture so that we don't introduce a
 * circular dependency between this module and that one
 */
class DelegateTestSynchronousCopy(
    override val logger: Logger = SystemLogger(),
    override val systemTimeWrapper: SystemTimeWrapper = getSystemTimeWrapper(),
    override val exceptionHandler: CoroutineExceptionHandler? = null,
) : DelegateBase() {

    override val isTest = true

    private val testJob = SupervisorJob()

    // Unconfined so it runs immediately
    private val testScope = CoroutineScope(Dispatchers.Unconfined + testJob + (exceptionHandler ?: EmptyCoroutineContext))

    override val ioScope get() = testScope
    override val defaultScope get() = testScope
    override val mainScope get() = testScope
    override val mainImmediateScope get() = testScope

    fun cleanup() {
        testJob.cancel()
    }
}
