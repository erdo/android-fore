package co.early.fore.core;

public class Affirm {

    public static <T> T notNull(T param) {
        if (param == null) {
            throw new NullPointerException("Parameter must not be null");
        }
        return param;
    }
}
