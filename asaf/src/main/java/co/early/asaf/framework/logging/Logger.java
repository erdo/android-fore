package co.early.asaf.framework.logging;


public interface Logger {

    void e(String tag, String message);
    void w(String tag, String message);
    void i(String tag, String message);
    void d(String tag, String message);
    void v(String tag, String message);

    void e(String tag, String message, Throwable throwable);
    void w(String tag, String message, Throwable throwable);
    void i(String tag, String message, Throwable throwable);
    void d(String tag, String message, Throwable throwable);
    void v(String tag, String message, Throwable throwable);
}
