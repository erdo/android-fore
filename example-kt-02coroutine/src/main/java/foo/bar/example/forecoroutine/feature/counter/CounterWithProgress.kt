package foo.bar.example.forecoroutine.feature.counter


import co.early.fore.core.WorkMode
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.coroutine.withContextDefault
import co.early.fore.kt.core.coroutine.withContextMain
import co.early.fore.kt.core.observer.ObservableImp
import kotlinx.coroutines.delay

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class CounterWithProgress(
        private val workMode: WorkMode,
        private val logger: Logger
) : Observable by ObservableImp(workMode, logger) {

    var isBusy = false
        private set
    var count: Int = 0
        private set
    var progress: Int = 0
        private set


    fun increaseBy20() {

        logger.i(LOG_TAG, "increaseBy20() t:" + Thread.currentThread())

        if (isBusy) {
            return
        }

        isBusy = true
        notifyObservers()

        launchMain(workMode) {

            val result = withContextDefault(workMode) {
                doStuffInBackground(20)
            }

            doThingsWithTheResult(result)
        }
    }


    private suspend fun doStuffInBackground(countTo: Int): Int {

        logger.i(LOG_TAG, "doStuffInBackground() t:" + Thread.currentThread())

        var totalIncrease = 0

        for (ii in 1..countTo) {

            delay((if (workMode == WorkMode.SYNCHRONOUS) 1 else 100).toLong())

            ++totalIncrease

            withContextMain(workMode) {
                publishProgress(totalIncrease)
            }

            logger.i(LOG_TAG, " ------tick------ t:" + Thread.currentThread())
        }

        return totalIncrease
    }


    private fun publishProgress(value: Int) {

        logger.i(LOG_TAG, "publishProgress() t:" + Thread.currentThread())

        progress = value
        notifyObservers()
    }


    private fun doThingsWithTheResult(result: Int) {

        logger.i(LOG_TAG, "doThingsWithTheResult() t:" + Thread.currentThread())

        count += result
        progress = 0
        isBusy = false
        notifyObservers()
    }

    companion object {
        private val LOG_TAG = CounterWithProgress::class.java.simpleName
    }
}
