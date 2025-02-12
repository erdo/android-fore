package co.early.fore.core.time

import kotlinx.browser.window

actual fun getSystemTimeWrapper(): SystemTimeWrapper = JsSystemTimeWrapper()

class JsSystemTimeWrapper : SystemTimeWrapper {
    override fun currentTimeMillis(): Long {
        return kotlin.js.Date.now().toLong()
    }

    override fun nanoTime(): Long {
        return (window.performance.now() * 1_000_000).toLong()
    }
}
