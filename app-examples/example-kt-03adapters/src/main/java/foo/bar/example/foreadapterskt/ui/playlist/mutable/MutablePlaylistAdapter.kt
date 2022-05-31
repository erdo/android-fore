package foo.bar.example.foreadapterskt.ui.playlist.mutable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import co.early.fore.adapters.Adaptable
import co.early.fore.adapters.Notifyable
import co.early.fore.kt.adapters.NotifyableImp
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.Track
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel
import foo.bar.example.foreadapterskt.ui.playlist.mutable.MutablePlaylistAdapter.*
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.*

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
class MutablePlaylistAdapter(
        private val mutablePlaylistModel: MutablePlaylistModel,
        private val notifyableImp: NotifyableImp<ViewHolder> = NotifyableImp(updateable = mutablePlaylistModel)
) :
        RecyclerView.Adapter<ViewHolder>(),
        Notifyable<ViewHolder> by notifyableImp,
        Adaptable<Track> by mutablePlaylistModel {

    init {
        notifyableImp.initializeAdapter(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_playlists_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mutablePlaylistModel.getItem(position)

        holder.itemView.track_increaseplays_button.setOnClickListener {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.increasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_decreaseplays_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.decreasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_remove_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.removeTrack(betterPosition)
            }
        }

        holder.itemView.setBackgroundResource(item.colourResource)
        holder.itemView.track_playsrequested_text.text = "${item.numberOfPlaysRequested}"
        holder.itemView.track_increaseplays_button.isEnabled = item.canIncreasePlays()
        holder.itemView.track_decreaseplays_button.isEnabled = item.canDecreasePlays()
        holder.itemView.track_percent_vbar.setPercentDone(
                item.id,
                (item.numberOfPlaysRequested*100/Track.MAX_PLAYS_REQUESTED).toFloat()
        )
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
