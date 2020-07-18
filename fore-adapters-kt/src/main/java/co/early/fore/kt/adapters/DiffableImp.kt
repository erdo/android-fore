package co.early.fore.kt.adapters

import co.early.fore.adapters.*
import co.early.fore.core.WorkMode
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.withContextDefault
import co.early.fore.kt.core.logging.Logger

class DiffableImp<T : DiffComparator<T>>(
        private val systemTimeWrapper: SystemTimeWrapper,
        private val workMode: WorkMode,
        private val maxSizeForDiffUtil: Int = 1000,
        private val logger: Logger? = null
) : Diffable {

    private val fullDiffSpec = DiffSpec(null, systemTimeWrapper)
    private var latestDiffSpec = fullDiffSpec

    /**
     * Calculate the DiffSpec between the two lists on Dispatchers.IO, the diffSpec is
     * set, ready for [Diffable.getAndClearLatestDiffSpec()] and is also returned via the
     * callback on Dispatchers.Main
     *
     * The caller should then immediately set the newList and notify the adapter
     */
    suspend fun setDiffSpec(oldList: List<T>, newList: List<T>, callback: (diffSpec: DiffSpec) -> Unit) {

        logger?.d("calculateDiffSpec() - thread.id: ${Thread.currentThread().id}")

        val result = withContextDefault(workMode) {
            if (oldList.size < maxSizeForDiffUtil && newList.size < maxSizeForDiffUtil) {
                logger?.d(" ...calculating DiffSpec between old and new list - thread.id: ${Thread.currentThread().id}")
                DiffSpec(DiffCalculator<T>().createDiffResult(oldList, newList), systemTimeWrapper)
            } else {
                logger?.d(" ...skipping DiffSpec, lists(s) too large - thread.id: ${Thread.currentThread().id}")
                fullDiffSpec
            }
        }

        logger?.d(" ...setting DiffSpec - thread.id: ${Thread.currentThread().id}")
        latestDiffSpec = result
        logger?.d(" ...returning DiffSpec - thread.id: ${Thread.currentThread().id}")
        callback(result)
    }

    /**
     * If the DiffSpec is old, then we assume that whatever changes
     * were made to the list last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the DiffSpec and create a fresh one with a
     * full diff spec.
     *
     * @return the latest DiffSpec for the list
     */
    override fun getAndClearLatestDiffSpec(maxAgeMs: Long): DiffSpec {

        logger?.d("getAndClearLatestDiffSpec() maxAgeMs:$maxAgeMs latestDiffSpec:$latestDiffSpec - thread.id: ${Thread.currentThread().id}")

        val latestDiffSpecAvailable = latestDiffSpec
        latestDiffSpec = fullDiffSpec

        return if (systemTimeWrapper.currentTimeMillis() - latestDiffSpecAvailable.timeStamp < maxAgeMs) {
            logger?.d(" ...returning latestDiffSpec:$latestDiffSpecAvailable - thread.id: ${Thread.currentThread().id}")
            latestDiffSpecAvailable
        } else {
            logger?.d(" ...too old, returning fullDiffSpec - thread.id: ${Thread.currentThread().id}")
            fullDiffSpec
        }
    }
}
