package co.early.fore.kt.core.ui

import androidx.lifecycle.*
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.observer.ObservableGroupImp

class ForeLifecycleObserver(
    private val observer: Observer,
    private val observableGroup: ObservableGroup
) : DefaultLifecycleObserver {

    constructor(
        syncableView: SyncableView,
        vararg observablesList: Observable
    ) : this(Observer { syncableView.syncView() }, ObservableGroupImp(*observablesList))

    constructor(
        syncableView: SyncableView,
        observableGroup: ObservableGroup
    ) : this(Observer { syncableView.syncView() }, observableGroup)

    constructor(
        observer: Observer,
        vararg observablesList: Observable
    ) : this(observer, ObservableGroupImp(*observablesList))

    override fun onStart(owner: LifecycleOwner) {
        observableGroup.addObserver(observer)
        observer.somethingChanged()
    }

    override fun onStop(owner: LifecycleOwner) {
        observableGroup.removeObserver(observer)
    }
}
