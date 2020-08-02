package co.early.fore.kt.adapters

interface DeepCopyable<T> {
    fun deepCopy(): T
}