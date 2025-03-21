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
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import okio.Buffer
import kotlin.random.Random

const val BIG_LOG2 = 250000
private const val someCharacters = "ABDEFGH023456789"

class ForeNetworkLogsConfig {
    var preTag: String = "Net"
    var curlStyleRequestLogs: Boolean = true
    var prettifyResponseLogs: Boolean = true
    var maxBodyLogBytes: Int = 4000
    var networkingLogSanitizer: NetworkingLogSanitizer? = null
    var filters: List<(HttpRequestBuilder) -> Boolean> = emptyList()
    var logger: Logger? = null
}

val ForeNetworkLogs2 = createClientPlugin("ForeNetworkLogs2", ::ForeNetworkLogsConfig) {

    val config = pluginConfig
    val logger = Fore.getLogger(config.logger)
    val compositeTag = "${config.preTag}${createRandomTag()}"
    var enabled = false
    var method = ""
    var url = ""

    onRequest { request, _ ->

        enabled = (config.logger !is SilentLogger && shouldBeLogged(request, config.filters))

        if (enabled) {
            val (m, u) = logRequest(
                request = request,
                config = config,
                compositeTag = compositeTag,
                lggr = logger,
            )
            method = m
            url = u
        }
    }

    onResponse { response ->
        if (enabled) {
            logResponse(
                response = response,
                config = config,
                method = method,
                url = url,
                compositeTag = compositeTag,
                lggr = logger,
            )
        }
    }
}

private val someChars = someCharacters.toCharArray()

private fun shouldBeLogged(
    request: HttpRequestBuilder,
    filters: List<(HttpRequestBuilder) -> Boolean>
): Boolean =
    filters.isEmpty() || filters.any { it(request) }

private fun createRandomTag() = " " +
        nextChar(someChars) +
        nextChar(someChars) +
        nextChar(someChars) +
        nextChar(someChars) +
        nextChar(someChars)

private fun nextChar(someChars: CharArray) =
    someChars[Random.Default.nextInt(someChars.size - 1)]

private suspend fun logRequest(
    request: HttpRequestBuilder,
    config: ForeNetworkLogsConfig,
    compositeTag: String,
    lggr: Logger
): Pair<String, String> {
    val requestStringBuilder = StringBuilder()
    val requestBody = extractBodyInfo(request.body, config.maxBodyLogBytes, false)

    val method = request.method.value
    val url = request.url.buildString()

    requestStringBuilder.logRequestInfo(
        method = method,
        url = url,
        curlStyleRequestLogging = config.curlStyleRequestLogs,
    )

    requestStringBuilder.logHeaders(
        headers = request.headers.entries(),
        curlStyleRequestLogging = config.curlStyleRequestLogs,
        networkingLogSanitizer = config.networkingLogSanitizer,
    )

    // remove the new line character of the curl request if there is no body
    if (config.curlStyleRequestLogs &&
        requestStringBuilder.length > 2 &&
        requestStringBuilder[requestStringBuilder.length - 2] == '\\' &&
        requestBody.first.size < 1
    ) {
        requestStringBuilder.setLength(requestStringBuilder.length - 2)
        requestStringBuilder.appendLine()
    }

    requestStringBuilder.logBody(
        bodyToLog = requestBody.first,
        message = requestBody.second,
        curlStyleLogging = config.curlStyleRequestLogs,
        attemptToPrettify = false,
        networkingLogSanitizer = config.networkingLogSanitizer,
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

private suspend fun logResponse(
    response: HttpResponse,
    config: ForeNetworkLogsConfig,
    method: String,
    url: String,
    compositeTag: String,
    lggr: Logger
) {
    val responseStringBuilder = StringBuilder()
    val responseBody = extractBodyInfo(response.bodyAsChannel(), config.maxBodyLogBytes)

    responseStringBuilder.logResponseInfo(
        method = method,
        code = "${response.status.value}, ${response.status.description}",
        url = url,
        curlStyleRequestLogs = config.curlStyleRequestLogs
    )

    responseStringBuilder.logHeaders(
        headers = response.headers.entries(),
        curlStyleRequestLogging = false,
        networkingLogSanitizer = config.networkingLogSanitizer,
    )

    responseStringBuilder.logBody(
        bodyToLog = responseBody.first,
        message = responseBody.second,
        curlStyleLogging = false,
        attemptToPrettify = config.prettifyResponseLogs,
        networkingLogSanitizer = config.networkingLogSanitizer,
        logger = lggr,
    )

    lggr.d(compositeTag, responseStringBuilder.toString())
}


private fun StringBuilder.logResponseInfo(
    method: String,
    code: String,
    url: String,
    curlStyleRequestLogs: Boolean,
) {
    appendLine(
        "HTTP $method ${if (curlStyleRequestLogs) "Response," else "<--"} Server replied: HTTP-${code} $url"
    )
}

private fun StringBuilder.logHeaders(
    headers: Set<Map.Entry<String, List<String>>>,
    curlStyleRequestLogging: Boolean,
    networkingLogSanitizer: NetworkingLogSanitizer?,
) {

    val sanitizedHeaderEntries =
        networkingLogSanitizer?.sanitizeHeaders(headers) ?: headers

    sanitizedHeaderEntries.forEach { (key, values) ->
        if (curlStyleRequestLogging) {
            appendLine(" --header '$key: ${values.joinToString(", ")}' \\")
        } else {
            appendLine("    $key: ${values.joinToString(", ")}")
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
                appendLine(" --data '$sanitized'")
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
