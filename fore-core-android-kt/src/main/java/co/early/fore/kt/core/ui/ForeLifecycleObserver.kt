package co.early.fore.kt.core.ui

import androidx.lifecycle.*
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.observer.ObservableGroupImp

class ForeLifecycleObserver(
    private val syncableView: SyncableView,
    vararg observablesList: Observable
) : DefaultLifecycleObserver {

    private val observableGroup: ObservableGroup = ObservableGroupImp(*observablesList)
    private val observer: Observer = Observer { syncableView.syncView() }

    override fun onStart(owner: LifecycleOwner) {
        observableGroup.addObserver(observer)
        observer.somethingChanged()
    }

    override fun onStop(owner: LifecycleOwner) {
        observableGroup.removeObserver(observer)
    }
}
