package co.early.fore.kt.net

import co.early.fore.core.utils.text.BasicTextWrapper
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.time.measureNanos
import co.early.fore.kt.core.time.nanosFormat
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.locks.ReentrantLock

import co.early.fore.net.NetworkingLogSanitizer
import okhttp3.*
import okio.Buffer
import kotlin.reflect.KVisibility

/**
 * see https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/java/okhttp3/logging/HttpLoggingInterceptor.java
 */
class InterceptorLogging @JvmOverloads constructor(
        private val logger: Logger? = null,
        private val maxBodyLogCharacters: Int = 4000,
        private val networkingLogSanitizer: NetworkingLogSanitizer? = null) : Interceptor {

    private val UTF8 = Charset.forName("UTF-8")
    private val random = Random()
    private val someCharacters = "ABDEFGH023456789".toCharArray()
    private val TAG = "Network"
    private val logLinesLock = ReentrantLock()
    private var printedWarningAlready = false

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

        try {
            val pair = logRequest(request, randomPostTag)
            url = pair.first
            method = pair.second
        } catch (t: Throwable) {
            logWarning(t)
        }

        val decoratedResponse = measureNanos {
            try {
                chain.proceed(request)
            } catch (e: Throwable) {
                ForeDelegateHolder.getLogger(logger).e(
                        TAG + randomPostTag,
                        "HTTP $method <-- Connection dropped, but GETs will be retried $url : $e")
                throw e
            }
        }
        val response = decoratedResponse.first
        val timeTaken = decoratedResponse.second

        try {
            logResponse(response, randomPostTag, method, url, timeTaken)
        } catch (t: Throwable) {
            logWarning(t)
        }

        return response
    }

    private fun logWarning(t: Throwable) {
        if (!printedWarningAlready) {
            ForeDelegateHolder.getLogger(logger).w("No network logging available: fore doesn't recognise this version of OkHttp, or you have excluded kotlin-reflect from your dependencies. ${t.message}")
            printedWarningAlready = true
        }
    }

    private fun logRequest(request: Request, randomPostTag: String): Pair<HttpUrl, String> {

        val method = method(request)
        val url = url(request)

        ForeDelegateHolder.getLogger(logger).i(TAG + randomPostTag, String.format("HTTP %s --> %s", method, url))

        networkingLogSanitizer?.let {
            logHeaders(it.sanitizeHeaders(headers(request)), randomPostTag)
        } ?: logHeaders(headers(request), randomPostTag)

        body(request)?.let {

            val buffer = Buffer()
            val charset = getCharset(it.contentType())

            it.writeTo(buffer)

            if (isPlaintext(buffer)) {

                val body = networkingLogSanitizer?.let {
                    truncate(networkingLogSanitizer.sanitizeBody(buffer.clone().readString(charset)))
                } ?: truncate(buffer.clone().readString(charset))

                val wrappedLines = BasicTextWrapper.wrapMonospaceText(
                        body.replace(",", ", "),
                        150)
                logLines(wrappedLines, randomPostTag)
            } else {
                ForeDelegateHolder.getLogger(logger).i(TAG + randomPostTag, "$method- binary data -")
            }
        }

        return url to method
    }

    private fun logResponse(response: Response, randomPostTag: String, method: String?, url: HttpUrl?, timeTaken: Long) {

        ForeDelegateHolder.getLogger(logger).i(
                TAG + randomPostTag,
                "HTTP " + method + " <-- Server replied HTTP-${code(response)}  " +
                        "${nanosFormat.format(timeTaken / (1000 * 1000))} ms $url")

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
                ForeDelegateHolder.getLogger(logger).i(
                        TAG + randomPostTag,
                        " (binary body omitted)")
            } else {
                if (contentLength != 0L) {
                    val bodyJson = truncate(buffer.clone().readString(charset))
                    val wrappedLines = BasicTextWrapper.wrapMonospaceText(bodyJson.replace(",", ", "), 150)
                    logLines(wrappedLines, randomPostTag)
                } else {
                    ForeDelegateHolder.getLogger(logger).i(TAG + randomPostTag, " (no body content)")
                }
            }
        }
    }

    private fun logLines(wrappedLines: List<String>, rndmPostTag: String) {
        try {
            logLinesLock.lock()
            for (line in wrappedLines) {
                ForeDelegateHolder.getLogger(logger).i(TAG + rndmPostTag, line)
            }
        } finally {
            logLinesLock.unlock()
        }
    }

    private fun truncate(potentiallyLongString: String): String {
        return if (potentiallyLongString.length > maxBodyLogCharacters) {
            potentiallyLongString.substring(0, maxBodyLogCharacters) + "...truncated"
        } else {
            potentiallyLongString
        }
    }

    private fun logHeaders(headers: Headers, randomPostTag: String) {
        for (headerName in headers.names()) {
            ForeDelegateHolder.getLogger(logger).i(TAG + randomPostTag, String.format("    %s: %s", headerName, headers[headerName]))
        }
    }

    private fun getCharset(contentType: MediaType?): Charset {
        var charset = UTF8
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        return charset
    }

    companion object {
        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(buffer: Buffer): Boolean {
            return try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
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
    }

    init {
        require(maxBodyLogCharacters >= 1) { "maxBodyLogCharacters must be greater than 0" }
    }

    //OkHttp3 v3.X.X used by Retrofit2 and Apollo has method calls: method(), body(), code() etc
    //OkHttp3 v4.X.X used by Ktor has fields: method, body, code etc instead

    private fun method(request: Request): String = call(request, "method") as String
    private fun url(request: Request): HttpUrl = call(request, "url") as HttpUrl
    private fun headers(request: Request): Headers = call(request, "headers") as Headers
    private fun body(request: Request): RequestBody? = call(request, "body")?.let { it as RequestBody }

    private fun code(response: Response): Int = call(response, "code") as Int
    private fun headers(response: Response): Headers = call(response, "headers") as Headers
    private fun body(response: Response): ResponseBody? = call(response, "body")?.let { it as ResponseBody }

    private fun call(clazz: Any, name: String): Any? {
        return clazz::class.members.find {
            it.name == name && it.parameters.size == 1 && it.visibility == KVisibility.PUBLIC
        }?.let {
            it.call(clazz)
        }
    }
}

