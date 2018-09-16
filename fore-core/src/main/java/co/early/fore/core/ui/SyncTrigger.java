package co.early.fore.core.ui;

import co.early.fore.core.Affirm;

/**
 * <p>
 *      Convenience class used to fire one-off events from within the {@link SyncableView#syncView()}
 *      method of a view.
 * </p><p>
 *      {@link DoThisWhenTriggered#triggered()} is called once the first time that
 *      {@link CheckTriggerThreshold#checkThreshold()} returns true when checked.
 * </p><p>
 *      If {@link CheckTriggerThreshold#checkThreshold()} later
 *      returns false when checked, then the trigger is reset and will fire again if
 *      {@link CheckTriggerThreshold#checkThreshold()} once again returns true.
 * </p><p>
 *      To configure this trigger to immediately reset itself after each time it's fired without
 *      having to wait for {@link CheckTriggerThreshold#checkThreshold()} to return false
 *      construct this class with the ResetRule.IMMEDIATELY flag
 * </p>
 */
public class SyncTrigger {

    private final DoThisWhenTriggered doThisWhenTriggered;
    private final CheckTriggerThreshold checkTriggerThreshold;
    private final ResetRule resetRule;

    private boolean overThreshold = false;
    private boolean firstCheck = true;

    // There is a reason we don't support ResetRule.NEVER here. SyncTriggers usually live in Views,
    // and are destroyed and recreated on device rotation along with the View. We have no way of
    // supporting ResetRule.NEVER across different SyncTrigger instances without getting involved
    // in persistence or Activity methods.
    //
    // If the caller needs a trigger that once fired is never reset despite being rotated, this has
    // to be done in the implementation of the checkThreshold() method - probably with reference to
    // a long living model that knows if the trigger has been fired once already.
    public enum ResetRule{
        /*
            Trigger is reset after each successful check
         */
        IMMEDIATELY,
        /*
            Trigger is only reset after a successful check, once a subsequent check fails.
            This is the default.
         */
        ONLY_AFTER_REVERSION
    }


    public SyncTrigger(DoThisWhenTriggered doThisWhenTriggered, CheckTriggerThreshold checkTriggerThreshold) {
        this(doThisWhenTriggered, checkTriggerThreshold, ResetRule.ONLY_AFTER_REVERSION);
    }

    public SyncTrigger(DoThisWhenTriggered doThisWhenTriggered, CheckTriggerThreshold checkTriggerThreshold, ResetRule resetRule) {
        this.doThisWhenTriggered = Affirm.notNull(doThisWhenTriggered);
        this.checkTriggerThreshold = Affirm.notNull(checkTriggerThreshold);
        this.resetRule = Affirm.notNull(resetRule);
    }


    /**
     * <p>
     * If you are using this in a view that supports rotation, you might want to look at
     * {@link #checkLazy()}
     * </p><p>
     * SyncTriggers usually exist in the the view layer and as such they are completely
     * reconstructed each time a view is rotated (on Android).
     * </p><p>
     * This means that if your trigger threshold is met (causing the trigger to be fired) and then
     * you rotate the screen - the first check on the newly constructed syncTrigger will also cause
     * the trigger to be fired, continually rotating the screen will result in continual trigger
     * firing.
     * </p><p>
     * This is probably not what you want, so you can use checkLazy() instead which swallows
     * the first trigger IF and only if it occurs on the FIRST EVER check of the trigger
     * threshold for this syncTrigger.
     * </p>
     */
    public void check(){
        check(false);
    }


    /**
     * <p>
     * A trigger check (similar to {@link #check()} but which swallows the first trigger IF and
     * only if it occurs on the FIRST EVER check of the trigger threshold for this syncTrigger.
     * </p><p>
     * SyncTriggers usually exist in the the view layer and as such they are completely
     * reconstructed each time a view is rotated (on Android).
     * </p><p>
     * This means that if your trigger threshold is met (causing the trigger to be fired) and then
     * you rotate the screen - the first check on the newly constructed syncTrigger will also cause
     * the trigger to be fired, continually rotating the screen will result in continual trigger
     * firing.
     * </p><p>
     * For this reason, you might want to use this method for your application
     * </p>
     */
    public void checkLazy(){
        check(true);
    }


    /**
     *
     * @param swallowTriggerForFirstCheck true to swallow triggers on the first check - depending
     *                                    on your application this maybe useful to prevent triggers
     *                                    firing due to a screen rotation.
     */
    private void check(boolean swallowTriggerForFirstCheck) {

        boolean reached = checkTriggerThreshold.checkThreshold();

        if (overThreshold != reached) { //change of state

            if(!(swallowTriggerForFirstCheck && firstCheck) && reached) {//not ignoring the first check AND threshold has been reached
                fireTrigger();
            }

            overThreshold = reached;
        }

        if (resetRule == ResetRule.IMMEDIATELY){
            overThreshold = false;
        }

        firstCheck = false;
    }

    private void fireTrigger(){
        doThisWhenTriggered.triggered();
    }

    public interface CheckTriggerThreshold {
        /**
         *
         * @return true if the threshold has been reached or requirement is met. false if not
         */
        boolean checkThreshold();
    }

    public interface DoThisWhenTriggered {
        void triggered();
    }
}
