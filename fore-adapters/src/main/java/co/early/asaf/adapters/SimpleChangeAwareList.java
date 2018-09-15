package co.early.asaf.adapters;

import java.util.ArrayList;
import java.util.Collection;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.time.SystemTimeWrapper;

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
 * <p>As this needs to be done for every list and it's reasonably involved, this class handles this work
 * automatically but it only supports single item changes at a time.
 *
 * @deprecated use {@link ChangeAwareList} {@link ChangeAwareArrayList} {@link ChangeAwareLinkedList} instead.
 *
 */
@Deprecated
public class SimpleChangeAwareList<T> extends ArrayList<T> implements Updateable {

    private final SystemTimeWrapper systemTimeWrapper;
    private UpdateSpec updateSpec;

    public SimpleChangeAwareList(SystemTimeWrapper systemTimeWrapper) {
        super();
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        updateSpec = createFullUpdateSpec(systemTimeWrapper);
    }

    public SimpleChangeAwareList(int capacity, SystemTimeWrapper systemTimeWrapper) {
        super(capacity);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        updateSpec = createFullUpdateSpec(systemTimeWrapper);
    }

    @Override
    public boolean add(T object) {
        boolean temp = super.add(object);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_INSERTED, size() - 1, 1, systemTimeWrapper);
        return temp;
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_INSERTED, index, 1, systemTimeWrapper);
    }

    @Override
    public T set(int index, T object) {
        T temp = super.set(index, object);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_CHANGED, index, 1, systemTimeWrapper);
        return temp;
    }

    @Override
    public T remove(int index) {
        T temp = super.remove(index);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_REMOVED, index, 1, systemTimeWrapper);
        return temp;
    }

    @Override
    public void clear() {
        int size = size();
        super.clear();
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_REMOVED, 0, size, systemTimeWrapper);
    }


    /**
     * Inform the list that the content of one of its rows has
     * changed.
     *
     * Without calling this, the list may not be aware of
     * any changes made to this row and the updateSpec will
     * be incorrect as a result (you won't get default
     * animations on your recyclerview)
     *
     * @param rowIndex index of the row that had its data changed
     */
    public void makeAwareOfDataChange(int rowIndex){
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_CHANGED, rowIndex, 1, systemTimeWrapper);
    }

    /**
     * Inform the list that the content of a range of its rows has
     * changed.
     *
     * Without calling this, the list will not be aware of
     * any changes made to these rows and the updateSpec will
     * be incorrect as a result (you won't get default
     * animations on your recyclerview)
     *
     * @param rowStartIndex index of the row that had its data changed
     * @param rowsAffected how many rows have been affected
     */
    public void makeAwareOfDataChange(int rowStartIndex, int rowsAffected){
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_CHANGED, rowStartIndex, rowsAffected, systemTimeWrapper);
    }



    @Override
    public boolean addAll(Collection collection) {
        throw new UnsupportedOperationException("Not supported by this class");
    }

    @Override
    public boolean addAll(int index, Collection collection) {
        throw new UnsupportedOperationException("Not supported by this class");
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("Not supported by this class");
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported by this class");
    }

    @Override
    public boolean removeAll(Collection collection) {
        throw new UnsupportedOperationException("Not supported by this class, please use clear() instead");
    }


    /**
     * Make sure you understand the limitations of this method!
     *
     * It essentially only supports one recycleView at a time,
     * in the unlikely event that you want two different list
     * views animating the same changes to this list, you will
     * need to store the updateSpec returned here so that both
     * recycleView adapters get a copy, otherwise the second
     * adapter will always get a FULL_UPDATE (meaning no
     * fancy animations for the second one)
     *
     * If the updateSpec is old, then we assume that whatever changes
     * were made to the Updateable last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the updateSpec and create a fresh one with a
     * full update spec.
     *
     * @return the latest update spec for the list
     */
    public UpdateSpec getAndClearLatestUpdateSpec(long maxAgeMs){

        UpdateSpec latestUpdateSpecAvailable = updateSpec;
        updateSpec = createFullUpdateSpec(systemTimeWrapper);

        if (systemTimeWrapper.currentTimeMillis() - latestUpdateSpecAvailable.timeStamp < maxAgeMs) {
            return latestUpdateSpecAvailable;
        }else{
            return updateSpec;
        }
    }

    private UpdateSpec createFullUpdateSpec(SystemTimeWrapper stw){
        return new UpdateSpec(UpdateSpec.UpdateType.FULL_UPDATE, 0, 0, stw);
    }

}
