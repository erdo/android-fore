package foo.bar.example.foreadapterskt.ui.playlist.mutable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import co.early.fore.adapters.Adaptable
import co.early.fore.adapters.Notifyable
import co.early.fore.kt.adapters.NotifyableImp
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.Track
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel
import foo.bar.example.foreadapterskt.ui.playlist.mutable.MutablePlaylistAdapter.*
import foo.bar.example.foreadapterskt.ui.widget.PercentVBar

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

        holder.trackIncreasePlaysButton.setOnClickListener {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.increasePlaysForTrack(betterPosition)
            }
        }

        holder.trackDecreasePlaysButton.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.decreasePlaysForTrack(betterPosition)
            }
        }

        holder.trackRemoveButton.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.removeTrack(betterPosition)
            }
        }

        holder.itemView.setBackgroundResource(item.colourResource)
        holder.trackPlaysRequestedText.text = "${item.numberOfPlaysRequested}"
        holder.trackIncreasePlaysButton.isEnabled = item.canIncreasePlays()
        holder.trackDecreasePlaysButton.isEnabled = item.canDecreasePlays()
        holder.trackPercentVbar.setPercentDone(
                item.id,
                (item.numberOfPlaysRequested*100/Track.MAX_PLAYS_REQUESTED).toFloat()
        )
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackPlaysRequestedText: TextView
        val trackIncreasePlaysButton: Button
        val trackDecreasePlaysButton: Button
        val trackRemoveButton: Button
        val trackPercentVbar: PercentVBar
        init {
            trackPlaysRequestedText = view.findViewById(R.id.track_playsrequested_text)
            trackIncreasePlaysButton = view.findViewById(R.id.track_increaseplays_button)
            trackDecreasePlaysButton = view.findViewById(R.id.track_decreaseplays_button)
            trackRemoveButton = view.findViewById(R.id.track_remove_button)
            trackPercentVbar = view.findViewById(R.id.track_percent_vbar)
        }
    }
}
