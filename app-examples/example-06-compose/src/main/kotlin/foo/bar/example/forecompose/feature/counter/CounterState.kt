package foo.bar.example.forecompose.feature.counter

import kotlinx.serialization.*

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 */

const val COUNTER_MAX_AMOUNT = 9

@Serializable
data class CounterState(
    val amount: Int = 5,
    @Transient
    val loading: Boolean = false,
) {
    fun canIncrease(): Boolean = !loading && amount < COUNTER_MAX_AMOUNT
    fun canDecrease(): Boolean = !loading && amount > 0
}
