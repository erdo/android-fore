package foo.bar.example.forecoroutine.ui

import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import foo.bar.example.forecoroutine.App
import foo.bar.example.forecoroutine.OG
import foo.bar.example.forecoroutine.feature.counter.Counter
import foo.bar.example.forecoroutine.feature.counter.CounterWithProgress
import io.mockk.every


/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class StateBuilder internal constructor(private val mockCounter: Counter, private val mockCounterWithProgress: CounterWithProgress) {

    internal fun counterBasicIsBusy(busy: Boolean): StateBuilder {
        every {
            mockCounter.isBusy
        } returns busy
        return this
    }

    internal fun counterWithProgressIsBusy(busy: Boolean): StateBuilder {
        every {
            mockCounterWithProgress.isBusy
        } returns busy
        return this
    }

    internal fun counterBasicCount(count: Int): StateBuilder {
        every {
            mockCounter.count
        } returns count
        return this
    }

    internal fun counterWithProgressCount(count: Int): StateBuilder {
        every {
            mockCounterWithProgress.count
        } returns count
        return this
    }

    internal fun counterWithProgressProgressValue(value: Int): StateBuilder {
        every {
            mockCounterWithProgress.progress
        } returns value
        return this
    }

    internal fun createRule(): ActivityTestRule<CounterActivity> {

        return object : ActivityTestRule<CounterActivity>(CounterActivity::class.java) {
            override fun beforeActivityLaunched() {

                //get hold of the application
                val customApp = InstrumentationRegistry.getTargetContext().applicationContext as App
                // customApp.injectSynchronousObjectGraph()

                //inject our mocks so our UI layer will pick them up
                OG.putMock(Counter::class.java, mockCounter)
                OG.putMock(CounterWithProgress::class.java, mockCounterWithProgress)
            }

        }
    }

}
