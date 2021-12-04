package co.early.fore.kt.core.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observer

@Composable
fun <T> Observable.observeAsState(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    getState: () -> T
): State<T> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state = remember { mutableStateOf(getState(), policy) }
    val observer = Observer { state.value = getState() }
    lifecycle.addObserver(LifecycleObserver(observer,this))
    return state
}

@Composable
fun <T> ObservableGroup.observeAsState(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
    getState: () -> T
): State<T> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state = remember { mutableStateOf(getState(), policy) }
    val observer = Observer { state.value = getState() }
    lifecycle.addObserver(LifecycleObserver(observer, this))
    return state
}