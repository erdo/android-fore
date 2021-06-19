package co.early.fore.kt.core.ui

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
typealias SyncTrigger = SyncTriggerKeeper<Unit>