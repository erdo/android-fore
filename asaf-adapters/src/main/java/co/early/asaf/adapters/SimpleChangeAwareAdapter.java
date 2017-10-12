package co.early.asaf.adapters;


import android.support.v7.widget.RecyclerView;

import co.early.asaf.core.Affirm;

/**
 *
 */
public abstract class SimpleChangeAwareAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final Updateable updateable;

    public SimpleChangeAwareAdapter(Updateable updateable) {
        this.updateable = Affirm.notNull(updateable);
    }

    public void notifyDataSetChangedAuto(){

        UpdateSpec updateSpec = updateable.getAndClearLatestUpdateSpec(50);

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

}
