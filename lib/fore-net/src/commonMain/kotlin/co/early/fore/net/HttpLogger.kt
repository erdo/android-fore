//package co.early.fore.net
//
//import co.early.fore.core.delegate.Fore
//import co.early.fore.core.logging.Logger
//import co.early.fore.core.logging.SilentLogger
//import co.early.fore.net.BodyRenderFormat.*
//import okio.Buffer
//import kotlin.random.Random
//
//private const val someCharacters = "ABDEFGH023456789"
//
//expect fun getUrl(httpTransaction: Any): String
//expect fun getMethod(httpTransaction: Any): String
//expect fun getRequestHeaders(httpTransaction: Any): List<Pair<String, String>>
//expect fun getRequestBody(
//    httpTransaction: Any,
//    maxBodyLogCharacters: Int
//): Buffer // (body.take(maxBodyLogCharacters))
//
//expect fun processRequest(httpTransaction: Any): Any
//expect fun getResponseCode(httpTransaction: Any): String
//expect fun getResponseHeaders(httpTransaction: Any): List<Pair<String, String>>  // Pair<Key, Value>
//expect fun getResponseBody(httpTransaction: Any, maxBodyLogCharacters: Int): Buffer
//
//class HttpLogger(
//    private val httpTransaction: Any // platform independent representation
//) {
//
//    private var printedWarningAlready = false
//    private val someChars = someCharacters.toCharArray()
//
//    fun logTransaction(
//        tag: String = "Net",
//        curlStyleRequestLogging: Boolean = true,
//        maxBodyLogBytes: Int = 4000,
//        networkingLogSanitizer: NetworkingLogSanitizer? = null,
//        logger: Logger? = null,
//    ) {
//        val compositeTag = "$tag ${createRandomTag()}"
//        val lggr = Fore.getLogger(logger)
//
//        if (lggr !is SilentLogger) {
//
//            val method = getMethod(httpTransaction)
//            val url = getUrl(httpTransaction)
//
//            uselocks to ensure request and response are grouped
//
//            logRequestInfo(
//                compositeTag = compositeTag,
//                method = method,
//                url = url,
//                curlStyleRequestLogging = curlStyleRequestLogging,
//                logger = lggr,
//            )
//
//            logHeaders(
//                compositeTag = compositeTag,
//                headers = getRequestHeaders(httpTransaction),
//                curlStyleRequestLogging = curlStyleRequestLogging,
//                networkingLogSanitizer = networkingLogSanitizer,
//                logger = lggr,
//            )
//
//            logBody(
//                compositeTag = compositeTag,
//                body = getRequestBody(httpTransaction, maxBodyLogBytes),
//                maxBodyLogBytes = maxBodyLogBytes.toLong(),
//                curlStyleRequestLogging = curlStyleRequestLogging,
//                networkingLogSanitizer = networkingLogSanitizer,
//                logger = lggr,
//            )
//
//            val response = measureNanos {
//                try {
//                    processRequest(httpTransaction)
//                } catch (e: Throwable) {
//                    lggr.e(
//                        compositeTag,
//                        "HTTP $method <-- Connection dropped, GETs may be retried $url : $e"
//                    )
//                    throw e
//                }
//            }
//
//            logResponseInfo(
//                compositeTag = compositeTag,
//                method = method,
//                code = getResponseCode(response.first),
//                timeTakenNs = response.second,
//                url = url,
//                logger = lggr,
//            )
//
//            logHeaders(
//                compositeTag = compositeTag,
//                headers = getResponseHeaders(response.first),
//                curlStyleRequestLogging = false,
//                networkingLogSanitizer = networkingLogSanitizer,
//                logger = lggr,
//            )
//
//            logBody(
//                compositeTag = compositeTag,
//                body = getResponseBody(response.first, maxBodyLogBytes),
//                maxBodyLogBytes = maxBodyLogBytes.toLong(),
//                curlStyleRequestLogging = false,
//                networkingLogSanitizer = networkingLogSanitizer,
//                logger = lggr,
//            )
//        }
//    }
//
//    private fun createRandomTag() = " " +
//            nextChar(someChars) +
//            nextChar(someChars) +
//            nextChar(someChars) +
//            nextChar(someChars) +
//            nextChar(someChars)
//
//    private fun nextChar(someChars: CharArray) =
//        someChars[Random.Default.nextInt(someChars.size - 1)]
//
//
//
//
//
//
//}
//
///**
// * In order to log HTTP calls, add this interceptor at the bottom of your interceptor chain
// * when creating an OkHttpConfig. The log tag will include a random string eg "Net B4D32" which
// * will remain constant for a given call (request and response), so that you can use it to
// * correlate logs in the event that you have many calls being logged simultaneously
// */
//class HttpLoggerx constructor(
//    private val logger: Logger? = null,
//    private val maxBodyLogCharacters: Int = 4000,
//    private val networkingLogSanitizer: NetworkingLogSanitizer? = null,
//    private val curlStyleRequests: Boolean = true,
//) : Interceptor {
//
//    private val nanosFormat = DecimalFormat("#,###")
//    private val UTF8 = Charset.forName("UTF-8")
//    private val random = Random()
//    private val someCharacters = "ABDEFGH023456789".toCharArray()
//    private val TAG = "Net"
//    private val logLinesLock = ReentrantLock(true)
//    private var printedWarningAlready = false
//
//    init {
//        require(maxBodyLogCharacters >= 1) { "maxBodyLogCharacters must be greater than 0" }
//    }
//
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        var url: HttpUrl? = null
//        var method: String? = null
//
//
//        logLinesLock.withLock {
//            try {
//                val pair = logRequest(request, randomPostTag)
//                url = pair.first
//                method = pair.second
//            } catch (t: Throwable) {
//                logWarning(t)
//            }
//        }
//
//        val decoratedResponse = measureNanos {
//            try {
//                chain.proceed(request)
//            } catch (e: Throwable) {
//                Fore.getLogger(logger).e(
//                    TAG + randomPostTag,
//                    "HTTP $method <-- Connection dropped, GETs may be retried $url : $e"
//                )
//                throw e
//            }
//        }
//        val response = decoratedResponse.first
//        val timeTaken = decoratedResponse.second
//
//        logLinesLock.withLock {
//            try {
//                logResponse(response, randomPostTag, method, url, timeTaken)
//            } catch (t: Throwable) {
//                logWarning(t)
//            }
//        }
//
//        return response
//    }
//
//
//    private fun logRequest(request: Request, randomPostTag: String): Pair<HttpUrl, String> {
//
//        val method = method(request)
//        val url = url(request)
//
//
//
//        body(request)?.let {
//
//            val buffer = Buffer()
//            val charset = getCharset(it.contentType())
//
//            it.writeTo(buffer)
//
//            if (isPlaintext(buffer)) {
//
//                val body = networkingLogSanitizer?.let {
//                    truncate(
//                        networkingLogSanitizer.sanitizeBody(
//                            buffer.clone().readString(charset)
//                        )
//                    )
//                } ?: truncate(buffer.clone().readString(charset))
//
//                try {
//                    val wrappedLines = wrapMonospaceText(
//                        body.replace(",", ", "),
//                        150
//                    )
//                    logLines(wrappedLines, randomPostTag, curlStyle = curlStyleRequests)
//                } catch (oom: OutOfMemoryError) {
//                    Fore.getLogger(logger)
//                        .e("Network request was too large to format nicely, consider reducing maxBodyLogCharacters from:$maxBodyLogCharacters")
//                    Fore.getLogger(logger).e(oom.toString())
//                }
//
//            } else {
//                Fore.getLogger(logger).i(TAG + randomPostTag, "$method- binary data -")
//            }
//        }
//
//        return url to method
//    }
//
//    private fun logResponse(
//        response: Response,
//        randomPostTag: String,
//        method: String?,
//        url: HttpUrl?,
//        timeTaken: Long
//    ) {
//
//        networkingLogSanitizer?.let {
//
//            val headers = headers(response).flatMap { header ->
//                header.toList()
//            }
//
//            logHeaders(it.sanitizeHeaders(headers), randomPostTag)
//        } ?: logHeaders(headers(response), randomPostTag)
//
//        body(response)?.let {
//            val contentLength = it.contentLength()
//            val charset = getCharset(it.contentType())
//            val source = it.source()
//            source.request(Long.MAX_VALUE) // Buffer the entire body.
//            val buffer = source.buffer
//            if (!isPlaintext(buffer)) {
//                Fore.getLogger(logger).i(
//                    TAG + randomPostTag,
//                    " (binary body omitted)"
//                )
//            } else {
//                if (contentLength != 0L) {
//                    val bodyJson = truncate(buffer.clone().readString(charset))
//                    try {
//                        val wrappedLines =
//                            wrapMonospaceText(bodyJson.replace(",", ", "), 150)
//                        logLines(wrappedLines, randomPostTag)
//                    } catch (oom: OutOfMemoryError) {
//                        Fore.getLogger(logger)
//                            .e("Network response was too large to format nicely, consider reducing maxBodyLogCharacters from:$maxBodyLogCharacters")
//                        Fore.getLogger(logger).e(oom.toString())
//                    }
//                } else {
//                    Fore.getLogger(logger).i(TAG + randomPostTag, " (no body content)")
//                }
//            }
//        }
//    }
//
//    private fun logLines(
//        wrappedLines: List<String>,
//        rndmPostTag: String,
//        curlStyle: Boolean = false
//    ) {
//        if (curlStyle) {
//            wrappedLines.withIndex().forEach {
//                if (it.index == 0) {
//                    Fore.getLogger(logger).i(TAG + rndmPostTag, " -d '${it.value}")
//                } else if (it.index == wrappedLines.size - 1) {
//                    Fore.getLogger(logger).i(TAG + rndmPostTag, "${it.value}'")
//                } else {
//                    Fore.getLogger(logger).i(TAG + rndmPostTag, it.value)
//                }
//            }
//        } else {
//            for (line in wrappedLines) {
//                Fore.getLogger(logger).i(TAG + rndmPostTag, line)
//            }
//        }
//    }
//
//
//
//    private fun getCharset(contentType: MediaType?): Charset {
//        var charset = UTF8
//        if (contentType != null) {
//            charset = contentType.charset(charset)
//        }
//        return charset
//    }
//
//    companion object {
//        val JsonPretty = Json {
//            prettyPrint = true
//        }
//    }
//}
