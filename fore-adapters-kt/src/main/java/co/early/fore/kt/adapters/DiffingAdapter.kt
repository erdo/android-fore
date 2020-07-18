package co.early.fore.kt.adapters

import androidx.recyclerview.widget.RecyclerView
import co.early.fore.adapters.DiffComparator
import co.early.fore.core.WorkMode
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.logging.Logger

/**
 * Android adapter for immutable list data, for example if your list data comes from a database
 * or from an immutable view state along the lines of MVI or Redux.
 *
 * For these situations the only way to calculate the animated list changes is to use
 * Android's [androidx.recyclerview.widget.DiffUtil] which is quite computationally intensive.
 * For this reason, by default [DiffingAdapter] skips animated list changes for lists above
 * 1000 rows, but you can configure this.
 *
 * If your adapter is driven by list data that is changed in memory, you may be able to use fore's
 * [UpdateSpec] and the [ChangeAware___] classes for a less power hungry solution.
 *
 * Use this adapter in the same way as you would [RecyclerView.Adapter], but whenever you get new
 * list data call [DiffingAdapter.setNewList]. Assuming [WorkMode.ASYNCHRONOUS] DiffUtil will be
 * run on Dispatchers.IO and then back on Dispatchers.Main the new list data will be set and
 * updates dispatched. In [WorkMode.SYNCHRONOUS] mode, no coroutines will be used and all code
 * will be run synchronously.
 *
 * For this to work, [DiffingAdapter] needs to keep hold of the actual list, it takes care of
 * [RecyclerView.Adapter.getItemCount]. The current list is still available to the caller
 * via [DiffingAdapter.list] so that methods like [RecyclerView.Adapter.onCreateViewHolder] and
 * [RecyclerView.Adapter.onBindViewHolder] can be implemented.
 *
 * If you pass in a logger, you will receive debugging logs that include the thread.id to help you
 * diagnose issues.
 */
abstract class DiffingAdapter<T : DiffComparator<T>, VH : RecyclerView.ViewHolder>(
        private val systemTimeWrapper: SystemTimeWrapper,
        private val workMode: WorkMode,
        private val maxSizeForDiffUtil: Int = 1000,
        private val maxAgeMsBeforeIgnore: Int = 50,
        private val logger: Logger? = null
) : RecyclerView.Adapter<VH>() {

    var list: List<T> = emptyList()
        private set
    private val diffable = DiffableImp<T>(
            systemTimeWrapper,
            workMode,
            maxSizeForDiffUtil,
            logger
    )

    fun setNewList(newList: List<T>) {

        logger?.d("setNewList() - thread.id: ${Thread.currentThread().id}")

        launchMain(workMode) {
            diffable.setDiffSpec(list, newList) {
                list = newList
                autoNotify()
            }
        }
    }

    private fun autoNotify() {

        logger?.d("autoNotify() - thread.id: ${Thread.currentThread().id}")

        val diffSpec = diffable.getAndClearLatestDiffSpec(maxAgeMsBeforeIgnore.toLong())
        if (diffSpec.diffResult == null) {
            notifyDataSetChanged()
        } else {
            diffSpec.diffResult.dispatchUpdatesTo(this)
        }
    }

    final override fun getItemCount(): Int {

        logger?.d("getItemCount() [${list.size}] - thread.id: ${Thread.currentThread().id}")

        return list.size
    }
}
