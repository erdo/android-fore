package co.early.fore.adapters.mutable;

import co.early.fore.core.time.SystemTimeWrapper;

/**
 * Indicates what was the most recent change in a list, helps the ChangeAware* classes
 * call the correct notify* method for android adapters to take advantage of built in
 * list animations
 */
public class UpdateSpec {

    public enum UpdateType {
        FULL_UPDATE,
        ITEM_CHANGED,
        ITEM_INSERTED,
        ITEM_REMOVED
    }

    public final UpdateType type;
    public final int rowPosition;
    public final int rowsEffected;
    public final long timeStamp;

    public UpdateSpec(UpdateType type, int rowPosition, int rowsEffected, SystemTimeWrapper systemTimeWrapper) {
        this.type = notNull(type);
        this.rowPosition = rowPosition;
        this.rowsEffected = rowsEffected;
        this.timeStamp = notNull(systemTimeWrapper).currentTimeMillis();
    }

    private <T> T notNull(T param) {
        if (param == null) {
            throw new NullPointerException("Parameter must not be null");
        }
        return param;
    }
}
