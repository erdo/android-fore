package co.early.fore.kt.adapters

import androidx.recyclerview.widget.RecyclerView
import co.early.fore.adapters.immutable.Diffable
import co.early.fore.adapters.mutable.UpdateSpec.UpdateType
import co.early.fore.adapters.mutable.Updateable

abstract class ChangeAwareAdapter<VH : RecyclerView.ViewHolder?>(
        private val updateable: Updateable? = null,
        private val diffable: Diffable? = null
) : RecyclerView.Adapter<VH>() {

    init {
        if (updateable == null && diffable == null) {
            throw IllegalArgumentException("updateable and diffable cannot both be null")
        }
        if (updateable != null && diffable != null) {
            throw IllegalArgumentException("one of updateable or diffable must be null")
        }
    }

    fun notifyDataSetChangedAuto() {
        if (updateable != null) {
            processUpdateable()
        } else {
            processDiffable()
        }
    }

    private fun processUpdateable() {
        val updateSpec = updateable!!.getAndClearLatestUpdateSpec(MAX_AGE_MS_BEFORE_IGNORE.toLong())
        when (updateSpec.type) {
            UpdateType.FULL_UPDATE -> notifyDataSetChanged()
            UpdateType.ITEM_CHANGED -> notifyItemRangeChanged(updateSpec.rowPosition, updateSpec.rowsEffected)
            UpdateType.ITEM_REMOVED -> notifyItemRangeRemoved(updateSpec.rowPosition, updateSpec.rowsEffected)
            UpdateType.ITEM_INSERTED -> notifyItemRangeInserted(updateSpec.rowPosition, updateSpec.rowsEffected)
        }
    }

    private fun processDiffable() {
        val diffSpec = diffable!!.getAndClearLatestDiffSpec(MAX_AGE_MS_BEFORE_IGNORE.toLong())
        if (diffSpec.diffResult == null) {
            notifyDataSetChanged()
        } else {
            diffSpec.diffResult.dispatchUpdatesTo(this)
        }
    }

    companion object {
        private const val MAX_AGE_MS_BEFORE_IGNORE = 50
    }
}