package co.early.fore.core.observer

/**
 * So that we can log the thread id for diagnostics
 *
 * JVM uses Thread.currentThread().name, iOS uses NSThread.currentThread().name
 */
expect fun threadName(): String



