package foo.bar.example.forecoroutine.feature.counter

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test


/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class CounterWithProgressTest {

    @Before
    fun setup() {
        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        ForeDelegateHolder.setDelegate(TestDelegateDefault())
    }

    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val counterWithProgress = CounterWithProgress(logger)

        //act

        //assert
        Assert.assertEquals(false, counterWithProgress.isBusy)
        Assert.assertEquals(0, counterWithProgress.progress.toLong())
        Assert.assertEquals(0, counterWithProgress.count.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun increasesBy20() {

        //arrange
        val counterWithProgress = CounterWithProgress(logger)

        //act
        counterWithProgress.increaseBy20()

        //assert
        Assert.assertEquals(false, counterWithProgress.isBusy)
        Assert.assertEquals(0, counterWithProgress.progress.toLong())
        Assert.assertEquals(20, counterWithProgress.count.toLong())
    }


    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the databinding readme for more information about this
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnce() {

        //arrange
        val counterWithProgress = CounterWithProgress(logger)
        val mockObserver = mockk<Observer>(relaxed = true)
        counterWithProgress.addObserver(mockObserver)

        //act
        counterWithProgress.increaseBy20()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    /**
     * Not so easy to test. We'll skip using verify for this test and do it manually.
     * We will test that the progress is:
     *
     * - published 20 times during one call to increaseBy20()
     * - never decreases from the last progress value (except when it gets set back to 0)
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun progressIsPublished() {

        //arrange
        val counterWithProgress = CounterWithProgress(logger)
        val pt = ProgressTracker()
        counterWithProgress.addObserver {
            val latestProgress = counterWithProgress.progress
            if (latestProgress != 0) {//or we just ignore it
                Assert.assertEquals(true, latestProgress >= pt.latestProgress)//never want progress to go down
                if (latestProgress > pt.latestProgress) {//if progress ticks up, then we count it as a progress publication
                    pt.latestProgress = latestProgress
                    pt.numberOfProgressPublications++
                }
            }
        }

        //act
        counterWithProgress.increaseBy20()

        //assert
        Assert.assertEquals(20, pt.numberOfProgressPublications.toLong())
    }


    private inner class ProgressTracker {
        var numberOfProgressPublications = 0
        var latestProgress = 0
    }

    companion object {
        private val logger = SystemLogger()
    }

}
