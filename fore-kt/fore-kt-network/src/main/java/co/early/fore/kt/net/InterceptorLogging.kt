package co.early.fore.kt.net

import co.early.fore.core.utils.text.BasicTextWrapper
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.locks.ReentrantLock

import co.early.fore.net.NetworkingLogSanitizer
import okhttp3.*
import okio.Buffer
import java.text.DecimalFormat
import kotlin.concurrent.withLock
import kotlin.reflect.KVisibility

const val BIG_LOG = 250000

/**
 * In order to log HTTP calls, add this interceptor at the bottom of your interceptor chain
 * when creating an OkHttpConfig. The log tag will include a random string eg "Net B4D32" which
 * will remain constant for a given call (request and response), so that you can use it to
 * correlate logs in the event that you have many calls being logged simultaneously
 */
class InterceptorLogging @JvmOverloads constructor(
    private val logger: Logger? = null,
    private val maxBodyLogCharacters: Int = 4000,
    private val networkingLogSanitizer: NetworkingLogSanitizer? = null,
    private val curlStyleRequests: Boolean = true,
) : Interceptor {

    private val nanosFormat = DecimalFormat("#,###")
    private val UTF8 = Charset.forName("UTF-8")
    private val random = Random()
    private val someCharacters = "ABDEFGH023456789".toCharArray()
    private val TAG = "Net"
    private val logLinesLock = ReentrantLock(true)
    private var printedWarningAlready = false

    init {
        require(maxBodyLogCharacters >= 1) { "maxBodyLogCharacters must be greater than 0" }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var url: HttpUrl? = null
        var method: String? = null
        val randomPostTag = (" " + someCharacters[random.nextInt(someCharacters.size - 1)]
                + someCharacters[random.nextInt(someCharacters.size - 1)]
                + someCharacters[random.nextInt(someCharacters.size - 1)]
                + someCharacters[random.nextInt(someCharacters.size - 1)]
                + someCharacters[random.nextInt(someCharacters.size - 1)])

        logLinesLock.withLock {
            try {
                val pair = logRequest(request, randomPostTag)
                url = pair.first
                method = pair.second
            } catch (t: Throwable) {
                logWarning(t)
            }
        }

        val decoratedResponse = measureNanos {
            try {
                chain.proceed(request)
            } catch (e: Throwable) {
                Fore.getLogger(logger).e(
                    TAG + randomPostTag,
                    "HTTP $method <-- Connection dropped, GETs may be retried $url : $e"
                )
                throw e
            }
        }
        val response = decoratedResponse.first
        val timeTaken = decoratedResponse.second

        logLinesLock.withLock {
            try {
                logResponse(response, randomPostTag, method, url, timeTaken)
            } catch (t: Throwable) {
                logWarning(t)
            }
        }

        return response
    }

    private fun logWarning(t: Throwable) {
        if (!printedWarningAlready) {
            Fore.getLogger(logger)
                .w("No network logging available: fore doesn't recognise this version of OkHttp, or you have excluded kotlin-reflect from your dependencies. ${t.message}")
            printedWarningAlready = true
        }
    }

    private fun logRequest(request: Request, randomPostTag: String): Pair<HttpUrl, String> {

        val method = method(request)
        val url = url(request)

        Fore.getLogger(logger).i(TAG + randomPostTag, String.format("HTTP %s --> %s", method, url))

        if (curlStyleRequests) {
            Fore.getLogger(logger)
                .i(TAG + randomPostTag, String.format("curl --request %s \\", method))
            Fore.getLogger(logger).i(TAG + randomPostTag, String.format(" --url '%s' \\", url))
        }

        networkingLogSanitizer?.let {
            logHeaders(it.sanitizeHeaders(headers(request)), randomPostTag, curlStyle = curlStyleRequests)
        } ?: logHeaders(headers(request), randomPostTag, curlStyle = curlStyleRequests)

        body(request)?.let {

            val buffer = Buffer()
            val charset = getCharset(it.contentType())

            it.writeTo(buffer)

            if (isPlaintext(buffer)) {

                val body = networkingLogSanitizer?.let {
                    truncate(
                        networkingLogSanitizer.sanitizeBody(
                            buffer.clone().readString(charset)
                        )
                    )
                } ?: truncate(buffer.clone().readString(charset))

                try {
                    val wrappedLines = BasicTextWrapper.wrapMonospaceText(
                        body.replace(",", ", "),
                        150
                    )
                    logLines(wrappedLines, randomPostTag, curlStyle = curlStyleRequests)
                } catch (oom: OutOfMemoryError) {
                    Fore.getLogger(logger)
                        .e("Network request was too large to format nicely, consider reducing maxBodyLogCharacters from:$maxBodyLogCharacters")
                    Fore.getLogger(logger).e(oom.toString())
                }

            } else {
                Fore.getLogger(logger).i(TAG + randomPostTag, "$method- binary data -")
            }
        }

        return url to method
    }

    private fun logResponse(
        response: Response,
        randomPostTag: String,
        method: String?,
        url: HttpUrl?,
        timeTaken: Long
    ) {

        Fore.getLogger(logger).i(
            TAG + randomPostTag,
            "HTTP " + method + " <-- Server replied HTTP-${code(response)}  " +
                    "${nanosFormat.format(timeTaken / (1000 * 1000))} ms $url"
        )

        networkingLogSanitizer?.let {
            logHeaders(it.sanitizeHeaders(headers(response)), randomPostTag)
        } ?: logHeaders(headers(response), randomPostTag)

        body(response)?.let {
            val contentLength = it.contentLength()
            val charset = getCharset(it.contentType())
            val source = it.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            if (!isPlaintext(buffer)) {
                Fore.getLogger(logger).i(
                    TAG + randomPostTag,
                    " (binary body omitted)"
                )
            } else {
                if (contentLength != 0L) {
                    val bodyJson = truncate(buffer.clone().readString(charset))
                    try {
                        val wrappedLines =
                            BasicTextWrapper.wrapMonospaceText(bodyJson.replace(",", ", "), 150)
                        logLines(wrappedLines, randomPostTag)
                    } catch (oom: OutOfMemoryError) {
                        Fore.getLogger(logger)
                            .e("Network response was too large to format nicely, consider reducing maxBodyLogCharacters from:$maxBodyLogCharacters")
                        Fore.getLogger(logger).e(oom.toString())
                    }
                } else {
                    Fore.getLogger(logger).i(TAG + randomPostTag, " (no body content)")
                }
            }
        }
    }

    private fun logLines(
        wrappedLines: List<String>,
        rndmPostTag: String,
        curlStyle: Boolean = false
    ) {
        if (curlStyle) {
            wrappedLines.withIndex().forEach {
                if (it.index == 0) {
                    Fore.getLogger(logger).i(TAG + rndmPostTag, " -d '${it.value}")
                } else if (it.index == wrappedLines.size - 1) {
                    Fore.getLogger(logger).i(TAG + rndmPostTag, "${it.value}'")
                } else {
                    Fore.getLogger(logger).i(TAG + rndmPostTag, it.value)
                }
            }
        } else {
            for (line in wrappedLines) {
                Fore.getLogger(logger).i(TAG + rndmPostTag, line)
            }
        }
    }

    private fun truncate(potentiallyLongString: String): String {
        return if (potentiallyLongString.length > maxBodyLogCharacters) {
            potentiallyLongString.substring(0, maxBodyLogCharacters) + "...truncated " +
                    if (maxBodyLogCharacters < BIG_LOG) {
                        "(set maxBodyLogCharacters=BIG_LOG)"
                    } else ""
        } else {
            potentiallyLongString
        }
    }

    private fun logHeaders(headers: Headers, randomPostTag: String, curlStyle: Boolean = false) {
        for (headerName in headers.names()) {
            if (curlStyle) {
                Fore.getLogger(logger).i(
                    TAG + randomPostTag,
                    String.format(" --header '%s: %s' \\", headerName, headers[headerName])
                )
            } else {
                Fore.getLogger(logger).i(
                    TAG + randomPostTag,
                    String.format("    %s: %s", headerName, headers[headerName])
                )
            }
        }
    }

    private fun getCharset(contentType: MediaType?): Charset {
        var charset = UTF8
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        return charset
    }

    private inline fun <T> measureNanos(function: () -> T): Pair<T, Long> {
        val startTime = Fore.getSystemTimeWrapper().nanoTime()
        val result: T = function.invoke()
        return result to (System.nanoTime() - startTime)
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val size = size(buffer)
            val byteCount = if (size < 64) size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }

    //OkHttp3 v3.X.X used by Retrofit2 and Apollo has method calls: method(), body(), code() etc
    //OkHttp3 v4.X.X used by Ktor has fields: method, body, code etc instead

    private fun size(buffer: Buffer): Long = call(buffer, "size") as Long

    private fun method(request: Request): String = call(request, "method") as String
    private fun url(request: Request): HttpUrl = call(request, "url") as HttpUrl
    private fun headers(request: Request): Headers = call(request, "headers") as Headers
    private fun body(request: Request): RequestBody? =
        call(request, "body")?.let { it as RequestBody }

    private fun code(response: Response): Int = call(response, "code") as Int
    private fun headers(response: Response): Headers = call(response, "headers") as Headers
    private fun body(response: Response): ResponseBody? =
        call(response, "body")?.let { it as ResponseBody }

    private fun call(clazz: Any, name: String): Any? {
        return clazz::class.members.find {
            it.name == name && it.parameters.size == 1 && it.visibility == KVisibility.PUBLIC
        }?.let {
            it.call(clazz)
        }
    }
}

