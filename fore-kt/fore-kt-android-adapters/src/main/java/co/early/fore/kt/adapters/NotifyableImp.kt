package co.early.fore.kt.adapters

import androidx.recyclerview.widget.RecyclerView
import co.early.fore.adapters.Notifyable
import co.early.fore.adapters.immutable.Diffable
import co.early.fore.adapters.mutable.UpdateSpec.UpdateType
import co.early.fore.adapters.mutable.Updateable

class NotifyableImp<VH : RecyclerView.ViewHolder>(
        private val updateable: Updateable? = null,
        private val diffable: Diffable? = null
) : Notifyable<VH> {

    private lateinit var adapter: RecyclerView.Adapter<VH>

    init {
        if (updateable == null && diffable == null) {
            throw IllegalArgumentException("updateable and diffable cannot both be null")
        }
        if (updateable != null && diffable != null) {
            throw IllegalArgumentException("one of updateable or diffable must be null")
        }
    }

    fun initializeAdapter(adapter: RecyclerView.Adapter<VH>) {
        if (this::adapter.isInitialized) {
            throw IllegalAccessException("adapter has already been initialized")
        }
        this.adapter = adapter
    }

    override fun notifyDataSetChangedAuto() {

        if (!this::adapter.isInitialized) {
            throw IllegalAccessException("adapter must be initialized first, please call initializeAdapter() from the init{} of your Adapter class")
        }

        if (updateable != null) {
            processUpdateable()
        } else {
            processDiffable()
        }
    }

    private fun processUpdateable() {
        val updateSpec = updateable!!.getAndClearLatestUpdateSpec(MAX_AGE_MS_BEFORE_IGNORE.toLong())
        when (updateSpec.type) {
            UpdateType.FULL_UPDATE -> adapter.notifyDataSetChanged()
            UpdateType.ITEM_CHANGED -> adapter.notifyItemRangeChanged(updateSpec.rowPosition, updateSpec.rowsEffected)
            UpdateType.ITEM_REMOVED -> adapter.notifyItemRangeRemoved(updateSpec.rowPosition, updateSpec.rowsEffected)
            UpdateType.ITEM_INSERTED -> adapter.notifyItemRangeInserted(updateSpec.rowPosition, updateSpec.rowsEffected)
        }
    }

    private fun processDiffable() {
        val diffSpec = diffable!!.getAndClearLatestDiffSpec(MAX_AGE_MS_BEFORE_IGNORE.toLong())
        if (diffSpec.diffResult == null) {
            adapter.notifyDataSetChanged()
        } else {
            diffSpec.diffResult.dispatchUpdatesTo(adapter)
        }
    }

    companion object {
        private const val MAX_AGE_MS_BEFORE_IGNORE = 50
    }
}
