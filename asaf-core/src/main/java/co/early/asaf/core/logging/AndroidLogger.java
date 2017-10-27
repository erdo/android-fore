package co.early.asaf.core.logging;

import android.util.Log;


public class AndroidLogger implements Logger{

    public void e(String tag, String message) {
        Log.e(tag, message);
    }

    public void w(String tag, String message) {
        Log.w(tag, message);
    }

    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    public void v(String tag, String message) {
        Log.v(tag, message);
    }

    public void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }

    public void w(String tag, String message, Throwable throwable) {
        Log.w(tag, message, throwable);
    }

    public void i(String tag, String message, Throwable throwable) {
        Log.i(tag, message, throwable);
    }

    public void d(String tag, String message, Throwable throwable) {
        Log.d(tag, message, throwable);
    }

    public void v(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);

    }

}
