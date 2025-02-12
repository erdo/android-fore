package co.early.fore.core.logging

actual fun getTagInferer(): TagInferer = JsTagInferer()

class JsTagInferer : TagInferer {

    override fun inferTag(): String {

        val stack = js("new Error().stack") as String?

        return if (stack != null) {
            val traceLines = stack.split("\n")
            if (traceLines.size > 2) {
                extractFunctionName(traceLines[2])
            } else {
                "missing stacktrace"
            }
        } else {
            "missing stacktrace"
        }
    }

    private fun extractFunctionName(traceLine: String): String {
        // Stack trace line typically looks like: at functionName (file.js:line:column)
        return traceLine.substringAfter("at ").substringBefore("(").trim().ifEmpty {
            // If there's no function name, fallback to the entire line or a default value
            "unknownFunction"
        }
    }
}
