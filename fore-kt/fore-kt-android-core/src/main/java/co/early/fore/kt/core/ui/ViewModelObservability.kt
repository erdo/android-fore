package co.early.fore.kt.core.ui

import co.early.fore.core.observer.Observable
import co.early.fore.core.ui.SyncableView

interface ViewModelObservability : Observable {
    fun initSyncableView(viewModel: SyncableView)
}
