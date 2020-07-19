package co.early.fore.kt.adapters

import co.early.fore.adapters.DiffComparator

interface DiffComparatorCopyable<T> : DiffComparator<T> {
    fun copy(): T
}
