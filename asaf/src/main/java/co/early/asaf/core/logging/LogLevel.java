package co.early.asaf.core.logging;


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
