package co.early.fore.kt.core.logging

import java.util.regex.Pattern

interface TagInferer {
    fun inferTag(): String
}

class TagInfererImpl() : TagInferer {

    override fun inferTag(): String {
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

    companion object {
        private const val MAX_TAG_LENGTH = 20 //below API 24 is limited to 23 characters anyway
        private const val BOOK_END_LENGTH = (MAX_TAG_LENGTH / 2) - 1
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }
}
