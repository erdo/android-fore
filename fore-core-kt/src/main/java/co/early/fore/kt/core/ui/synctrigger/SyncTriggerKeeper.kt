package co.early.fore.kt.core.ui.synctrigger

interface Keeper<K> {
    /**
     * swapper provides the previously kept value (or null),
     * and expects the new value to keep in return
     *
     * the swap function should return true if the values have
     * changed (i.e. when the new value is not the same as the
     * previously kept value)
     */
    fun swap(swapper: (K?) -> K) : Boolean
}

/**
 *
 * Convenience class used to fire one-off events from within the [SyncableView.syncView]
 * method of a view.
 *
 * [doThisWhenTriggered] is called once the first time that [triggeredWhen] returns true when checked.
 *
 * If [triggeredWhen] later returns false when checked, then the trigger is reset and will fire again if
 * [triggeredWhen] once again returns true.
 *
 * To configure this trigger to immediately reset itself after each time it's fired without
 * having to wait for [triggeredWhen] to return false,
 * construct this class with the ResetRule.IMMEDIATELY flag
 *
 * So that we can remember temporary states between invocations of triggeredWhen(), triggeredWhen
 * passes a Keeper to the client which can be used to keep temporary values so that they can be
 * read again during the next invocation of triggeredWhen
 *
 */
class SyncTriggerKeeper<T>(
    private val triggeredWhen: (Keeper<T>) -> Boolean,
    private val doThisWhenTriggered: () -> Unit
) {

    private var previousValue: T? = null
    private val keeper = object : Keeper<T> {
        override fun swap(swapper: (T?) -> T): Boolean {
            val valueToKeep = swapper(previousValue)
            val hasChanged = (valueToKeep != previousValue)
            previousValue = valueToKeep
            return hasChanged
        }
    }

    private var resetRule: ResetRule = ResetRule.ONLY_AFTER_REVERSION
    private var overThreshold = false
    private var firstCheck = true

    fun resetRule(resetRule: ResetRule): SyncTriggerKeeper<T> {
        this.resetRule = resetRule
        return this
    }

    fun getResetRule(): ResetRule {
        return resetRule
    }

    /**
     *
     * If you are using this in a view that supports rotation, you might want to look at
     * [.checkLazy]
     *
     * SyncTriggers usually exist in the the view layer and as such they are completely
     * reconstructed each time a view is rotated (on Android).
     *
     * This means that if your trigger threshold is met (causing the trigger to be fired) and then
     * you rotate the screen - the first check on the newly constructed syncTrigger will also cause
     * the trigger to be fired, continually rotating the screen will result in continual trigger
     * firing.
     *
     * This is probably not what you want, so you can use checkLazy() instead which swallows
     * the first trigger IF and only if it occurs on the FIRST EVER check of the trigger
     * threshold for this syncTrigger.
     */
    fun check() {
        check(false)
    }

    /**
     * A trigger check (similar to [.check] but which swallows the first trigger IF and
     * only if it occurs on the FIRST EVER check of the trigger threshold for this syncTrigger.
     *
     * SyncTriggers usually exist in the the view layer and as such they are completely
     * reconstructed each time a view is rotated (on Android).
     *
     * This means that if your trigger threshold is met (causing the trigger to be fired) and then
     * you rotate the screen - the first check on the newly constructed syncTrigger will also cause
     * the trigger to be fired, continually rotating the screen will result in continual trigger
     * firing.
     *
     * For this reason, you might want to use this method for your application
     */
    fun checkLazy() {
        check(true)
    }

    /**
     *
     * @param swallowTriggerForFirstCheck true to swallow triggers on the first check - depending
     * on your application this maybe useful to prevent triggers
     * firing due to a screen rotation.
     */
    private fun check(swallowTriggerForFirstCheck: Boolean) {
        val reached = triggeredWhen(keeper)
        if (!overThreshold && reached) {
            overThreshold = true
            if (!(swallowTriggerForFirstCheck && firstCheck)) { //not ignoring the first check AND threshold has been reached
                fireTrigger()
            }
        }
        firstCheck = false
        when (resetRule) {
            ResetRule.IMMEDIATELY -> overThreshold = false
            ResetRule.ONLY_AFTER_REVERSION -> if (!reached) {
                overThreshold = false
            }
            ResetRule.NEVER -> {
            }
        }
    }

    private fun fireTrigger() {
        doThisWhenTriggered()
    }
}