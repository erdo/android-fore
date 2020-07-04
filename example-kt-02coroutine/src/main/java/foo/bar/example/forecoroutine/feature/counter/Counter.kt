package foo.bar.example.forecoroutine.feature.counter


import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.coroutine.withContextDefault
import co.early.fore.kt.core.observer.ObservableImp
import kotlinx.coroutines.delay

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class Counter(
        private val workMode: WorkMode,
        private val logger: Logger
) : Observable by ObservableImp(workMode, logger) {

    var isBusy = false
        private set
    var count: Int = 0
        private set


    fun increaseBy20() {

        logger.i("increaseBy20() t:" + Thread.currentThread())

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

        logger.i("doStuffInBackground() t:" + Thread.currentThread())

        var totalIncrease = 0

        for (ii in 1..countTo) {

            delay((if (workMode == WorkMode.SYNCHRONOUS) 1 else 100).toLong())

            ++totalIncrease

            logger.i("-tick- t:" + Thread.currentThread())
        }

        return totalIncrease
    }


    private fun doThingsWithTheResult(result: Int) {

        logger.i("doThingsWithTheResult() t:" + Thread.currentThread())

        count += result
        isBusy = false
        notifyObservers()
    }

}
