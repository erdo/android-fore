package co.early.fore.net

import okio.Buffer

internal fun Buffer.jsonPrettyPrint() = readUtf8().let { jsonString ->
    var indentLevel = 0
    val indentWidth = 2
    fun padding() = "".padStart((indentLevel * indentWidth).coerceAtLeast(0))

    buildString {
        var inString = false
        jsonString.forEach { char ->
            when {
                char == '"' -> {
                    inString = !inString
                    append(char)
                }

                inString -> append(char)

                char in listOf('(', '[', '{') -> {
                    indentLevel++
                    appendLine(char)
                    append(padding())
                }

                char in listOf(')', ']', '}') -> {
                    indentLevel--
                    appendLine()
                    append(padding())
                    append(char)
                }

                char == ',' -> {
                    appendLine(char)
                    append(padding())
                }

                char.isWhitespace() -> {} // ignore whitespace outside of strings

                else -> append(char)
            }
        }
    }
}

internal fun Buffer.xmlPrettyPrint() = readUtf8().let { htmlString ->
    val indentWidth = 2
    var indentLevel = 0
    val stringBuilder = StringBuilder(htmlString.length)
    val regex =
        Regex("<[^>]*>|<.+|[^<]+") // matches tags, content between tags, or truncated partial tags
    var precededByText = true

    fun padding() = "".padStart((indentLevel * indentWidth).coerceAtLeast(0))

    regex.findAll(htmlString).forEach { match ->
        val chunk = match.value.trim()
        when {
            chunk.startsWith("</") -> {
                indentLevel--
                if (!precededByText) stringBuilder.appendLine().append(padding())
                stringBuilder.append(chunk)
                precededByText = false
            }

            chunk.startsWith("<") -> {
                if (!precededByText)stringBuilder.appendLine().append(padding())
                stringBuilder.append(chunk)
                if (!chunk.endsWith("/>") && !chunk.startsWith("<?") && !chunk.startsWith("<!")) {
                    indentLevel++
                }
                precededByText = false
            }

            chunk.isNotEmpty() -> { // text content
                stringBuilder.append(chunk)
                precededByText = true
            }
        }
    }
    stringBuilder.toString()
}
