package co.early.fore.core.observer

interface ObservableGroup {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
}