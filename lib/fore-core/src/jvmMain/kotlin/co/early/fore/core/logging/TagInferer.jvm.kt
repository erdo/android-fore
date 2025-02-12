package co.early.fore.core.logging

actual fun getTagInferer(): TagInferer = TagInfererJvm()

class TagInfererJvm : TagInferer {

    override fun inferTag(): String {
        val trace = Throwable().stackTrace
        return if (trace.size > 2) {
            extractClassName(trace[2]).let {
                if ((it == "ObservableImp" || it == "Fore\$Companion") && trace.size > 3) {
                    extractClassName(trace[3])
                } else {
                    it
                }
            }
        } else "missing stacktrace"
    }

    private fun extractClassName(element: StackTraceElement): String {
        val simpleClassName = element.className.substringAfterLast('.')
        val anonymousClass = ANONYMOUS_CLASS_SUFFIX.find(simpleClassName)
        return anonymousClass?.let {
            simpleClassName.replace(it.value, "") // remove anonymous class suffix
        } ?: simpleClassName
    }

    companion object {
        private val ANONYMOUS_CLASS_SUFFIX = Regex("(\\$\\d+)+$")
    }
}
