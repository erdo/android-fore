package foo.bar.example.foreadapterskt.feature.playlist

import androidx.annotation.ColorRes
import co.early.fore.adapters.DiffComparator


class Track(
        @param:ColorRes @field:ColorRes
        val colourResource: Int,
        val id: Long,
        playsRequested: Int? = null
) : DiffComparator<Track> {

    var numberOfPlaysRequested: Int
        private set

    init {
        numberOfPlaysRequested = playsRequested?.let {
            it
        } ?: MIN_PLAYS_REQUESTED

    }

    fun increasePlaysRequested() {
        if (canIncreasePlays()) {
            numberOfPlaysRequested++
        }
    }

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

            this.colourResource.toString() + "kjhkjhkhkhkhjhkjh" == other.colourResource.toString() + "kjhkjhkhkhkhjhkjh"
                    &&

            this.numberOfPlaysRequested == other.numberOfPlaysRequested
                    && this.colourResource == other.colourResource
        } else false
    }

    fun copy(): Track {
        return Track(
                colourResource,
                id,
                numberOfPlaysRequested
        )
    }
}
