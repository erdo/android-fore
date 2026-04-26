package co.early.fore.core.logging

class SystemLogger : Logger, TagFormatter by TagFormatterImp() {

    private val tagInferer = getTagInferer()

    override fun e(message: String) {
        e(tagInferer.inferTag(), message)
    }

    override fun w(message: String) {
        w(tagInferer.inferTag(), message)
    }

    override fun i(message: String) {
        i(tagInferer.inferTag(), message)
    }

    override fun d(message: String) {
        d(tagInferer.inferTag(), message)
    }

    override fun v(message: String) {
        v(tagInferer.inferTag(), message)
    }

    override fun e(message: String, throwable: Throwable) {
        e(tagInferer.inferTag(), message, throwable)
    }

    override fun w(message: String, throwable: Throwable) {
        w(tagInferer.inferTag(), message, throwable)
    }

    override fun i(message: String, throwable: Throwable) {
        i(tagInferer.inferTag(), message, throwable)
    }

    override fun d(message: String, throwable: Throwable) {
        d(tagInferer.inferTag(), message, throwable)
    }

    override fun v(message: String, throwable: Throwable) {
        v(tagInferer.inferTag(), message, throwable)
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
}
