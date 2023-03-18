package foo.bar.example.forecompose.feature.counter

import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import co.early.persista.PerSista
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.Executors

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 */
class CounterModelTest {

    @MockK
    private lateinit var mockObserver: Observer

    private val testDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @Before
    fun setup() {

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        Fore.setDelegate(TestDelegateDefault())

        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val counterModel = createCounterModel()

        //act

        //assert
        Assert.assertEquals(true, counterModel.state.canIncrease())
        Assert.assertEquals(false, counterModel.state.canDecrease())
        Assert.assertEquals(0, counterModel.state.amount)
    }

    @Test
    @Throws(Exception::class)
    fun increaseCounter() {

        //arrange
        val counterModel = createCounterModel()

        //act
        counterModel.increase()

        //assert
        Assert.assertEquals(true, counterModel.state.canIncrease())
        Assert.assertEquals(true, counterModel.state.canDecrease())
        Assert.assertEquals(1, counterModel.state.amount)
    }

    @Test
    @Throws(Exception::class)
    fun decreaseCounter() {

        //arrange
        val counterModel = createCounterModel()
        counterModel.increase()

        //act
        counterModel.decrease()

        //assert
        Assert.assertEquals(true, counterModel.state.canIncrease())
        Assert.assertEquals(false, counterModel.state.canDecrease())
        Assert.assertEquals(0, counterModel.state.amount)
    }

    @Test
    @Throws(Exception::class)
    fun canIncreaseIsFalseAtLimit() {

        //arrange
        val counterModel = createCounterModel()
        for (ii in 0 until COUNTER_MAX_AMOUNT) {
            counterModel.increase()
        }

        //act

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
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncrease() {

        //arrange
        val counterModel = createCounterModel()
        counterModel.addObserver(mockObserver)

        //act
        counterModel.increase()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForDecrease() {

        //arrange
        val counterModel = createCounterModel()
        counterModel.increase()
        counterModel.addObserver(mockObserver)

        //act
        counterModel.decrease()

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
                dataDirectory = dataFolder.newFolder(),
                mainDispatcher = testDispatcher,
                writeReadDispatcher = testDispatcher,
                logger = Fore.getLogger()
            ),
            Fore.getLogger(),
        )
    }
}
