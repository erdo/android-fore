package co.early.fore.core.testhelpers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.early.fore.core.observer.Observable;


/**
 * Removing countdownlatch boiler plate from tests which are focused on Room db updates
 */
public class CountDownLatchWrapper {

    public static final int DEFAULT_INVALIDATION_TRACKER_TIMEOUT_SECONDS = 2;
    private static int timeoutInSeconds = DEFAULT_INVALIDATION_TRACKER_TIMEOUT_SECONDS;

    /**
     * @param updatesExpected how many changes to the db you are making in this batch - and therefore
     *                          how many invalidation trackers you expect to be triggered from the db
     * @param observableModel observable model that is responding to a Room db invalidation tracker
     * @param surroundByCountdownLatch code to be surrounded by the countdown latch
     */
    public static void runInBatch(int updatesExpected, Observable observableModel, SurroundByCountdownLatch surroundByCountdownLatch){

        // the Room invalidation tracker fires in a different thread (we only get updated by our observers
        // once the in memory list is updated, but its a reasonable proxy for invalidation tracker updates)
        CountDownLatch latchForInvalidationTracker = new CountDownLatch(updatesExpected);
        observableModel.addObserver(() -> latchForInvalidationTracker.countDown());

        surroundByCountdownLatch.run();

        //Try to ensure all the invalidation trackers have been fired before we continue.
        //In reality, Room batches up the invalidation trackers so we can't be deterministic
        //about how many we will receive, hence the timeout
        try {
            latchForInvalidationTracker.await(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void overrideInvalidationTrackerTimeout(int timeoutInSeconds){
        CountDownLatchWrapper.timeoutInSeconds = timeoutInSeconds;
    }

    public interface SurroundByCountdownLatch{
        void run();
    }
}
