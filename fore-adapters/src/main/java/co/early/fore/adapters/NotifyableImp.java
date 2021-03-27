package co.early.fore.adapters;

import androidx.recyclerview.widget.RecyclerView;

import co.early.fore.adapters.immutable.DiffSpec;
import co.early.fore.adapters.immutable.Diffable;
import co.early.fore.adapters.mutable.UpdateSpec;
import co.early.fore.adapters.mutable.Updateable;
import co.early.fore.core.Affirm;
import co.early.fore.core.logging.AndroidLogger;
import co.early.fore.core.logging.Logger;

public class NotifyableImp<VH extends RecyclerView.ViewHolder> implements Notifyable<VH> {

    private static final int MAX_AGE_MS_BEFORE_IGNORE = 50;

    private final Updateable updateable;
    private final Diffable diffable;
    private final RecyclerView.Adapter<VH> adapter;

    public NotifyableImp(RecyclerView.Adapter<VH> adapter, Updateable updateable) {
        this.adapter = Affirm.notNull(adapter);
        this.updateable = Affirm.notNull(updateable);
        this.diffable = null;
    }

    public NotifyableImp(RecyclerView.Adapter<VH> adapter, Diffable diffable) {
        this.adapter = Affirm.notNull(adapter);
        this.updateable = null;
        this.diffable = Affirm.notNull(diffable);
    }

    public void notifyDataSetChangedAuto() {
        if (updateable != null) {
            processUpdateable();
        } else {
            processDiffable();
        }
    }

    private void processUpdateable() {

        UpdateSpec updateSpec = updateable.getAndClearLatestUpdateSpec(MAX_AGE_MS_BEFORE_IGNORE);

        switch (updateSpec.type) {
            case FULL_UPDATE:
                adapter.notifyDataSetChanged();
                break;
            case ITEM_CHANGED:
                adapter.notifyItemRangeChanged(updateSpec.rowPosition, updateSpec.rowsEffected);
                break;
            case ITEM_REMOVED:
                adapter.notifyItemRangeRemoved(updateSpec.rowPosition, updateSpec.rowsEffected);
                break;
            case ITEM_INSERTED:
                adapter.notifyItemRangeInserted(updateSpec.rowPosition, updateSpec.rowsEffected);
                break;
        }
    }

    private void processDiffable() {

        DiffSpec diffSpec = diffable.getAndClearLatestDiffSpec(MAX_AGE_MS_BEFORE_IGNORE);

        if (diffSpec.diffResult == null) {
            adapter.notifyDataSetChanged();
        } else {
            diffSpec.diffResult.dispatchUpdatesTo(adapter);
        }
    }
}
