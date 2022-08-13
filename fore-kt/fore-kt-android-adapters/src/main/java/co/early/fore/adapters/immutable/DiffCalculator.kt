package co.early.fore.adapters.immutable

import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.DiffUtil

class DiffCalculator<T : DiffComparator<T>?> {
    /**
     * A wrapper for Android's DiffUtil. It's recommended that this method is run off the UI thread
     * because it can take some time to generate the DiffResult, especially for large lists or lists
     * with many changes.
     *
     *
     *
     * It's very important that the contents of the two lists are not changed from another thread
     * while the DiffUtil calculations are being made. You could use synchronization blocks to
     * ensure this, use an implementation of an immutable list, or you can make a deepCopy() of
     * the lists to sever the connection between the lists you pass to this function and the calling
     * code.
     *
     *
     *
     * Another common issue is to create the new list based on the old list (with changes) and
     * accidentally change the old list in the process, so that DiffUtil sees two lists which are
     * identical (2 identical new lists, instead of an old list and a new list). This will result
     * in no changes being animated in the adapter. You can fix this by making a deepCopy of the
     * old list before making changes to it. Architectures using immutable view states have often
     * already made a deep copy of their list data by the time it reaches the UI, so you might not
     * encounter this issue in that case.
     *
     *
     *
     * Once these lists start to get larger than about 1000 rows, DiffUtil based methods can get
     * a bit slow. For faster performance consider using the mutable package classes if it's an
     * option for your situation (mostly if you are using MVI or some other form of immutable
     * list data, you will be restricted to using DiffUtil based methods to determine list changes)
     *
     *
     *
     * @param oldList the list about to be replaced
     * @param newList the new list
     * @return the DiffResult for the two lists
     */
    fun createDiffResult(oldList: List<T>, newList: List<T>): DiffResult {
        return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] != null && oldList[oldItemPosition]!!.itemsTheSame(
                    newList[newItemPosition]
                )
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] != null && oldList[oldItemPosition]!!.itemsLookTheSame(
                    newList[newItemPosition]
                )
            }
        })
    }
}
