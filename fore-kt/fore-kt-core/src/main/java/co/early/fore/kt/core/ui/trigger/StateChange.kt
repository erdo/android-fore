package co.early.fore.kt.core.ui.trigger

data class StateChange<out T>(
    val pre: T?,
    val now: T
) {
    override fun toString(): String = "PRE:$pre -> NOW:$now"
}

infix fun <T> T?.to(that: T): StateChange<T> = StateChange(this, that)
