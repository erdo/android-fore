package co.early.fore.kt.core.observer

import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.Observer

class ObservableGroupImp(vararg observablesList: Observable) : ObservableGroup {

    private val observablesList: List<Observable> = listOf(*observablesList)

    override fun addObserver(observer: Observer) {
        for (observable in observablesList) {
            observable.addObserver(observer)
        }
    }

    override fun removeObserver(observer: Observer) {
        for (observable in observablesList) {
            observable.removeObserver(observer)
        }
    }
}