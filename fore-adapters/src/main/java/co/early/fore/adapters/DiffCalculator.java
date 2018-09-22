package co.early.fore.adapters;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 *
 */
public class DiffCalculator<T extends DiffComparator> {

    /**
     * It's the callers' responsibility to ensure that these lists and the items within them do not
     * change while the diff is being calculated. If you are calling this method on a separate
     * thread (recommended) then you need to ensure that no other thread will be changing the lists
     * using synchronization blocks.
     * <p>
     * Once these lists start to get larger than about 1000 rows, this method becomes unusable
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
                return oldList.get(oldItemPosition) != null && oldList.get(oldItemPosition).contentsTheSame(newList.get(newItemPosition));
            }
        });
    }

}
