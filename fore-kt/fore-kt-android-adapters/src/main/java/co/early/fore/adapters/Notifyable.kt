package co.early.fore.adapters

import androidx.recyclerview.widget.RecyclerView

interface Notifyable<T : RecyclerView.ViewHolder?> {
    fun notifyDataSetChangedAuto()
}