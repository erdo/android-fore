package co.early.fore.core.observer

import platform.windows.GetCurrentThreadId

actual fun threadName(): String {
    return "Thread-${GetCurrentThreadId()}"
}
