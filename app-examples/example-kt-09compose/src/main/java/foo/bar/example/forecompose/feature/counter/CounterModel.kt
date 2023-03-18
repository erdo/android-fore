package foo.bar.example.forecompose.feature.counter

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.observer.ObservableImp
import co.early.persista.PerSista
import kotlinx.coroutines.delay

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 */

class CounterModel(
    private val perSista: PerSista,
    private val logger: Logger,
) : Observable by ObservableImp() {

    var state = CounterState(amount = 0, loading = true)
        private set

    init {
        launchIO{
            delay(1000) // not necessary, it just lets us verify the initial loading spinner
            perSista.read(state) {
                state = it.copy(loading = false)
                notifyObservers()
            }
        }
    }

    fun increase() {
        if (state.canIncrease()) {
            perSista.write(state.copy(amount = state.amount + 1)) {
                logger.i("counter increased to:${it.amount}")
                state = it
                notifyObservers()
            }
        }
    }

    fun decrease() {
        if (state.canDecrease()) {
            perSista.write(state.copy(amount = state.amount - 1)) {
                logger.i("counter decreased to:${it.amount}")
                state = it
                notifyObservers()
            }
        }
    }

    fun reset() {
        perSista.clear(state.javaClass.kotlin){
            logger.i("counter state cleared")
            state = CounterState(0)
            notifyObservers()
        }
    }
}
