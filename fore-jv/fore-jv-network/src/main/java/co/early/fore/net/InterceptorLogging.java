package co.early.fore.net;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

import co.early.fore.core.Affirm;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.utils.text.BasicTextWrapper;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;


/**
 * see https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/java/okhttp3/logging/HttpLoggingInterceptor.java
 */
public class InterceptorLogging implements Interceptor {

    private final static String TAG = "Network";
    private final int MAX_BODY_LOG_LENGTH;
    private static final int DEAFULT_MAX_BODY_LOG_LENGTH = 4000;
    Charset UTF8 = Charset.forName("UTF-8");
    private final Logger logger;
    private final Random random = new Random();
    private char[] somecharacters = "ABDEFGH023456789".toCharArray();
    private NetworkingLogSanitizer networkingLogSanitizer;

    public InterceptorLogging(Logger logger) {
        this(logger, DEAFULT_MAX_BODY_LOG_LENGTH, null);
    }

    public InterceptorLogging(Logger logger, int maxBodyLogCharacters) {
        this(logger, maxBodyLogCharacters, null);
    }

    public InterceptorLogging(Logger logger, NetworkingLogSanitizer networkingLogSanitizer) {
        this(logger, DEAFULT_MAX_BODY_LOG_LENGTH, networkingLogSanitizer);
    }

    public InterceptorLogging(Logger logger, int maxBodyLogCharacters, NetworkingLogSanitizer networkingLogSanitizer) {
        this.logger = Affirm.notNull(logger);
        this.MAX_BODY_LOG_LENGTH = maxBodyLogCharacters;
        this.networkingLogSanitizer = networkingLogSanitizer;

        if (maxBodyLogCharacters<1){
            throw new IllegalArgumentException("maxBodyLogCharacters must be greater than 0");
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String method = request.method();
        String url = request.url().toString();

        String randomPostTag = " " + somecharacters[random.nextInt(15)]
                + somecharacters[random.nextInt(15)]
                + somecharacters[random.nextInt(15)]
                + somecharacters[random.nextInt(15)]
                + somecharacters[random.nextInt(15)];

        //request

        logger.i(TAG + randomPostTag, String.format("HTTP %s --> %s", method, url));

        if (networkingLogSanitizer == null){
            logHeaders(request.headers(), randomPostTag);
        } else {
            logHeaders(networkingLogSanitizer.sanitizeHeaders(request.headers()), randomPostTag);
        }

        RequestBody requestBody = request.body();

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = getCharset(requestBody.contentType());
            if (isPlaintext(buffer)) {

                String body;
                if (networkingLogSanitizer == null){
                    body = truncate(buffer.clone().readString(charset));
                } else {
                    body = truncate(networkingLogSanitizer.sanitizeBody(buffer.clone().readString(charset)));
                }

                List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(body.replace(",", ", "), 150);
                synchronized (this) {
                    for (String line : wrappedLines) {
                        logger.i(TAG + randomPostTag, line);
                    }
                }
            } else {
                logger.i(TAG + randomPostTag, method + "- binary data -");
            }
        }



        //response

        long t1 = System.nanoTime();


        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logger.e(TAG + randomPostTag, "HTTP " + method + " <-- Connection dropped, but GETs will be retried " + url + " : " + e);
            throw e;
        }

        long t2 = System.nanoTime();
        logger.i(TAG + randomPostTag, "HTTP " + method + " <-- Server replied HTTP-" + response.code() + " " + (t2-t1)/(1000*1000) + "ms " + url);


        if (networkingLogSanitizer == null){
            logHeaders(response.headers(), randomPostTag);
        } else {
            logHeaders(networkingLogSanitizer.sanitizeHeaders(response.headers()), randomPostTag);
        }

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        if (HttpHeaders.hasBody(response)){
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.getBuffer();
            Charset charset = getCharset(responseBody.contentType());
            if (!isPlaintext(buffer)) {
                logger.i(TAG + randomPostTag, " (binary " + buffer.size() + " byte body omitted)");
                return response;
            }else {
                if (contentLength != 0) {
                    String bodyJson = truncate(buffer.clone().readString(charset));
                    List<String> wrappedLines = BasicTextWrapper.wrapMonospaceText(bodyJson.replace(",", ", "), 150);
                    synchronized (this) {
                        for (String line : wrappedLines) {
                            logger.i(TAG + randomPostTag, line);
                        }
                    }
                } else {
                    logger.i(TAG + randomPostTag, " (no body content)");
                }
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private String truncate(String potentiallyLongString){
        if (potentiallyLongString.length()>MAX_BODY_LOG_LENGTH){
            return potentiallyLongString.substring(0, MAX_BODY_LOG_LENGTH) + "...truncated";
        }else {
            return potentiallyLongString;
        }
    }

    private void logHeaders(Headers headers, String randomPostTag){
        for(String headerName: headers.names()){
            logger.i(TAG + randomPostTag, String.format("    %s: %s", headerName, headers.get(headerName)));
        }
    }

    private Charset getCharset(MediaType contentType){
        Charset charset = UTF8;
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        return charset;
    }
}
