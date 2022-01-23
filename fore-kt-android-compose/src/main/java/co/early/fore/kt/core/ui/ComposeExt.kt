package co.early.fore.kt.core.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.Fore

/**
 * Observes this [Observable], representing its state via [State]
 *
 * This function uses a [DisposableEffect] to add and remove the an [Observer] in line
 * with the composable being added/entered or removed/exited (as [Flow.collectAsState] does)
 * AND ALSO takes in to account the lifecycle of the activity or fragment containing the
 * composable (as [LiveData.observeAsState] does).
 *
 * [State] will be added as an observer when BOTH: the composable is entered AND the containing
 * lifecycle is STARTED. If either or both conditions are false, the observer is removed (and
 * will be added again as appropriate when conditions change).
 *
 * If you want to see exactly what is happening in your UI, you can pass a logString in to this function,
 * and then set a debug logger as usual:
 * ```
 *   if (BuildConfig.DEBUG) {
 *       Fore.setDelegate(DebugDelegateDefault("foo_"))
 *   }
 * ```
 * Filter your logs on "foo_" and to Debug level
 *
 * There is a sample app that you can use to investigate this behaviour (and the behaviour
 * of [Flow.collectAsState] and [LiveData.observeAsState] here: https://github.com/erdo/compose-observe-as-state-explorer)
 */
@Composable
fun <T> Observable.observeAsState(
    logString: String? = null,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    getState: () -> T
): State<T> {
    return (this as ObservableGroup).observeAsState(logString, policy, getState)
}

/**
 * See #Observable.observeAsState()
 */
@Composable
fun <T> ObservableGroup.observeAsState(
    logLabel: String? = null,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    getState: () -> T
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(getState(), policy) }
    val observer = Observer { state.value = getState() }
    var refCount = 0

    logLabel?.let {
        logMessage(logLabel, "", refCount)
    }

    val lifeCycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            logLabel?.let {
                logMessage(logLabel, "[LIFECYCLE START]", refCount)
            }
            addIfThresholdMet(logLabel, this@observeAsState, observer, ++refCount)
        }

        override fun onStop(owner: LifecycleOwner) {
            logLabel?.let {
                logMessage(logLabel, "[LIFECYCLE STOP]", refCount)
            }
            removeIfThresholdSkirted(logLabel, this@observeAsState, observer, --refCount)
        }
    }

    DisposableEffect(this, lifecycleOwner) {
        logLabel?.let {
            logMessage(logLabel, "[DISPOSABLE EFFECT]", refCount)
            logMessage(logLabel, "[ADD LIFECYCLE OBSERVER]", refCount)
        }
        lifecycleOwner.lifecycle.addObserver(lifeCycleObserver)
        addIfThresholdMet(logLabel, this@observeAsState, observer, ++refCount)
        onDispose {
            logLabel?.let {
                logMessage(logLabel, "[ON DISPOSE]", refCount)
            }
            removeIfThresholdSkirted(logLabel, this@observeAsState, observer, --refCount)
            logLabel?.let {
                logMessage(logLabel, "[REMOVE LIFECYCLE OBSERVER]", refCount)
            }
            lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
        }
    }
    return state
}

private fun addIfThresholdMet(logLabel: String?, observable: ObservableGroup, observer: Observer, count: Int, threshold: Int = 2){
    if (count == threshold) {
        logLabel?.let {
            logMessage(logLabel, "[ADD FORE OBSERVER]", count)
        }
        observable.addObserver(observer)
        observer.somethingChanged()
    }
}

private fun removeIfThresholdSkirted(logLabel: String?, observable: ObservableGroup, observer: Observer, count: Int, threshold: Int = 2){
    if (count == (threshold - 1)) {
        logLabel?.let {
            logMessage(logLabel, "[REMOVE FORE OBSERVER]", count)
        }
        observable.removeObserver(observer)
    }
}

private fun logMessage(label: String, msg: String, refCount: Int){
    Fore.getLogger().d("observeAsState $label $msg refCount:${refCount}")
}
