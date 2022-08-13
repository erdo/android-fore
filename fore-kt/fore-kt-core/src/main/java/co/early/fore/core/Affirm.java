package co.early.fore.core;

// this is a remnant of the fore's original java implementation and isn't particularly useful for kotlin, so will be removed in the next major version (the java packages like fore-jv-android will remain as they are)
@Deprecated
public class Affirm {

    public static <T> T notNull(T param) {
        if (param == null) {
            throw new NullPointerException("Parameter must not be null");
        }
        return param;
    }
}
