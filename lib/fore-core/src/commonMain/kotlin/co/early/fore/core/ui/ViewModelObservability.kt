package co.early.fore.core.ui

import co.early.fore.core.observer.Observable

interface ViewModelObservability : Observable {
    fun initSyncableView(viewModel: SyncableView)
}
