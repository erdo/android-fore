package co.early.fore.adapters.immutable;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffCalculator<T extends DiffComparator<T>> {

    /**
     * A wrapper for Android's DiffUtil. It's recommended that this method is run off the UI thread
     * because it can take some time to generate the DiffResult, especially for large lists or lists
     * with many changes.
     *
     * <p>
     * It's very important that the contents of the two lists are not changed from another thread
     * while the DiffUtil calculations are being made. You could use synchronization blocks to
     * ensure this, use an implementation of an immutable list, or you can make a deepCopy() of
     * the lists to severe the connection between the lists you pass to this function and the calling
     * code.
     *
     * <p>
     * Another common issue is to create the new list based on the old list (with changes) and
     * accidentally change the old list in the process, so that DiffUtil sees two lists which are
     * identical (2 identical new lists, instead of an old list and a new list). This will result
     * in no changes being animated in the adapter. You can fix this by making a deepCopy of the
     * old list before making changes to it. Architectures using immutable view states have often
     * already made a deep copy of their list data by the time it reaches the UI, so you might not
     * encounter this issue in that case.
     *
     * <p>
     * Once these lists start to get larger than about 1000 rows, DiffUtil based methods can get
     * a bit slow. For faster performance consider using the mutable package classes if it's an
     * option for your situation (mostly if you are using MVI or some other form of immutable
     * list data, you will be restricted to using DiffUtil based methods to determine list changes)
     *
     * <p>
     * @param oldList the list about to be replaced
     * @param newList the new list
     * @return the DiffResult for the two lists
     */
    public DiffUtil.DiffResult createDiffResult(List<T> oldList, List<T> newList) {

        return DiffUtil.calculateDiff(new DiffUtil.Callback() {

            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition) != null && oldList.get(oldItemPosition).itemsTheSame(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition) != null && oldList.get(oldItemPosition).itemsLookTheSame(newList.get(newItemPosition));
            }
        });
    }

}
