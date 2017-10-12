package co.early.asaf.adapters;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.time.SystemTimeWrapper;

/**
 *
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
        this.type = Affirm.notNull(type);
        this.rowPosition = rowPosition;
        this.rowsEffected = rowsEffected;
        this.timeStamp = Affirm.notNull(systemTimeWrapper).currentTimeMillis();
    }
}
