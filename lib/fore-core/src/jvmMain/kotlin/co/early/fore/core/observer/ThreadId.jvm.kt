package co.early.fore.core.observer

actual fun threadName(): String {
    return Thread.currentThread().name
}
