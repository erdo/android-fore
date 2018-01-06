package co.early.asaf.adapters;


import android.support.v7.widget.RecyclerView;

import co.early.asaf.core.Affirm;

/**
 * <p>Android adapters used to just have a notifyDataSetChanged() method which would trigger a redraw of the list UI.
 *
 * <code>
 * adapter.notifyDataSetChanged();
 * </code>
 *
 * <p>We now have access to a selection of notifyXXX methods() which indicate to a recyclerview adapter what sort of change
 * happened and which rows were effected (eg: there was an item inserted at position 5; the item at position 3
 * changed etc).
 *
 * <code>
 *
 * UpdateSpec updateSpec = list.getMostRecentUpdateSpec();
 *
 * switch (updateSpec.type) {
 *  case FULL_UPDATE:
 *      adapter.notifyDataSetChanged();
 *      break;
 *  case ITEM_CHANGED:
 *      adapter.notifyItemChanged(updateSpec.rowPosition);
 *      break;
 *  case ITEM_REMOVED:
 *      adapter.notifyItemRemoved(updateSpec.rowPosition);
 *      break;
 *  case ITEM_INSERTED:
 *      adapter.notifyItemInserted(updateSpec.rowPosition);
 *      break;
 * }
 *
 * </code>
 *
 * <p>Android uses this information to apply default animations on the list view (like fading in
 * changes or animating a new row in to position).
 *
 * <p>This architecture puts the onus on the developer to indicate the change type rather than the platform
 * inferring it automatically (like with iOS lists)
 *
 * <p>As this needs to be done for every list and it's reasonably involved, this class uses
 * {@link ChangeAwareArrayList} and {@link ChangeAwareLinkedList} to handle much of the work for you,
 * please refer to the ASAF adapters source code for example useage.
 * *
 */
public abstract class ChangeAwareAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final Updateable updateable;

    public ChangeAwareAdapter(Updateable updateable) {
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
