package foo.bar.example.foreadapterskt.ui.playlist.updatable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import co.early.fore.adapters.ChangeAwareAdapter
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_decreaseplays_button
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_increaseplays_button
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_playsrequested_text
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_remove_button


class UpdatablePlaylistModelAdapter(private val updatablePlaylistModel: UpdatablePlaylistModel) : ChangeAwareAdapter<UpdatablePlaylistModelAdapter.ViewHolder>(updatablePlaylistModel) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_playlists_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = updatablePlaylistModel.getTrack(position)

        holder.itemView.track_increaseplays_button.setOnClickListener {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                updatablePlaylistModel.increasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_decreaseplays_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                updatablePlaylistModel.decreasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_remove_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                updatablePlaylistModel.removeTrack(betterPosition)
            }
        }

        holder.itemView.setBackgroundResource(item.colourResource)
        holder.itemView.track_playsrequested_text.text = "${item.numberOfPlaysRequested}"
        holder.itemView.track_increaseplays_button.isEnabled = item.canIncreasePlays()
        holder.itemView.track_decreaseplays_button.isEnabled = item.canDecreasePlays()
    }

    override fun getItemCount(): Int {
        return updatablePlaylistModel.trackListSize
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}