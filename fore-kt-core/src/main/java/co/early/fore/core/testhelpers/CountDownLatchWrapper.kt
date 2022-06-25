package co.early.fore.core.testhelpers

import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Removing countdownlatch boiler plate from tests which require latches
 */
object CountDownLatchWrapper {
    const val DEFAULT_TIMEOUT_SECONDS = 2
    private var timeoutInSeconds = DEFAULT_TIMEOUT_SECONDS

    /**
     * @param updatesExpected how many notifications (countdowns) you expect from the observable
     * @param observableModel observable model to be used for counting down the latch
     * @param surroundByCountdownLatch code to be surrounded by the countdown latch
     */
    fun runInBatch(
        updatesExpected: Int,
        observableModel: Observable,
        surroundByCountdownLatch: () -> Unit
    ) {
        val latch = CountDownLatch(updatesExpected)
        observableModel.addObserver(object : Observer {
            override fun somethingChanged() {
                latch.countDown()
            }
        })
        surroundByCountdownLatch()

        // Try to ensure all the invalidation trackers have been fired before we continue.
        // In reality, there are some things we can't be deterministic about (Room batches up
        // its invalidation trackers for example, so we can't tell how many we will receive)
        // hence the timeout
        try {
            latch.await(timeoutInSeconds.toLong(), TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun overrideLatchTimeout(timeoutInSeconds: Int) {
        CountDownLatchWrapper.timeoutInSeconds = timeoutInSeconds
    }
}