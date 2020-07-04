package co.early.fore.kt.core.logging

import android.util.Log
import java.util.regex.Pattern

class AndroidLogger(private val tagPrefix: String? = null) : Logger {

    override fun e(message: String) {
        e(inferTag(), message)
    }

    override fun w(message: String) {
        w(inferTag(), message)
    }

    override fun i(message: String) {
        i(inferTag(), message)
    }

    override fun d(message: String) {
        d(inferTag(), message)
    }

    override fun v(message: String) {
        v(inferTag(), message)
    }

    override fun e(message: String, throwable: Throwable) {
        e(inferTag(), message, throwable)
    }

    override fun w(message: String, throwable: Throwable) {
        w(inferTag(), message, throwable)
    }

    override fun i(message: String, throwable: Throwable) {
        i(inferTag(), message, throwable)
    }

    override fun d(message: String, throwable: Throwable) {
        d(inferTag(), message, throwable)
    }

    override fun v(message: String, throwable: Throwable) {
        v(inferTag(), message, throwable)
    }

    override fun e(tag: String, message: String) {
        Log.e(addTagPrefixIfPresent(tag), message)
    }

    override fun w(tag: String, message: String) {
        Log.w(addTagPrefixIfPresent(tag), message)
    }

    override fun i(tag: String, message: String) {
        Log.i(addTagPrefixIfPresent(tag), message)
    }

    override fun d(tag: String, message: String) {
        Log.d(addTagPrefixIfPresent(tag), message)
    }

    override fun v(tag: String, message: String) {
        Log.v(addTagPrefixIfPresent(tag), message)
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        Log.e(addTagPrefixIfPresent(tag), message, throwable)
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        Log.w(addTagPrefixIfPresent(tag), message, throwable)
    }

    override fun i(tag: String, message: String, throwable: Throwable) {
        Log.i(addTagPrefixIfPresent(tag), message, throwable)
    }

    override fun d(tag: String, message: String, throwable: Throwable) {
        Log.d(addTagPrefixIfPresent(tag), message, throwable)
    }

    override fun v(tag: String, message: String, throwable: Throwable) {
        Log.e(addTagPrefixIfPresent(tag), message, throwable)
    }

    private fun inferTag(): String {
        val trace = Throwable().stackTrace
        return if (trace.size > 2) {
            extractClassName(trace[2])
        } else "missing"
    }

    /**
     * inspired by Timber (also Apache 2) -->
     */
    private fun extractClassName(element: StackTraceElement): String {
        var tag = element.className.substringAfterLast('.')
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return if (tag.length <= MAX_TAG_LENGTH) {
            tag
        } else {
            tag.substring(0, BOOK_END_LENGTH) + ".." + tag.substring(tag.length - BOOK_END_LENGTH, tag.length)
        }
    }

    private fun addTagPrefixIfPresent(message: String): String {
        return tagPrefix?.let {
            it + message
        } ?: message
    }

    companion object {
        private const val MAX_TAG_LENGTH = 20 //below API 24 is limited to 23 characters anyway
        private const val BOOK_END_LENGTH = (MAX_TAG_LENGTH / 2) - 1
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }
}