package co.early.fore.adapters;

import android.os.Build;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.UnaryOperator;

import co.early.fore.core.Affirm;
import co.early.fore.core.time.SystemTimeWrapper;

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
 * automatically - but it only supports one type of change at a time (you can't handle a cell update;
 * two removals; and three inserts, all in the same notify round trip - each has to be handled one at a time)
 *
 *
 */
public class ChangeAwareLinkedList<T> extends LinkedList<T> implements ChangeAwareList<T>, Updateable {

    private final SystemTimeWrapper systemTimeWrapper;
    private UpdateSpec updateSpec;

    public ChangeAwareLinkedList(SystemTimeWrapper systemTimeWrapper) {
        super();
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);
        updateSpec = createFullUpdateSpec(systemTimeWrapper);
    }

    public ChangeAwareLinkedList(@NonNull Collection<? extends T> c, SystemTimeWrapper systemTimeWrapper) {
        super(c);
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
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_REMOVED, 0, size(), systemTimeWrapper);
        super.clear();
    }

    @Override
    public boolean addAll(Collection collection) {
        boolean temp = super.addAll(collection);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_INSERTED, size() - 1, collection.size(), systemTimeWrapper);
        return temp;
    }

    @Override
    public boolean addAll(int index, Collection collection) {
        boolean temp = super.addAll(index, collection);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_INSERTED, index, collection.size(), systemTimeWrapper);
        return temp;
    }

    /**
     * Standard AbstractList and ArrayList have this method protected as you
     * are supposed to remove a range by doing this:
     * list.subList(start, end).clear();
     * (clear() ends up calling removeRange() behind the scenes).
     * This won't work for these change aware lists (plus it's a ball ache),
     * so this gets made public
     */
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_REMOVED, fromIndex, toIndex - fromIndex, systemTimeWrapper);
    }

    @Override
    public boolean remove(Object object) {
        int index = super.indexOf(object);
        if (index != -1){
            boolean temp = super.remove(object);
            updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_REMOVED, index, 1, systemTimeWrapper);
            return temp;
        }else{
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection collection) {
        boolean temp = super.removeAll(collection);
        if (temp){
            updateSpec = createFullUpdateSpec(systemTimeWrapper);
        }
        return temp;
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.replaceAll(operator);
            updateSpec = new UpdateSpec(UpdateSpec.UpdateType.ITEM_CHANGED, 0, size(), systemTimeWrapper);
        } else {
            throw new UnsupportedOperationException("Not supported by this class");
        }
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
