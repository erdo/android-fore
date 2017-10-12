package co.early.asaf.adapters;

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

    public UpdateSpec(UpdateType type, int rowPosition, int rowsEffected) {
        this.type = type;
        this.rowPosition = rowPosition;
        this.rowsEffected = rowsEffected;
    }
}
