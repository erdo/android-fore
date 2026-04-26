package co.early.fore.core.delegate

import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.SystemLogger
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.core.time.getSystemTimeWrapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DelegateTestSynchronous(
    override val logger: Logger = SystemLogger(),
    override val systemTimeWrapper: SystemTimeWrapper = getSystemTimeWrapper(),
    override val exceptionHandler: CoroutineExceptionHandler? = null,
) : DelegateBase() {

    override val isTest = true

    private val testJob = SupervisorJob()

    // Unconfined so it runs immediately
    private val testScope = createScope(Dispatchers.Unconfined + testJob)

    override val ioScope get() = testScope
    override val defaultScope get() = testScope
    override val mainScope get() = testScope
    override val mainImmediateScope get() = testScope

    fun cleanup() {
        testJob.cancel()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun runWithTestDelegate(
    context: CoroutineContext = EmptyCoroutineContext,
    timeout: Duration = 60.seconds,
    useBackgroundScope: Boolean = false,
    testBody: suspend TestScope.() -> Unit
): TestResult {

    return runTest(context, timeout) {

        // not using Dispatchers.setMain() because it only allows use to use
        // one dispatcher for both Main and Main.immediate

        val testTimeWrapper = object : SystemTimeWrapper {
            override fun currentTimeMillis(): Long {
                return getSystemTimeWrapper().currentTimeMillis() + testScheduler.currentTime
            }

            override fun nanoTime(): Long {
                return getSystemTimeWrapper().nanoTime() + (testScheduler.currentTime * 1000)
            }
        }

        Fore.setDelegate(
            DelegateTest(
                testScope = if (useBackgroundScope) {
                    backgroundScope
                } else this,
                standardDispatcher = StandardTestDispatcher(testScheduler),
                mainImmediateDispatcher = UnconfinedTestDispatcher(testScheduler),
                systemTimeWrapper = testTimeWrapper
            )
        )

        try {
            testBody()
        } finally {
            Fore.setDelegate(DelegateDebug())
        }
    }
}

class DelegateTest(
    testScope: CoroutineScope,
    standardDispatcher: TestDispatcher,
    mainImmediateDispatcher: TestDispatcher,
    override val logger: Logger = SystemLogger(),
    override val systemTimeWrapper: SystemTimeWrapper,
) : DelegateBase() {

    override val isTest = true

    // so that everything is tracked inside runTest{}
    val testParentJob = testScope.coroutineContext[Job]!!

    override val ioScope = createScope(standardDispatcher + testParentJob)
    override val defaultScope = ioScope
    override val mainScope = ioScope
    override val mainImmediateScope = createScope(mainImmediateDispatcher + testParentJob)
}
