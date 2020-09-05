package co.early.fore.kt.adapters

import co.early.fore.adapters.DiffCalculator
import co.early.fore.adapters.DiffComparator
import co.early.fore.adapters.DiffSpec
import co.early.fore.adapters.Diffable
import co.early.fore.core.WorkMode
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.awaitDefault
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp

class DiffableImp<T>(
        private val systemTimeWrapper: SystemTimeWrapper? = null,
        private val workMode: WorkMode? = null,
        private val logger: Logger? = null
) : Diffable, Observable by ObservableImp(workMode, logger)
        where T : DeepCopyable<T>, T : DiffComparator<T> {

    private var latestDiffSpec: DiffSpec? = createFullDiffSpec()
    private var currentList = listOf<T>()
    private var currentListMutableCopy = mutableListOf<T>()

    fun getItem(index: Int): T {
        return currentList[index]
    }

    fun getListCopy(): MutableList<T> {
        return currentListMutableCopy
    }

    fun size(): Int = currentList.size

    fun updateList(newList: List<T>) {

        launchMain(workMode) {

            val result = awaitDefault(workMode) {

                // work out the differences in the lists
                val diffResult = DiffCalculator<T>().createDiffResult(currentList, newList)

                //create a mutable copy of the new list, ready for when client wants to change it
                val newListCopy = newList.map { it.deepCopy() }

                //return to the UI thread
                Triple(newList, newListCopy.toMutableList(), DiffSpec(diffResult, ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper)))
            }

            currentList = result.first
            currentListMutableCopy = result.second
            latestDiffSpec = result.third
            ForeDelegateHolder.getLogger(logger).i("list updated, thread:" + Thread.currentThread().id)
            notifyObservers()
        }

    }

    /**
     * If the DiffResult is old, then we assume that whatever changes
     * were made to the list last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the DiffResult and create a fresh one with a
     * full diff spec.
     *
     * @return the latest DiffResult for the list
     */
    override fun getAndClearLatestDiffSpec(maxAgeMs: Long): DiffSpec {

        val latestDiffSpecAvailable = latestDiffSpec
        val fullDiffSpec = createFullDiffSpec()

        latestDiffSpec = fullDiffSpec

        return if (ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper).currentTimeMillis() - latestDiffSpecAvailable!!.timeStamp < maxAgeMs) {
            latestDiffSpecAvailable
        } else {
            fullDiffSpec
        }
    }

    private fun createFullDiffSpec(): DiffSpec {
        return DiffSpec(null, ForeDelegateHolder.getSystemTimeWrapper(systemTimeWrapper))
    }
}
