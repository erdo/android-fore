package foo.bar.example.foreadapterskt.ui.playlist.diffable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import co.early.fore.adapters.ChangeAwareAdapter
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_decreaseplays_button
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_increaseplays_button
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_playsrequested_text
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_remove_button

/**
 * Demonstrating list animations with [Diffable]
 *
 * fore's [Diffable] classes use android's DiffUtil behind the scenes.
 *
 * In this example you'll see the adapter is much less verbose than if we were using google's
 * [AsyncListDiffer] method (you'll find most of the code in [DiffablePlaylistModel])
 *
 * Diffable is run using coroutines as DiffUtil is a lot more resource intensive than Updatable
 *
 */
class DiffablePlaylistModelAdapter(
        private val diffablePlaylistModel: DiffablePlaylistModel
) : ChangeAwareAdapter<DiffablePlaylistModelAdapter.ViewHolder>(diffablePlaylistModel) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_playlists_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = diffablePlaylistModel.getTrack(position)

        holder.itemView.track_increaseplays_button.setOnClickListener {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                diffablePlaylistModel.increasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_decreaseplays_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                diffablePlaylistModel.decreasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_remove_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                diffablePlaylistModel.removeTrack(betterPosition)
            }
        }

        holder.itemView.setBackgroundResource(item.colourResource)
        holder.itemView.track_playsrequested_text.text = "${item.numberOfPlaysRequested}"
        holder.itemView.track_increaseplays_button.isEnabled = item.canIncreasePlays()
        holder.itemView.track_decreaseplays_button.isEnabled = item.canDecreasePlays()
    }

    override fun getItemCount(): Int {
        return diffablePlaylistModel.size()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
