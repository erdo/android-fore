package co.early.fore.net

interface NetworkingLogSanitizer {
    fun sanitizeHeaders(headers: Set<Map.Entry<String, List<String>>>): Set<Map.Entry<String, List<String>>>
    fun sanitizeBody(text: String): String
}
