package co.early.fore.kt.core.logging

interface Logger {
    fun e(message: String)
    fun w(message: String)
    fun i(message: String)
    fun d(message: String)
    fun v(message: String)

    fun e(tag: String?, message: String?)
    fun w(tag: String?, message: String?)
    fun i(tag: String?, message: String?)
    fun d(tag: String?, message: String?)
    fun v(tag: String?, message: String?)

    fun e(message: String, throwable: Throwable)
    fun w(message: String, throwable: Throwable)
    fun i(message: String, throwable: Throwable)
    fun d(message: String, throwable: Throwable)
    fun v(message: String, throwable: Throwable)

    fun e(tag: String?, message: String?, throwable: Throwable?)
    fun w(tag: String?, message: String?, throwable: Throwable?)
    fun i(tag: String?, message: String?, throwable: Throwable?)
    fun d(tag: String?, message: String?, throwable: Throwable?)
    fun v(tag: String?, message: String?, throwable: Throwable?)
}
