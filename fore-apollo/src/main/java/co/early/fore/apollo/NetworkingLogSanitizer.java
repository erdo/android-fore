package co.early.fore.apollo;

import okhttp3.Headers;


public interface NetworkingLogSanitizer {
    Headers sanitizeHeaders(Headers allHeaders);
    String sanitizeBody(String text);
}
