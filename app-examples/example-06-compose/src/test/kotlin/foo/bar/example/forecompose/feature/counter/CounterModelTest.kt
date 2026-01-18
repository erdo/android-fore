package foo.bar.example.forecompose.feature.counter

import co.early.fore.core.delegate.Fore
import co.early.fore.core.delegate.runWithTestDelegate
import co.early.fore.core.observer.Observer
import co.early.persista.PerSista
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import okio.Path.Companion.toOkioPath
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterModelTest {

    @MockK
    private lateinit var mockObserver: Observer

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun cleanup() {
    }

    @Test
    fun initialConditions() = runWithTestDelegate {

        //arrange
        val counterModel = createCounterModel()

        advanceUntilIdle()

        //act

        //assert
        Assert.assertEquals(true, counterModel.state.canIncrease())
        Assert.assertEquals(false, counterModel.state.canDecrease())
        Assert.assertEquals(0, counterModel.state.amount)
    }

    @Test
    fun increaseCounter() = runWithTestDelegate {

        //arrange
        val counterModel = createCounterModel()
        advanceUntilIdle()
        // alternatively: counterModel.waitUntil { counterModel.state.loading }

        //act
        counterModel.increase()
        advanceUntilIdle()

        //assert
        Assert.assertEquals(true, counterModel.state.canIncrease())
        Assert.assertEquals(true, counterModel.state.canDecrease())
        Assert.assertEquals(1, counterModel.state.amount)
    }

    @Test
    fun decreaseCounter() = runWithTestDelegate {

        //arrange
        val counterModel = createCounterModel()
        counterModel.increase()

        //act
        counterModel.decrease()
        advanceUntilIdle()

        //assert
        Assert.assertEquals(true, counterModel.state.canIncrease())
        Assert.assertEquals(false, counterModel.state.canDecrease())
        Assert.assertEquals(0, counterModel.state.amount)
    }

    @Test
    fun canIncreaseIsFalseAtLimit() = runWithTestDelegate {

        //arrange
        val counterModel = createCounterModel()
        advanceUntilIdle()

        //act
        repeat (COUNTER_MAX_AMOUNT) {
            counterModel.increase()
            advanceUntilIdle()
        }

        //assert
        Assert.assertEquals(false, counterModel.state.canIncrease())
        Assert.assertEquals(true, counterModel.state.canDecrease())
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
     * See the reactive ui section of the docs for more information about this
     *
     * @throws Exception
     */
    @Test
    fun observersNotifiedAtLeastOnceForIncrease() = runWithTestDelegate {

        //arrange
        val counterModel = createCounterModel()
        counterModel.addObserver(mockObserver)

        //act
        counterModel.increase()

        advanceUntilIdle()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    @Test
    fun observersNotifiedAtLeastOnceForDecrease() = runWithTestDelegate {

        //arrange
        val counterModel = createCounterModel()
        counterModel.increase()
        counterModel.addObserver(mockObserver)

        //act
        counterModel.decrease()
        advanceUntilIdle()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    private fun createCounterModel(): CounterModel {
        val dataFolder = TemporaryFolder()
        dataFolder.create()
        return CounterModel(
            PerSista(
                dataPath = dataFolder.newFolder().toOkioPath(),
                logger = Fore.getLogger(),
            ),
            Fore.getLogger(),
        )
    }
}
