package co.early.fore.adapters.immutable

interface DeepCopyable<T> {
    fun deepCopy(): T
}
