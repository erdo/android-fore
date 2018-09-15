package co.early.fore.retrofit;

import okhttp3.Headers;


public interface NetworkingLogSanitizer {
    Headers sanitizeHeaders(Headers allHeaders);
    String sanitizeBody(String text);
}
