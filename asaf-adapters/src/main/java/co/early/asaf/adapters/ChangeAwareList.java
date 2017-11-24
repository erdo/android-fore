package co.early.asaf.adapters;

import java.util.List;

/**
 *
 */
public interface ChangeAwareList<E> extends List<E> {

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
    void makeAwareOfDataChange(int rowIndex);

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
    public void makeAwareOfDataChange(int rowStartIndex, int rowsAffected);

}
