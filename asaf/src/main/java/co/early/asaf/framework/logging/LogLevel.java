package co.early.asaf.framework.logging;


public enum LogLevel {
    V,
    D,
    I,
    W,
    E;
    public static LogLevel fromOrdinal(int ordinal){
        return LogLevel.values()[ordinal];
    }
}
