package co.early.fore.net

import okhttp3.Headers

interface NetworkingLogSanitizer {
    fun sanitizeHeaders(allHeaders: Headers): Headers
    fun sanitizeBody(text: String): String
}
