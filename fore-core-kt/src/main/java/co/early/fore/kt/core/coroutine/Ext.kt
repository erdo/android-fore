package co.early.fore.kt.core.coroutine

import co.early.fore.core.WorkMode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext


/**
 * Would love to drop workMode entirely when using co-routines and just depend on
 * kotlinx-coroutines-test, sadly we can't always get determinate test results at
 * the moment - and for unit tests that's a total deal breaker.
 *
 * There is a complicated discussion about that here: https://github.com/Kotlin/kotlinx.coroutines/pull/1206
 *
 * For the moment we continue to use the very simple and clear WorkMode switch as we have done in the past.
 * SYNCHRONOUS means everything is run sequentially in a blocking manner and on whatever thread the caller
 * is on. ASYNCHRONOUS gives you the co-routine behaviour you would expect.
 */

fun launchMainImm(workMode: WorkMode, block: suspend CoroutineScope.() -> Unit): Job {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Main.immediate).launch { block() }
    }
}

fun launchMain(workMode: WorkMode, block: suspend CoroutineScope.() -> Unit): Job {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Main).launch { block() }
    }
}

fun launchIO(workMode: WorkMode, block: suspend CoroutineScope.() -> Unit): Job {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.IO).launch { block() }
    }
}

fun launchDefault(workMode: WorkMode, block: suspend CoroutineScope.() -> Unit): Job {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Default).launch { block() }
    }
}


fun <T> asyncMainImm(workMode: WorkMode, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Main.immediate).async { block() }
    }
}

fun <T> asyncMain(workMode: WorkMode, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Main).async { block() }
    }
}

fun <T> asyncIO(workMode: WorkMode, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.IO).async { block() }
    }
}

fun <T> asyncDefault(workMode: WorkMode, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Default).async { block() }
    }
}

suspend fun <T> withContextMainImm(workMode: WorkMode, block: suspend CoroutineScope.() -> T): T {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(Dispatchers.Main.immediate) { block() }
    }
}

suspend fun <T> withContextMain(workMode: WorkMode, block: suspend CoroutineScope.() -> T): T {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(Dispatchers.Main) { block() }
    }
}

suspend fun <T> withContextIO(workMode: WorkMode, block: suspend CoroutineScope.() -> T): T {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(Dispatchers.IO) { block() }
    }
}

suspend fun <T> withContextDefault(workMode: WorkMode, block: suspend CoroutineScope.() -> T): T {
    return if (workMode == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(Dispatchers.Default) { block() }
    }
}
