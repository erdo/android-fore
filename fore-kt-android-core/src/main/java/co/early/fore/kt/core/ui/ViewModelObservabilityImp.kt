package co.early.fore.kt.core.ui

import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.observer.ObservableGroupImp
import co.early.fore.kt.core.observer.ObservableImp

/**
 * Convenience class to use with ViewModels which will enable a viewModel to be
 *
 * 1) Observable (by a fragment or activity)
 * 2) Observe other Observable models (usually from the domain layer).
 *
 * When used with #ForeLifecycleObserver, both observers (that's the one that connects the
 * fragment/activity/UI to the viewModel, and the one that connects the ViewModel to the
 * other Domain models) will be added and removed in line with the onStart and onStop
 * lifecycle methods.
 *
 */
class ViewModelObservabilityImp(
    vararg observablesList: Observable
) : ViewModelObservability {

    private val uiToVmObservable = ObservableImp()

    private val vmToDomainObservable: ObservableGroup = ObservableGroupImp(*observablesList)
    private lateinit var vmToDomainObserver: Observer

    /**
     * this typically refers to the ViewModel (which should implement SyncableView)
     */
    override fun initSyncableView(viewModel: SyncableView) {
        vmToDomainObserver = Observer { viewModel.syncView() }
        viewModel.syncView()
    }

    /**
     * @param uiToVmObserver this will typically be a fragment or activity observer which is observing the view model
     */
    override fun addObserver(uiToVmObserver: Observer) {
        verifyInitialization()
        uiToVmObservable.addObserver(uiToVmObserver)
        vmToDomainObservable.addObserver(vmToDomainObserver)
    }

    /**
     * @param uiToVmObserver this will typically be a fragment or activity observer which is observing the view model
     */
    override fun removeObserver(uiToVmObserver: Observer) {
        verifyInitialization()
        uiToVmObservable.removeObserver(uiToVmObserver)
        vmToDomainObservable.removeObserver(vmToDomainObserver)
    }

    override fun notifyObservers() {
        verifyInitialization()
        uiToVmObservable.notifyObservers()
    }

    override fun hasObservers(): Boolean {
        verifyInitialization()
        return uiToVmObservable.hasObservers()
    }

    private fun verifyInitialization() {
        if (!this::vmToDomainObserver.isInitialized){
            throw RuntimeException(
                    "\nYou must call initSyncableView() after construction (before adding or removing\n" +
                    "observers or using ViewModelObservabilityImp in any way), typically you would do\n" +
                    "this from your viewModel (which also needs to implement SyncableView):\n" +
                    "\n" +
                    "    init {\n" +
                    "        initSyncableView(this)\n" +
                    "    }\n")
        }
    }
}
