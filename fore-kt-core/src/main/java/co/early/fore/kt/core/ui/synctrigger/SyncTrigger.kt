package co.early.fore.kt.core.ui.synctrigger

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
 */
class SyncTrigger(
    private val triggeredWhen: () -> Boolean,
    private val doThisWhenTriggered: () -> Unit
) {
    private var resetRule: ResetRule = ResetRule.ONLY_AFTER_REVERSION
    private var overThreshold = false
    private var firstCheck = true

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

    fun resetRule(resetRule: ResetRule): SyncTrigger{
        this.resetRule = resetRule
        return this
    }

    fun getResetRule(): ResetRule {
        return resetRule
    }

    /**
     *
     * @param swallowTriggerForFirstCheck true to swallow triggers on the first check - depending
     * on your application this maybe useful to prevent triggers
     * firing due to a screen rotation.
     */
    private fun check(swallowTriggerForFirstCheck: Boolean) {
        val reached = triggeredWhen()
        var fireTrigger = false
        if (!overThreshold && reached) {
            overThreshold = true
            if (!(swallowTriggerForFirstCheck && firstCheck)) { //not ignoring the first check AND threshold has been reached
                fireTrigger = true
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
        if (fireTrigger){
            doThisWhenTriggered()
        }
    }
}