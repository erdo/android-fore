package co.early.fore.core.testhelpers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.early.fore.core.observer.Observable;


/**
 * Removing countdownlatch boiler plate from tests which require latches
 */
public class CountDownLatchWrapper {

    public static final int DEFAULT_TIMEOUT_SECONDS = 2;
    private static int timeoutInSeconds = DEFAULT_TIMEOUT_SECONDS;

    /**
     * @param updatesExpected how many notifications (countdowns) you expect from the observable
     * @param observableModel observable model to be used for counting down the latch
     * @param surroundByCountdownLatch code to be surrounded by the countdown latch
     */
    public static void runInBatch(int updatesExpected, Observable observableModel, SurroundByCountdownLatch surroundByCountdownLatch){

        CountDownLatch latch = new CountDownLatch(updatesExpected);
        observableModel.addObserver(() -> latch.countDown());

        surroundByCountdownLatch.run();

        // Try to ensure all the invalidation trackers have been fired before we continue.
        // In reality, there are some things we can't be deterministic about (Room batches up
        // its invalidation trackers for example, so we can't tell how many we will receive)
        // hence the timeout
        try {
            latch.await(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void overrideLatchTimeout(int timeoutInSeconds){
        CountDownLatchWrapper.timeoutInSeconds = timeoutInSeconds;
    }

    public interface SurroundByCountdownLatch {
        void run();
    }
}
