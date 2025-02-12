package co.early.fore.core.observer

actual fun threadName(): String {
    return if (js("typeof window !== 'undefined'") as Boolean) {
        "main thread (browser)"
    } else {
        "main thread (node..js)"
    }
}
