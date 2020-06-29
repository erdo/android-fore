package co.early.fore.kt.core.logging

interface Logger : co.early.fore.core.logging.Logger {
    fun e(message: String)
    fun w(message: String)
    fun i(message: String)
    fun d(message: String)
    fun v(message: String)
    fun e(message: String, throwable: Throwable)
    fun w(message: String, throwable: Throwable)
    fun i(message: String, throwable: Throwable)
    fun d(message: String, throwable: Throwable)
    fun v(message: String, throwable: Throwable)
}
