package co.early.fore.core.observer

import platform.posix.pthread_self

actual fun threadName(): String {
    return pthread_self().toString()
}
