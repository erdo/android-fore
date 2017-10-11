package foo.bar.example.asafadapters.framework.list;


import android.support.v7.widget.RecyclerView;

import co.early.asaf.framework.Affirm;
import foo.bar.example.asafadapters.framework.time.SystemTimeWrapper;

import static foo.bar.example.asafadapters.framework.list.UpdateSpec.UpdateType.FULL_UPDATE;

/**
 *
 */
public abstract class SimpleChangeAwareAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final SystemTimeWrapper systemTimeWrapper;
    private final Updateable updateable;

    private long t1 = 0;
    private long UPDATE_SPEC_VALID_FOR_MS = 50;

    public SimpleChangeAwareAdapter(Updateable updateable, SystemTimeWrapper systemTimeWrapper) {
        this.updateable = Affirm.notNull(updateable);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
    }


    public void notifyDataSetChangedAuto(){

        UpdateSpec updateSpec = getFreshUpdateSpec();

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


    /**
     * Get the latest updateSpec from the Updateable,
     * while the spec is still within UPDATE_SPEC_VALID_FOR_MS.
     *
     * If the updateSpec is old, then we assume that whatever changes
     * were made to the Updateable last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the updateSpec and create a fresh one.
     *
     * @return
     */
    private UpdateSpec getFreshUpdateSpec(){

        UpdateSpec freshUpdateSpec;

        long t2 = systemTimeWrapper.currentTimeMillis();
        if (t2 - t1 < UPDATE_SPEC_VALID_FOR_MS) {
            freshUpdateSpec = updateable.getAndClearMostRecentUpdateSpec();
        }else{
            updateable.getAndClearMostRecentUpdateSpec();
            freshUpdateSpec = new UpdateSpec(FULL_UPDATE, 0, 0);
        }
        t1 = t2;

        return freshUpdateSpec;
    }

}
