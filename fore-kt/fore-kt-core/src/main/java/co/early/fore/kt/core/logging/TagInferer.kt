package co.early.fore.kt.core.logging

import java.util.regex.Pattern

interface TagInferer {
    fun inferTag(): String
}

class TagInfererImpl : TagInferer {

    override fun inferTag(): String {
        val trace = Throwable().stackTrace
        return if (trace.size > 3) {
            extractClassName(trace[3]).let {
                if ((it == "ObservableImp" || it == "Fore\$Companion") && trace.size > 4){
                    extractClassName(trace[4])
                } else {
                    it
                }
            }
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
        return tag
    }

    companion object {
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }
}
