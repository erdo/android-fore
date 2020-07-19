package foo.bar.example.foreadapterskt.feature.playlist

import androidx.annotation.ColorRes
import co.early.fore.kt.adapters.DiffComparatorCopyable


class Track(
        @param:ColorRes @field:ColorRes
        val colourResource: Int,
        val id: Long,
        playsRequested: Int? = null
) : DiffComparatorCopyable<Track> {

    //TODO look at getChangePayload() for DiffComparator

    var numberOfPlaysRequested: Int
        private set

    init {
        numberOfPlaysRequested = playsRequested?.let {
            it
        } ?: MIN_PLAYS_REQUESTED

    }

    // these aren't full observable models,
    // we want the playlist to manage any changes
    // so we don't make this method public
    fun increasePlaysRequested() {
        if (canIncreasePlays()) {
            numberOfPlaysRequested++
        }
    }

    // these aren't full observable models,
    // we want the playlist to manage any changes
    // so we don't make this method public
    fun decreasePlaysRequested() {
        if (canDecreasePlays()) {
            numberOfPlaysRequested--
        }
    }

    fun canIncreasePlays(): Boolean {
        return numberOfPlaysRequested < MAX_PLAYS_REQUESTED
    }

    fun canDecreasePlays(): Boolean {
        return numberOfPlaysRequested > MIN_PLAYS_REQUESTED
    }

    companion object {
        private const val MIN_PLAYS_REQUESTED = 1
        private const val MAX_PLAYS_REQUESTED = 9
    }

    override fun itemsTheSame(other: Track?): Boolean {
        return if (other != null) {
            this.id == other.id
        } else false
    }

    override fun contentsTheSame(other: Track?): Boolean {
        return if (other != null) {
            this.numberOfPlaysRequested == other.numberOfPlaysRequested
                    && this.colourResource == other.colourResource
        } else false
    }

    override fun copy(): Track {
        return Track(
                colourResource,
                id,
                numberOfPlaysRequested
        )
    }
}
