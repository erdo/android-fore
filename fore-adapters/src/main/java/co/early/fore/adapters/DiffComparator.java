package co.early.fore.adapters;

import android.support.v7.util.DiffUtil;

/**
 *
 */
public interface DiffComparator<T> {

    /**
     * Used by {@link DiffUtil}
     *
     * Do the two instances represent the same real world item? even though they maybe
     * different instances. For example, one could be its representation in a list view, the other
     * could be its representation in a database entity, but if they represent the same conceptual
     * item then this method should return true
     *
     * @param other the instance to compare this with
     * @return true if the items represent the same real world / conceptual item
     */
    boolean itemsTheSame(T other);

    /**
     * Used by {@link DiffUtil}
     *
     * Note this really means do they look the same in a list on the display. As such
     * there can be multiple implementations, each related to a particular list display
     *
     * @param other the instance to compare this with
     * @return true if the items will look the same when displayed
     */
    boolean contentsTheSame(T other);
}
