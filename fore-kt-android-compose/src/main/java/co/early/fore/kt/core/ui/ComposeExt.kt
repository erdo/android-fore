package co.early.fore.kt.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.core.observer.Observer

@Composable
fun <T> Observable.observeAsState(getState: () -> T): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(getState()) }
    val observer = Observer { state.value = getState() }
    lifecycleOwner.lifecycle.addObserver(ForeLifecycleObserver(observer,this))
    return state
}

@Composable
fun <T> ObservableGroup.observeAsState(getState: () -> T): State<T> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state = remember { mutableStateOf(getState()) }
    val observer = Observer { state.value = getState() }
    lifecycle.addObserver(ForeLifecycleObserver(observer, this))
    return state
}