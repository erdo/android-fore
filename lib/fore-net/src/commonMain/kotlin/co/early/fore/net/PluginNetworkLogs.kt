package co.early.fore.net

import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.SilentLogger
import co.early.fore.net.BodyRenderFormat.Binary
import co.early.fore.net.BodyRenderFormat.Curl
import co.early.fore.net.BodyRenderFormat.Html
import co.early.fore.net.BodyRenderFormat.Json
import co.early.fore.net.BodyRenderFormat.PlainText
import co.early.fore.net.BodyRenderFormat.Xml
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.AttributeKey
import okio.Buffer
import kotlin.random.Random

const val BIG_LOG = 250000
private const val someCharacters = "ABDEFGH023456789"

class PluginNetworkLogs private constructor(
    private val tag: String,
    private val curlStyleRequestLogs: Boolean,
    private val prettifyResponseLogs: Boolean,
    private val maxBodyLogBytes: Int,
    private val filters: List<(HttpRequestBuilder) -> Boolean>,
    private val networkingLogSanitizer: NetworkingLogSanitizer?,
    private val logger: Logger,
) {
    class Config {
        var preTag: String = "Net"
        var curlStyleRequestLogs: Boolean = true
        var prettifyResponseLogs: Boolean = true
        var maxBodyLogBytes: Int = 4000
        var networkingLogSanitizer: NetworkingLogSanitizer? = null
        var logger: Logger? = null
        var filters: List<(HttpRequestBuilder) -> Boolean> = emptyList()
    }

    companion object Plugin : HttpClientPlugin<Config, PluginNetworkLogs> {

        override val key: AttributeKey<PluginNetworkLogs> = AttributeKey("PluginNetworkLogs")

        override fun prepare(block: Config.() -> Unit): PluginNetworkLogs {
            Config().apply(block).apply {
                return PluginNetworkLogs(
                    tag = preTag,
                    curlStyleRequestLogs = curlStyleRequestLogs,
                    prettifyResponseLogs = prettifyResponseLogs,
                    maxBodyLogBytes = maxBodyLogBytes,
                    networkingLogSanitizer = networkingLogSanitizer,
                    filters = filters,
                    logger = Fore.getLogger(logger),
                )
            }
        }

        override fun install(plugin: PluginNetworkLogs, scope: HttpClient) {
            scope.plugin(HttpSend).intercept { request ->

                val lggr = plugin.logger

                if (lggr !is SilentLogger && plugin.shouldBeLogged(request)) {

                    val compositeTag = "${plugin.tag}${plugin.createRandomTag()}"

                    val (method, url) = plugin.logRequest(
                        request = request,
                        plugin = plugin,
                        compositeTag = compositeTag,
                        lggr = lggr
                    )

                    val measuredCall = plugin.measureNanos {
                        try {
                            execute(request)
                        } catch (e: Throwable) {
                            lggr.d(
                                compositeTag,
                                "HTTP $method <-- Connection dropped, GETs may be retried $url : $e"
                            )
                            throw e
                        }
                    }

                    plugin.logResponse(
                        response = measuredCall.first.response,
                        plugin = plugin,
                        method = method,
                        timeTakenNs = measuredCall.second,
                        url = url,
                        compositeTag = compositeTag,
                        lggr = lggr
                    )

                    measuredCall.first
                } else {
                    execute(request)
                }
            }
        }
    }

    private val someChars = someCharacters.toCharArray()

    private fun shouldBeLogged(request: HttpRequestBuilder): Boolean =
        filters.isEmpty() || filters.any { it(request) }

    private fun createRandomTag() = " " +
            nextChar(someChars) +
            nextChar(someChars) +
            nextChar(someChars) +
            nextChar(someChars) +
            nextChar(someChars)

    private fun nextChar(someChars: CharArray) =
        someChars[Random.Default.nextInt(someChars.size - 1)]

    private inline fun <T> measureNanos(function: () -> T): Pair<T, Long> {
        val startTime = Fore.getSystemTimeWrapper().nanoTime()
        val result: T = function.invoke()
        return result to (Fore.getSystemTimeWrapper().nanoTime() - startTime)
    }

    private fun formatNumberWithCommas(number: Long): String {
        val chunks = number.toString().reversed().chunked(3)
        return chunks.joinToString(",").reversed()
    }

    private suspend fun logRequest(
        request: HttpRequestBuilder,
        plugin: PluginNetworkLogs,
        compositeTag: String,
        lggr: Logger
    ): Pair<String, String> {
        val requestStringBuilder = StringBuilder()
        val requestBody = extractBodyInfo(request.body, plugin.maxBodyLogBytes, false)

        val method = request.method.value
        val url = request.url.buildString()

        requestStringBuilder.logRequestInfo(
            method = method,
            url = url,
            curlStyleRequestLogging = plugin.curlStyleRequestLogs,
        )

        requestStringBuilder.logHeaders(
            headers = request.headers.entries(),
            curlStyleRequestLogging = plugin.curlStyleRequestLogs,
            networkingLogSanitizer = plugin.networkingLogSanitizer,
        )

        requestStringBuilder.logBody(
            bodyToLog = requestBody.first,
            message = requestBody.second,
            curlStyleLogging = plugin.curlStyleRequestLogs,
            attemptToPrettify = false,
            networkingLogSanitizer = plugin.networkingLogSanitizer,
            logger = lggr,
        )

        lggr.d(compositeTag, requestStringBuilder.toString())
        return Pair(method, url)
    }

    private fun StringBuilder.logRequestInfo(
        method: String,
        url: String,
        curlStyleRequestLogging: Boolean,
    ) {
        if (curlStyleRequestLogging) {
            appendLine("curl -i --request $method \\")
            appendLine(" --url '$url' \\")
        } else {
            appendLine("HTTP $method --> $url")
        }
    }

    internal suspend fun logResponse(
        response: HttpResponse,
        plugin: PluginNetworkLogs,
        method: String,
        timeTakenNs: Long,
        url: String,
        compositeTag: String,
        lggr: Logger
    ) {
        val responseStringBuilder = StringBuilder()
        val responseBody = extractBodyInfo(response.bodyAsChannel(), plugin.maxBodyLogBytes)

        responseStringBuilder.logResponseInfo(
            method = method,
            code = "${response.status.value}, ${response.status.description}",
            timeTakenNs = timeTakenNs,
            url = url,
            curlStyleRequestLogs = plugin.curlStyleRequestLogs
        )

        responseStringBuilder.logHeaders(
            headers = response.headers.entries(),
            curlStyleRequestLogging = false,
            networkingLogSanitizer = plugin.networkingLogSanitizer,
        )

        responseStringBuilder.logBody(
            bodyToLog = responseBody.first,
            message = responseBody.second,
            curlStyleLogging = false,
            attemptToPrettify = plugin.prettifyResponseLogs,
            networkingLogSanitizer = plugin.networkingLogSanitizer,
            logger = lggr,
        )

        lggr.d(compositeTag, responseStringBuilder.toString())
    }

    private fun StringBuilder.logResponseInfo(
        method: String,
        code: String,
        timeTakenNs: Long,
        url: String,
        curlStyleRequestLogs: Boolean,
    ) {
        appendLine(
            "HTTP $method ${if (curlStyleRequestLogs) "Response," else "<--"} Server replied: HTTP-${code}. took:" +
                    "${formatNumberWithCommas(timeTakenNs / (1000 * 1000))}ms $url"
        )
    }

    private fun StringBuilder.logHeaders(
        headers: Set<Map.Entry<String, List<String>>>,
        curlStyleRequestLogging: Boolean,
        networkingLogSanitizer: NetworkingLogSanitizer?,
    ) {

        val sanitizedHeaderEntries =
            networkingLogSanitizer?.sanitizeHeaders(headers) ?: headers

        sanitizedHeaderEntries.forEachIndexed { index, (key, values) ->
            if (curlStyleRequestLogging) {
                if (index < sanitizedHeaderEntries.size - 1) {
                    appendLine(" --header '$key: $values' \\")
                } else {
                    appendLine(" --header '$key: $values'")
                }
            } else {
                appendLine("    $key: $values")
            }
        }
    }

    private fun StringBuilder.logBody(
        bodyToLog: Buffer,
        message: String = "",
        curlStyleLogging: Boolean,
        attemptToPrettify: Boolean,
        networkingLogSanitizer: NetworkingLogSanitizer? = null,
        logger: Logger,
    ) {

        val size = bodyToLog.size
        if (size > 0) {

            try {

                val inferredBodyType = if (curlStyleLogging) {
                    Curl
                } else if (!attemptToPrettify) {
                    PlainText
                } else {
                    inferBodyRenderFormat(bodyToLog)
                }

                val pretty = when (inferredBodyType) {
                    Binary -> {
                        "(we think this body probably contains binary data - if not, " +
                                "please open a PR at https://github.com/erdo/android-fore)"
                    }

                    Html, Xml -> bodyToLog.xmlPrettyPrint()
                    Json -> bodyToLog.jsonPrettyPrint()
                    PlainText, Curl -> bodyToLog.readUtf8() // make minimal changes
                }

                val sanitized = networkingLogSanitizer?.sanitizeBody(pretty) ?: pretty

                if (curlStyleLogging) {
                    appendLine("-d '$sanitized'")
                } else {
                    appendLine(sanitized)
                }

            } catch (e: Exception) {
                logger.e("too large to format nicely, consider reducing maxBodyLogBytes from:$size")
                logger.e(e.toString())
            }
        }

        if (message.isNotBlank()) {
            append(message)
        }
    }
}
