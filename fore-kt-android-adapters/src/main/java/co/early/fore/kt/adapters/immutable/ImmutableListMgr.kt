package co.early.fore.kt.adapters.immutable

import co.early.fore.adapters.Adaptable
import co.early.fore.adapters.immutable.*
import co.early.fore.core.WorkMode
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.awaitDefault
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * This class holds the definition of the current list for an adapter, so it's getItemCount() and getItem()
 * functions are always correct.
 *
 * The way to change the list is to pass a function to changeList() you will receive a deepCopy
 * of the current list which you can change however you wish, once you are finished the changes
 * will be applied.
 *
 * You can also replace the list completely by calling replaceList()
 *
 * The DiffSpec between the old and the new list will be calculated off the UI thread and the
 * results applied atomically on the UI thread. Observers will be notified on the UI thread.
 *
 */
class ImmutableListMgr<T>(
        private val systemTimeWrapper: SystemTimeWrapper? = null,
        private val workMode: WorkMode? = null,
        private val logger: Logger? = null
) : Observable by ObservableImp(workMode, logger),
        Adaptable<T>,
        Diffable
        where T : DeepCopyable<T>, T : DiffComparator<T> {

    private var latestDiffSpec: DiffSpec? = createFullDiffSpec()
    private var currentList = listOf<T>()
    private var currentListMutableCopy = mutableListOf<T>()
    private var currentListVersion = 0

    private val mutex = Mutex()

    fun changeList(block: (MutableList<T>) -> Unit) {
        launchMain(Fore.getWorkMode(workMode)) {
            mutex.withLock {
                block(currentListMutableCopy)
            }
            updateList(currentListMutableCopy)
        }
    }

    fun replaceList(block: () -> List<T>) {
        updateList(block())
    }

    override fun getItem(index: Int): T {
        return currentList[index]
    }

    override fun getItemCount(): Int = currentList.size

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

        return if (Fore.getSystemTimeWrapper(systemTimeWrapper).currentTimeMillis() - latestDiffSpecAvailable!!.timeStamp < maxAgeMs) {
            latestDiffSpecAvailable
        } else {
            fullDiffSpec
        }
    }

    private fun updateList(newList: List<T>) {

        val input = Input(currentListVersion, currentList, newList)

        launchMain(Fore.getWorkMode(workMode)) {

            val result = awaitDefault(Fore.getWorkMode(workMode)) {

                // work out the differences in the lists
                val diffResult = DiffCalculator<T>().createDiffResult(input.currentList, input.newList)

                val newListCopy: List<T>
                val newListCopy2: List<T>

                mutex.withLock {
                    //create a mutable copy of the new list, ready for when client wants to change it
                    newListCopy = newList.map { it.deepCopy() }
                    newListCopy2 = newList.map { it.deepCopy() }
                }

                //return to the UI thread
                Result(input.currentListVersion, newListCopy, newListCopy2.toMutableList(), DiffSpec(diffResult, Fore.getSystemTimeWrapper(systemTimeWrapper)))
            }

            // otherwise this is an old change, we ignore it
            if (result.oldListVersion == currentListVersion) {
                currentListVersion += 1
                currentList = result.newList
                currentListMutableCopy = result.newListCopy
                latestDiffSpec = result.diffSpec
                Fore.getLogger(logger).i("list updated, thread:" + Thread.currentThread().id)
                notifyObservers()
            }
        }
    }

    private fun createFullDiffSpec(): DiffSpec {
        return DiffSpec(null, Fore.getSystemTimeWrapper(systemTimeWrapper))
    }

    private data class Input<T>(
            val currentListVersion: Int,
            val currentList: List<T>,
            val newList: List<T>
    )

    private data class Result<T>(
            val oldListVersion: Int,
            val newList: List<T>,
            val newListCopy: MutableList<T>,
            val diffSpec: DiffSpec
    )
}
