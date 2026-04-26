package co.early.fore.core.coroutine

import co.early.fore.core.delegate.Fore
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.Observer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Copyright © 2019 early.co. All rights reserved.
 *
 * === Historical context ===
 *
 * 2019 kotlinx-coroutines-test did not provide
 * deterministic behaviour when code mixed multiple dispatchers (e.g. IO and Main), which made
 * reliable unit testing of non-trivial coroutine flows impractical.
 *
 * while it was possible to test individual suspend functions, testing larger units that launched
 * multiple coroutines could result in flaky tests. at the time, coroutine testing support focused
 * primarily on replacing Dispatchers.Main, while other dispatchers could still execute
 * concurrently with tests
 *
 * See discussion here:
 * https://github.com/Kotlin/kotlinx.coroutines/pull/1206
 *
 * Fore introduced an explicit WorkMode switch:
 *
 * - SYNCHRONOUS: work executes immediately and sequentially on the caller thread
 * - ASYNCHRONOUS: work executes using standard coroutine dispatchers
 *
 * This allowed asynchronous code paths to be exercised deterministically in tests without relying
 * on virtual time or complex scheduler control.
 *
 * Note: This approach does not provide virtual time. Calls such as delay(10_000) will block for
 * real time in tests unless explicitly shortened (e.g. delay(if (workMode == ASYNCHRONOUS) 10_000 else 1)).
 *
 * === Update (Jan 2022) ===
 *
 * kotlinx-coroutines-test is significantly reworked in version 1.6, addressing many of the
 * original issues around determinism and structured testing:
 * https://blog.jetbrains.com/kotlin/2021/12/introducing-kotlinx-coroutines-1-6-0/
 *
 * We're keeping the Fore coroutine helpers as a convenient and low boiler plate way to run
 * fire and forget coroutines that run synchronously during tests
 *
 * === Update (Dec 2025) ===
 *
 * Adding globally configured CoroutineScopes, so we can take advantage of runBlocking (especially
 * for integration testing complicated coroutines flows where strictly synchronous mode is not
 * always feasible)
 *
 * - Fire-and-forget, unstructured concurrency in production by default
 * - Fully structured concurrency in tests by swapping the Delegate to provide a test-owned scope
 * - Deterministic testing using runBlocking or runTest without latches, sleeps, or thread blocking
 * - Consistent cancellation and failure semantics via shared SupervisorJobs
 *
 * In SYNCHRONOUS mode, coroutines are started using CoroutineStart.UNDISPATCHED to provide immediate,
 * non-blocking execution while preserving coroutine structure.
 *
 * Fore's default delegate scopes use `SupervisorJob` to preserve independent task execution in
 * production, while test delegates can provide a shared scope so that `runBlocking` or
 * `runTest` deterministically waits for all child coroutines
 *
 * Additional reading:
 * https://www.thedevtavern.com/blog/posts/structured-concurrency-exceptions-and-cancellations/
 */

inline fun launchIO(
    name: String = "launchIO",
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return Fore.scopeIo().launch(CoroutineName(name)) { block() }
}

inline fun launchDefault(
    name: String = "launchDefault",
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return Fore.scopeDefault().launch(CoroutineName(name)) { block() }
}

inline fun launchCustom(
    coContext: CoroutineContext,
    name: String = "launchCustom",
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return CoroutineScope(Fore.scopeDefault().coroutineContext + coContext).launch(
        CoroutineName(
            name
        )
    ) { block() }
}

inline fun launchMain(
    name: String = "launchMain",
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return Fore.scopeMain().launch(CoroutineName(name)) { block() }
}

inline fun launchMainImm(
    name: String = "launchMainImm",
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return Fore.scopeMainImm().launch(CoroutineName(name)) { block() }
}

inline fun <T> asyncIO(
    name: String = "asyncIO",
    crossinline block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return Fore.scopeIo().async(CoroutineName(name)) { block() }
}

inline fun <T> asyncDefault(
    name: String = "asyncDefault",
    crossinline block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return Fore.scopeDefault().async(CoroutineName(name)) { block() }
}

inline fun <T> asyncCustom(
    coContext: CoroutineContext,
    name: String = "asyncCustom",
    crossinline block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return CoroutineScope(Fore.scopeDefault().coroutineContext + coContext).async(CoroutineName(name)) { block() }
}

inline fun <T> asyncMain(
    name: String = "asyncMain",
    crossinline block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return Fore.scopeMain().async(CoroutineName(name)) { block() }
}

inline fun <T> asyncMainImm(
    name: String = "asyncMainImm",
    crossinline block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return Fore.scopeMainImm().async(CoroutineName(name)) { block() }
}

suspend inline fun <T> awaitIO(
    name: String = "awaitIO",
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Fore.scopeIo().coroutineContext + CoroutineName(name)) { block() }
}

suspend inline fun <T> awaitDefault(
    name: String = "awaitDefault",
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Fore.scopeDefault().coroutineContext + CoroutineName(name)) { block() }
}

suspend inline fun <T> awaitCustom(
    coContext: CoroutineContext,
    name: String = "awaitCustom",
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(coContext + CoroutineName(name)) { block() }
}

suspend inline fun <T> awaitMain(
    name: String = "awaitMain",
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Fore.scopeMain().coroutineContext + CoroutineName(name)) { block() }
}

suspend inline fun <T> awaitMainImm(
    name: String = "awaitMainImm",
    crossinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(Fore.scopeMainImm().coroutineContext + CoroutineName(name)) { block() }
}

suspend fun Observable.waitUntil(
    condition: () -> Boolean
) {
    waitWhile { !condition() }
}

suspend fun Observable.waitWhile(condition: () -> Boolean) {
    awaitMain {

        if (!condition()) return@awaitMain

        val done = CompletableDeferred<Unit>()

        lateinit var temporaryObserver: Observer
        temporaryObserver = Observer {
            if (!condition() && !done.isCompleted) {
                removeObserver(temporaryObserver)
                done.complete(Unit)
            }
        }

        addObserver(temporaryObserver)
        done.await()
    }
}
