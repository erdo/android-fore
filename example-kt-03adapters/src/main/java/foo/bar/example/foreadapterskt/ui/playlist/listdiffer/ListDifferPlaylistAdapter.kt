package foo.bar.example.foreadapterskt.ui.playlist.listdiffer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.randomLong
import foo.bar.example.foreadapterskt.feature.playlist.Track
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_decreaseplays_button
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_increaseplays_button
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_playsrequested_text
import kotlinx.android.synthetic.main.activity_playlists_listitem.view.track_remove_button
import java.util.ArrayList


/**
 *
 */
class ListDifferPlaylistAdapter(private val syncableView: SyncableView, private val logger: Logger) :
        RecyclerView.Adapter<ListDifferPlaylistAdapter.ViewHolder>() {

    val itemCallback: DiffUtil.ItemCallback<Track> = object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.itemsTheSame(newItem)
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.contentsTheSame(newItem)
        }
    }
    private val differ = AsyncListDiffer<Track>(this, itemCallback)


    fun removeTrack(index: Int) {
        logger.i("removeTrack() $index")
        val mutableList = getListCopy()
        mutableList.removeAt(index)
        differ.submitList(mutableList) {
            logger.i("removeTrack() updated")
            syncableView.syncView()
        }
    }

    fun removeAllTracks() {
        logger.i("removeAllTracks()")
        val mutableList = getListCopy()
        mutableList.clear()
        differ.submitList(mutableList) {
            logger.i("removeAllTracks() updated")
            syncableView.syncView()
        }
    }

    fun increasePlaysForTrack(index: Int) {
        logger.i("increasePlaysForTrack() $index")
        val mutableList = getListCopy()
        mutableList[index].increasePlaysRequested()
        differ.submitList(mutableList) {
            logger.i("increasePlaysForTrack() updated")
            syncableView.syncView()
        }
    }

    fun decreasePlaysForTrack(index: Int) {
        logger.i("decreasePlaysForTrack() $index")
        val mutableList = getListCopy()
        mutableList[index].decreasePlaysRequested()
        differ.submitList(mutableList) {
            logger.i("decreasePlaysForTrack() updated")
            syncableView.syncView()
        }
    }

    fun addNTracks(n: Int) {
        logger.i("addNTracks() n:$n")
        val newTracks = ArrayList<Track>()
        for (ii in 0 until n) {
            newTracks.add(Track(RandomStuffGeneratorUtil.generateRandomColourResource(), randomLong()))
        }
        val mutableList = getListCopy()
        mutableList.addAll(newTracks)
        differ.submitList(mutableList) {
            logger.i("addNTracks() updated")
            syncableView.syncView()
        }
    }

    fun removeNTracks(n: Int) {
        logger.i("removeNTracks() n:$n")
        val mutableList = getListCopy()
        if (mutableList.size > n - 1) {
            mutableList.subList(0, n).clear()
            differ.submitList(mutableList) {
                logger.i("remove5Tracks() updated")
                syncableView.syncView()
            }
        }
    }

    fun isEmpty(): Boolean {
        return differ.currentList.isEmpty()
    }

    fun hasAtLeastNItems(n: Int): Boolean {
        return differ.currentList.size >= n
    }

    private fun getListCopy(): MutableList<Track> {
        val listCopy = differ.currentList.map { it.copy() } //deep copy
        return listCopy.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_playlists_listitem, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.tag = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.track_increaseplays_button.setOnClickListener {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                increasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_decreaseplays_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                decreasePlaysForTrack(betterPosition)
            }
        }

        holder.itemView.track_remove_button.setOnClickListener {
            val betterPosition = holder.adapterPosition
            if (betterPosition != NO_POSITION) {
                removeTrack(betterPosition)
            }
        }

        holder.itemView.setBackgroundResource(item.colourResource)
        holder.itemView.track_playsrequested_text.text = "${item.numberOfPlaysRequested}"
        holder.itemView.track_increaseplays_button.isEnabled = item.canIncreasePlays()
        holder.itemView.track_decreaseplays_button.isEnabled = item.canDecreasePlays()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
