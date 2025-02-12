package co.early.fore.core.observer

import platform.Foundation.NSThread

actual fun threadName(): String {
    return NSThread.currentThread().name ?: "(no-thread-name)"
}
