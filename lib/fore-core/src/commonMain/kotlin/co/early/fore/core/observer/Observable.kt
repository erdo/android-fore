package co.early.fore.core.observer

interface Observable : ObservableGroup {
    fun notifyObservers()
    fun hasObservers(): Boolean
}