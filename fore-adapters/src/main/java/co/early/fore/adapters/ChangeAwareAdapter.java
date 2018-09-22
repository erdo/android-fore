package co.early.fore.adapters;


import android.support.v7.widget.RecyclerView;

import co.early.fore.core.Affirm;

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
 * <p>As this needs to be done for every list and it's reasonably involved, this class handles much
 * of the work for you
 *
 * <p>The <strong>first method</strong>  is primarily for simple in memory lists, and uses
 * {@link ChangeAwareArrayList} and {@link ChangeAwareLinkedList} in conjunction with
 * {@link Updateable} interface. Please refer to the ASAF adapters source code for example useage.
 *
 *  <p>The <strong>second method</strong> is primarily for lists which are driven by database changes, and is more
 *  computationally intensive, as it uses Android's {@link android.support.v7.util.DiffUtil.DiffResult}
 *  for list comparison in conjunction with the {@link Diffable} and {@link DiffComparator} classes.
 *  Please refer to the ASAF adapters source code for example useage.
 * *
 */
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
        this.diffable = diffable;
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
