package co.early.fore.kt.core.logging

class SilentLogger() : Logger {

    override fun e(message: String) {}

    override fun w(message: String) {}

    override fun i(message: String) {}

    override fun d(message: String) {}

    override fun v(message: String) {}

    override fun e(message: String, throwable: Throwable) {}

    override fun w(message: String, throwable: Throwable) {}

    override fun i(message: String, throwable: Throwable) {}

    override fun d(message: String, throwable: Throwable) {}

    override fun v(message: String, throwable: Throwable) {}

    override fun e(tag: String, message: String) {}

    override fun w(tag: String, message: String) {}

    override fun i(tag: String, message: String) {}

    override fun d(tag: String, message: String) {}

    override fun v(tag: String, message: String) {}

    override fun e(tag: String, message: String, throwable: Throwable) {}

    override fun w(tag: String, message: String, throwable: Throwable) {}

    override fun i(tag: String, message: String, throwable: Throwable) {}

    override fun d(tag: String, message: String, throwable: Throwable) {}

    override fun v(tag: String, message: String, throwable: Throwable) {}
}
