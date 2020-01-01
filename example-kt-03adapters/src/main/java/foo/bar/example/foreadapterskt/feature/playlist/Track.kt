package foo.bar.example.foreadapterskt.feature.playlist

import androidx.annotation.ColorRes


class Track(
        @param:ColorRes @field:ColorRes
        val colourResource: Int
) {
    var numberOfPlaysRequested = MIN_PLAYS_REQUESTED
        private set

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
        private val MIN_PLAYS_REQUESTED = 1
        private val MAX_PLAYS_REQUESTED = 9
    }
}
