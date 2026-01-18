package foo.bar.example.forecoroutine.feature.counter

import co.early.fore.core.delegate.runWithTestDelegate
import co.early.fore.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterTest {

    @Before
    fun setup() {

    }

    @Test
    fun initialConditions() = runWithTestDelegate {

        //arrange
        val counter = Counter(logger)

        //act

        //assert
        Assert.assertEquals(false, counter.isBusy)
        Assert.assertEquals(0, counter.count.toLong())
    }

    @Test
    fun increasesBy20() = runWithTestDelegate {
        // arrange
        val counter = Counter(logger)

        // act
        counter.increaseBy20()

        advanceUntilIdle()

        // assert
        assertEquals(false, counter.isBusy)
        assertEquals(20, counter.count.toLong())
    }

    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times the observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the reactive UIs section of the fore docs for more information about this
     *
     * @throws Exception
     */
    @Test
    fun observersNotifiedAtLeastOnce() = runWithTestDelegate {

        //arrange
        val counter = Counter(logger)
        val mockObserver = mockk<Observer>(relaxed = true)
        counter.addObserver(mockObserver)

        //act
        counter.increaseBy20()

        advanceUntilIdle()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    companion object {
        private val logger = SystemLogger()
    }
}
