package co.early.fore.adapters.mutable

interface ChangeAwareList<E> : MutableList<E>, Updateable {
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
    fun makeAwareOfDataChange(rowIndex: Int)

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
    fun makeAwareOfDataChange(rowStartIndex: Int, rowsAffected: Int)

    /**
     * Removes from this list all of the elements whose index is between
     * `fromIndex`, inclusive, and `toIndex`, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by `(toIndex - fromIndex)` elements.
     * (If `toIndex==fromIndex`, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed
     * @param toIndex index after last element to be removed
     */
    fun removeRange(fromIndex: Int, toIndex: Int)
}
