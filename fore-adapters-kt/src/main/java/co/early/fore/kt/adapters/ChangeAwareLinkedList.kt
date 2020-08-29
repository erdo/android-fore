package co.early.fore.kt.adapters

import co.early.fore.adapters.ChangeAwareList
import co.early.fore.adapters.UpdateSpec
import co.early.fore.adapters.Updateable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import java.util.*
import java.util.function.UnaryOperator

/**
 * This list is aware of how it was changed, to help support android's adapter animations. For example
 * if you add an item, it will create an UpdateSpec that matches that operation.
 *
 * UpdateSpec updateSpec = list.getMostRecentUpdateSpec();
 *
 * switch (updateSpec.type) {
 * case FULL_UPDATE:
 * adapter.notifyDataSetChanged();
 * break;
 * case ITEM_CHANGED:
 * adapter.notifyItemChanged(updateSpec.rowPosition);
 * break;
 * case ITEM_REMOVED:
 * adapter.notifyItemRemoved(updateSpec.rowPosition);
 * break;
 * case ITEM_INSERTED:
 * adapter.notifyItemInserted(updateSpec.rowPosition);
 * break;
 * }
 *
 * The only operations it may not aware of are change operations, so if you change the data in the
 * list without going through this class, it's your responsibility to inform this class by
 * calling the makeAwareOfDataChange() methods
 *
 * This class only supports one type of change at a time (you can't handle a cell update;
 * two removals; and three inserts, all in the same notify round trip - each has to be handled one at a time)
 *
 * @see co.early.fore.adapters.ChangeAwareAdapter
 *
 */
class ChangeAwareLinkedList<T>(
        private val systemTimeWrapper: SystemTimeWrapper? = null
) : LinkedList<T>(), ChangeAwareList<T>, Updateable {

    private var updateSpec: UpdateSpec = createFullUpdateSpec(ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper))

    override fun add(element: T): Boolean {
        val temp = super.add(element)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_INSERTED,
                size - 1,
                1,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
        return temp
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_INSERTED,
                index,
                1,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
    }

    override fun set(index: Int, element: T): T {
        val temp = super.set(index, element)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_CHANGED,
                index,
                1,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
        return temp
    }

    override fun removeAt(index: Int): T {
        val temp = super.removeAt(index)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_REMOVED,
                index,
                1,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
        return temp
    }

    override fun clear() {
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_REMOVED,
                0,
                size,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
        super.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val temp = super.addAll(elements)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_INSERTED,
                size - 1,
                elements.size,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
        return temp
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val temp = super.addAll(index, elements)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_INSERTED,
                index,
                elements.size,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
        return temp
    }

    /**
     * Standard AbstractList and ArrayList have this method protected as you
     * are supposed to remove a range by doing this:
     * list.subList(start, end).clear();
     * (clear() ends up calling removeRange() behind the scenes).
     * This won't work for these change aware lists (plus it's a ball ache),
     * so this gets made public
     */
    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_REMOVED,
                fromIndex,
                toIndex - fromIndex,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
    }

    override fun remove(element: T): Boolean {
        val index = super.indexOf(element)
        return if (index != -1) {
            val temp = super.remove(element)
            updateSpec = UpdateSpec(
                    UpdateSpec.UpdateType.ITEM_REMOVED,
                    index,
                    1,
                    ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
            )
            temp
        } else {
            false
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val temp = super.removeAll(elements)
        if (temp) {
            updateSpec = createFullUpdateSpec(
                    ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
            )
        }
        return temp
    }

    override fun replaceAll(operator: UnaryOperator<T>) {
        throw UnsupportedOperationException("Not supported by this class")
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
    override fun makeAwareOfDataChange(rowIndex: Int) {
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_CHANGED,
                rowIndex,
                1,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
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
    override fun makeAwareOfDataChange(rowStartIndex: Int, rowsAffected: Int) {
        updateSpec = UpdateSpec(
                UpdateSpec.UpdateType.ITEM_CHANGED,
                rowStartIndex,
                rowsAffected,
                ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)
        )
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
    override fun getAndClearLatestUpdateSpec(maxAgeMs: Long): UpdateSpec {
        val latestUpdateSpecAvailable = updateSpec
        updateSpec = createFullUpdateSpec(ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper))
        return if (ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper).currentTimeMillis() - latestUpdateSpecAvailable.timeStamp < maxAgeMs) {
            latestUpdateSpecAvailable
        } else {
            updateSpec
        }
    }

    private fun createFullUpdateSpec(stw: SystemTimeWrapper): UpdateSpec {
        return UpdateSpec(UpdateSpec.UpdateType.FULL_UPDATE, 0, 0, stw)
    }
}
