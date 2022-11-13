package co.early.fore.net

import okhttp3.Interceptor.Chain
import co.early.fore.core.utils.text.BasicTextWrapper.wrapMonospaceText
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import okhttp3.*
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

/**
 * In order to log HTTP calls, add this interceptor at the bottom of your interceptor chain
 * when creating an OkHttpConfig. The log tag will include a random string eg "B4D32" which
 * will remain constant for a given call (request and response), so that you can use it to
 * correlate logs in the event that you have many calls being logged simultaneously
 */
@Deprecated(message = "will be removed in the next major version", replaceWith = ReplaceWith(expression = "co.early.fore.kt.net.InterceptorLogging"))
class InterceptorLogging @JvmOverloads constructor(
    logger: Logger,
    maxBodyLogCharacters: Int = DEAFULT_MAX_BODY_LOG_LENGTH,
    networkingLogSanitizer: NetworkingLogSanitizer? = null
) : Interceptor {
    private val MAX_BODY_LOG_LENGTH: Int
    var UTF8 = Charset.forName("UTF-8")
    private val logger: Logger
    private val random = Random()
    private val someCharacters = "ABDEFGH023456789".toCharArray()
    private val networkingLogSanitizer: NetworkingLogSanitizer?

    constructor(logger: Logger, networkingLogSanitizer: NetworkingLogSanitizer?) : this(
        logger,
        DEAFULT_MAX_BODY_LOG_LENGTH,
        networkingLogSanitizer
    )

    init {
        this.logger = logger
        MAX_BODY_LOG_LENGTH = maxBodyLogCharacters
        this.networkingLogSanitizer = networkingLogSanitizer
        require(maxBodyLogCharacters >= 1) { "maxBodyLogCharacters must be greater than 0" }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val request: Request = chain.request()
        val method = request.method
        val url = request.url.toString()
        val randomPostTag = (" " + someCharacters[random.nextInt(15)]
                + someCharacters[random.nextInt(15)]
                + someCharacters[random.nextInt(15)]
                + someCharacters[random.nextInt(15)]
                + someCharacters[random.nextInt(15)])

        //request
        logger.i(TAG + randomPostTag, String.format("HTTP %s --> %s", method, url))
        if (networkingLogSanitizer == null) {
            logHeaders(request.headers, randomPostTag)
        } else {
            logHeaders(networkingLogSanitizer.sanitizeHeaders(request.headers), randomPostTag)
        }
        val requestBody = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val charset = getCharset(requestBody.contentType())
            if (isPlaintext(buffer)) {
                val body: String = if (networkingLogSanitizer == null) {
                    truncate(buffer.clone().readString(charset!!))
                } else {
                    truncate(
                        networkingLogSanitizer.sanitizeBody(
                            buffer.clone().readString(charset!!)
                        )
                    )
                }
                val wrappedLines = wrapMonospaceText(body.replace(",", ", "), 150)
                synchronized(this) {
                    for (line in wrappedLines) {
                        logger.i(TAG + randomPostTag, line)
                    }
                }
            } else {
                logger.i(TAG + randomPostTag, "$method- binary data -")
            }
        }

        //response
        val t1 = Fore.getSystemTimeWrapper().nanoTime()
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            logger.e(
                TAG + randomPostTag,
                "HTTP $method <-- Connection dropped, but GETs will be retried $url : $e"
            )
            throw e
        }
        val t2 = Fore.getSystemTimeWrapper().nanoTime()
        logger.i(
            TAG + randomPostTag,
            "HTTP " + method + " <-- Server replied HTTP-" + response.code + " " + (t2 - t1) / (1000 * 1000) + "ms " + url
        )
        if (networkingLogSanitizer == null) {
            logHeaders(response.headers, randomPostTag)
        } else {
            logHeaders(networkingLogSanitizer.sanitizeHeaders(response.headers), randomPostTag)
        }
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        if (response.promisesBody()) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            val charset = getCharset(responseBody.contentType())
            if (!isPlaintext(buffer)) {
                logger.i(TAG + randomPostTag, " (binary " + buffer.size + " byte body omitted)")
                return response
            } else {
                if (contentLength != 0L) {
                    val bodyJson = truncate(buffer.clone().readString(charset!!))
                    val wrappedLines = wrapMonospaceText(bodyJson.replace(",", ", "), 150)
                    synchronized(this) {
                        for (line in wrappedLines) {
                            logger.i(TAG + randomPostTag, line)
                        }
                    }
                } else {
                    logger.i(TAG + randomPostTag, " (no body content)")
                }
            }
        }
        return response
    }

    private fun truncate(potentiallyLongString: String): String {
        return if (potentiallyLongString.length > MAX_BODY_LOG_LENGTH) {
            potentiallyLongString.substring(0, MAX_BODY_LOG_LENGTH) + "...truncated"
        } else {
            potentiallyLongString
        }
    }

    private fun logHeaders(headers: Headers, randomPostTag: String) {
        for (headerName in headers.names()) {
            logger.i(
                TAG + randomPostTag,
                String.format("    %s: %s", headerName, headers[headerName])
            )
        }
    }

    private fun getCharset(contentType: MediaType?): Charset? {
        var charset = UTF8
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        return charset
    }

    companion object {
        private const val TAG = "Network"
        private const val DEAFULT_MAX_BODY_LOG_LENGTH = 4000

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
}
