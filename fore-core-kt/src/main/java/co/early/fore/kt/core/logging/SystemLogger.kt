package co.early.fore.kt.core.logging

class SystemLogger :
        Logger,
        TagInferer by TagInfererImpl() {

    private var longestTagLength = 0

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
        println("(E) " + padTagWithSpace(tag) + "|" + message)
    }

    override fun w(tag: String, message: String) {
        println("(W) " + padTagWithSpace(tag) + "|" + message)
    }

    override fun i(tag: String, message: String) {
        println("(I) " + padTagWithSpace(tag) + "|" + message)
    }

    override fun d(tag: String, message: String) {
        println("(D) " + padTagWithSpace(tag) + "|" + message)
    }

    override fun v(tag: String, message: String) {
        println("(V) " + padTagWithSpace(tag) + "|" + message)
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        e(tag, message)
        println(throwable)
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        w(tag, message)
        println(throwable)
    }

    override fun i(tag: String, message: String, throwable: Throwable) {
        i(tag, message)
        println(throwable)
    }

    override fun d(tag: String, message: String, throwable: Throwable) {
        d(tag, message)
        println(throwable)
    }

    override fun v(tag: String, message: String, throwable: Throwable) {
        v(tag, message)
        println(throwable)
    }

    private fun padTagWithSpace(tag: String): String? {
        longestTagLength = Math.max(longestTagLength, tag.length + 1)
        return if (longestTagLength != tag.length) {
            co.early.fore.core.utils.text.TextPadder.padText(tag, longestTagLength, co.early.fore.core.utils.text.TextPadder.Pad.RIGHT, ' ')
        } else {
            tag
        }
    }
}
