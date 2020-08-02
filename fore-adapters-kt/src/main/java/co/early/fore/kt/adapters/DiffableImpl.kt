package co.early.fore.kt.adapters

import androidx.core.util.Pair
import co.early.fore.adapters.Diffable
import co.early.fore.adapters.DiffSpec
import co.early.fore.adapters.DiffCalculator
import co.early.fore.adapters.DiffComparator
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.coroutine.withContextDefault
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.observer.ObservableImp

class DiffableImpl<T>(
        private val systemTimeWrapper: SystemTimeWrapper,
        private val workMode: WorkMode,
        private val logger: Logger?
) : Diffable, Observable by ObservableImp(workMode, logger)
        where T : DeepCopyable<T>, T : DiffComparator<T> {

    private var latestDiffSpec: DiffSpec? = createFullDiffSpec()
    private var currentList = listOf<T>()

    fun getItem(index: Int): T {
        return currentList[index]
    }

    fun getListCopy(): MutableList<T> {
        val listCopy = currentList.map { it.deepCopy() }
        return listCopy.toMutableList()
    }

    fun size(): Int = currentList.size

    fun updateList(newList: List<T>) {

        launchMain(workMode) {

            val result = withContextDefault(workMode) {

                // work out the differences in the lists
                val diffResult = DiffCalculator<T>().createDiffResult(currentList, newList)

                //return to the UI thread
                Pair(newList, DiffSpec(diffResult, systemTimeWrapper))
            }

            currentList = result.first ?: emptyList()
            latestDiffSpec = result.second ?: createFullDiffSpec()
            logger?.i("list updated")
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

        return if (systemTimeWrapper.currentTimeMillis() - latestDiffSpecAvailable!!.timeStamp < maxAgeMs) {
            latestDiffSpecAvailable
        } else {
            fullDiffSpec
        }
    }

    private fun createFullDiffSpec(): DiffSpec {
        return DiffSpec(null, systemTimeWrapper)
    }
}
