package co.early.fore.adapters;


import androidx.recyclerview.widget.RecyclerView;

import co.early.fore.adapters.immutable.DiffSpec;
import co.early.fore.adapters.immutable.Diffable;
import co.early.fore.adapters.mutable.UpdateSpec;
import co.early.fore.adapters.mutable.Updateable;
import co.early.fore.core.Affirm;

public abstract class ChangeAwareAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final int MAX_AGE_MS_BEFORE_IGNORE = 50;

    private final Updateable updateable;
    private final Diffable diffable;

    public ChangeAwareAdapter(Updateable updateable) {
        this.updateable = Affirm.notNull(updateable);
        this.diffable = null;
    }

    public ChangeAwareAdapter(Diffable diffable) {
        this.updateable = null;
        this.diffable = Affirm.notNull(diffable);
    }

    public void notifyDataSetChangedAuto() {
        if (updateable != null){
            processUpdateable();
        } else {
            processDiffable();
        }
    }

    private void processUpdateable(){

        UpdateSpec updateSpec = updateable.getAndClearLatestUpdateSpec(MAX_AGE_MS_BEFORE_IGNORE);

        switch (updateSpec.type) {
            case FULL_UPDATE:
                notifyDataSetChanged();
                break;
            case ITEM_CHANGED:
                notifyItemRangeChanged(updateSpec.rowPosition, updateSpec.rowsEffected);
                break;
            case ITEM_REMOVED:
                notifyItemRangeRemoved(updateSpec.rowPosition, updateSpec.rowsEffected);
                break;
            case ITEM_INSERTED:
                notifyItemRangeInserted(updateSpec.rowPosition, updateSpec.rowsEffected);
                break;
        }
    }

    private void processDiffable(){

        DiffSpec diffSpec = diffable.getAndClearLatestDiffSpec(MAX_AGE_MS_BEFORE_IGNORE);

        if (diffSpec.diffResult == null) {
            notifyDataSetChanged();
        }else {
            diffSpec.diffResult.dispatchUpdatesTo(this);
        }
    }
}
