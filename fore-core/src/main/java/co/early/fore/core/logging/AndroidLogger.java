package co.early.fore.core.logging;

import android.util.Log;


public class AndroidLogger implements Logger{

    private String tagPrefix = null;

    public AndroidLogger() {
    }

    public AndroidLogger(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public void e(String tag, String message) {
        Log.e(addTagPrefixIfPresent(tag), message);
    }

    public void w(String tag, String message) {
        Log.w(addTagPrefixIfPresent(tag), message);
    }

    public void i(String tag, String message) {
        Log.i(addTagPrefixIfPresent(tag), message);
    }

    public void d(String tag, String message) {
        Log.d(addTagPrefixIfPresent(tag), message);
    }

    public void v(String tag, String message) {
        Log.v(addTagPrefixIfPresent(tag), message);
    }

    public void e(String tag, String message, Throwable throwable) {
        Log.e(addTagPrefixIfPresent(tag), message, throwable);
    }

    public void w(String tag, String message, Throwable throwable) {
        Log.w(addTagPrefixIfPresent(tag), message, throwable);
    }

    public void i(String tag, String message, Throwable throwable) {
        Log.i(addTagPrefixIfPresent(tag), message, throwable);
    }

    public void d(String tag, String message, Throwable throwable) {
        Log.d(addTagPrefixIfPresent(tag), message, throwable);
    }

    public void v(String tag, String message, Throwable throwable) {
        Log.v(addTagPrefixIfPresent(tag), message, throwable);
    }

    private String addTagPrefixIfPresent(String message){
        return (tagPrefix == null ? message : tagPrefix + message);
    }
}
