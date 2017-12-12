package co.early.asaf.ui;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.ui.SyncableView;

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
 * </p>
 */
public class SyncTrigger {


    private final DoThisWhenTriggered doThisWhenTriggered;
    private final CheckTriggerThreshold checkTriggerThreshold;

    private boolean overThreshold = false;
    private boolean firstCheck = true;


    public SyncTrigger(DoThisWhenTriggered doThisWhenTriggered, CheckTriggerThreshold checkTriggerThreshold) {
        this.doThisWhenTriggered = Affirm.notNull(doThisWhenTriggered);
        this.checkTriggerThreshold = Affirm.notNull(checkTriggerThreshold);
    }

    /**
     *
     * @param swallowTriggerForFirstCheck true to hide triggers on the first check - depending on your
     *                         application this maybe useful to prevent triggers firing due
     *                         to a screen rotation.
     */
    public void check(boolean swallowTriggerForFirstCheck) {

        boolean reached = checkTriggerThreshold.checkThreshold();

        if (overThreshold != reached) { //change of state

            if(!(swallowTriggerForFirstCheck && firstCheck) && reached) {//not ignoring the first check AND threshold has been reached
                fireTrigger();
            }

            overThreshold = reached;
        }

        firstCheck = false;
    }

    private void fireTrigger(){
        doThisWhenTriggered.triggered();
    }

    public interface CheckTriggerThreshold {
        /**
         *
         * @return true if the threshold has been reached or requirement is met, false if not
         */
        boolean checkThreshold();
    }

    public interface DoThisWhenTriggered {
        void triggered();
    }
}
