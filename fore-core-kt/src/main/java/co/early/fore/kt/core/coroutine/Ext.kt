package co.early.fore.kt.core.coroutine

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext


/**
 * Would love to drop workMode entirely when using co-routines and just depend on
 * kotlinx-coroutines-test, sadly we can't always get determinate test results at
 * the moment - and for unit tests that's a total deal breaker.
 *
 * There is a complicated discussion about that here: https://github.com/Kotlin/kotlinx.coroutines/pull/1206
 *
 * In any case https://github.com/Kotlin/kotlinx.coroutines/blob/coroutines-test/kotlinx-coroutines-test/README.md
 * is focussed on swapping out the main dispatcher for unit tests. Even with runBlockingTest, other dispatchers can
 * still run concurrently with your tests, making tests much more complicated. The whole thing is extremely
 * complicated in fact (which is probably why it doesn't work yet).
 *
 * For the moment we continue to use the very simple and clear WorkMode switch as we have done in the past.
 * SYNCHRONOUS means everything is run sequentially in a blocking manner and on whatever thread the caller
 * is on. ASYNCHRONOUS gives you the co-routine behaviour you would expect.
 *
 * NB. This means there is no virtual time unless you implement it yourself though, for instance if you have code
 * like this in your app: delay(10 000), it will sit there and wait during a unit test, same as it would in app code.
 * You can write something like this instead: delay(if (workMode == WorkMode.ASYNCHRONOUS) 10000 else 1)
 *
 * Copyright © 2019 early.co. All rights reserved.
 */

fun launchIO(workMode: WorkMode? = null, block: suspend CoroutineScope.() -> Unit): Job {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.IO).launch { block() }
    }
}

fun launchDefault(workMode: WorkMode? = null, block: suspend CoroutineScope.() -> Unit): Job {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Default).launch { block() }
    }
}

fun launchCustom(dispatcher: CoroutineDispatcher, workMode: WorkMode? = null, block: suspend CoroutineScope.() -> Unit): Job {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(dispatcher).launch { block() }
    }
}

fun <T> asyncIO(workMode: WorkMode? = null, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.IO).async { block() }
    }
}

fun <T> asyncDefault(workMode: WorkMode? = null, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(Dispatchers.Default).async { block() }
    }
}

fun <T> asyncCustom(dispatcher: CoroutineDispatcher, workMode: WorkMode? = null, block: suspend CoroutineScope.() -> T): Deferred<T> {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        runBlocking { CompletableDeferred(block()) }
    } else {
        CoroutineScope(dispatcher).async { block() }
    }
}

suspend fun <T> awaitIO(workMode: WorkMode? = null, block: suspend CoroutineScope.() -> T): T {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(Dispatchers.IO) { block() }
    }
}

suspend fun <T> awaitDefault(workMode: WorkMode? = null, block: suspend CoroutineScope.() -> T): T {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(Dispatchers.Default) { block() }
    }
}

suspend fun <T> awaitCustom(dispatcher: CoroutineDispatcher, workMode: WorkMode? = null, block: suspend CoroutineScope.() -> T): T {
    return if (ForeDelegateHolder.getWorkMode(workMode) == WorkMode.SYNCHRONOUS) {
        block(CoroutineScope(coroutineContext))
    } else {
        withContext(dispatcher) { block() }
    }
}