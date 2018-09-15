package co.early.fore.core.logging;


import co.early.fore.core.utils.TextPaddingUtils;

public class SystemLogger implements Logger {

    private int longestTagLength = 0;

    @Override
    public void e(String tag, String message) {
        System.out.println("(E) " + padTagWithSpace(tag) + "|" + message);
    }

    @Override
    public void w(String tag, String message) {
        System.out.println("(W) " + padTagWithSpace(tag) + "|" + message);
    }

    @Override
    public void i(String tag, String message) {
        System.out.println("(I) " + padTagWithSpace(tag) + "|" + message);
    }

    @Override
    public void d(String tag, String message) {
        System.out.println("(D) " + padTagWithSpace(tag) + "|" + message);
    }

    @Override
    public void v(String tag, String message) {
        System.out.println("(V) " + padTagWithSpace(tag) + "|" + message);
    }


    @Override
    public void e(String tag, String message, Throwable throwable) {
        e(tag, message);
        System.out.println(throwable);
    }

    @Override
    public void w(String tag, String message, Throwable throwable) {
        w(tag, message);
        System.out.println(throwable);
    }

    @Override
    public void i(String tag, String message, Throwable throwable) {
        i(tag, message);
        System.out.println(throwable);
    }

    @Override
    public void d(String tag, String message, Throwable throwable) {
        d(tag, message);
        System.out.println(throwable);
    }

    @Override
    public void v(String tag, String message, Throwable throwable) {
        v(tag, message);
        System.out.println(throwable);
    }


    private String padTagWithSpace(String tag){

        longestTagLength = Math.max(longestTagLength, tag.length() + 1);

        if (longestTagLength != tag.length()) {
            return TextPaddingUtils.padText(tag, longestTagLength, TextPaddingUtils.Padding.END, ' ');
        }else{
            return tag;
        }
    }
}
