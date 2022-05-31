package foo.bar.example.foreadapterskt.feature.playlist.immutable

import co.early.fore.adapters.Adaptable
import co.early.fore.adapters.immutable.Diffable
import co.early.fore.core.observer.Observable
import co.early.fore.kt.adapters.immutable.ImmutableListMgr
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.generateRandomColourResource
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.randomLong
import foo.bar.example.foreadapterskt.feature.playlist.Track
import java.util.ArrayList

/**
 * Example model based on **immutable** list data
 *
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
class ImmutablePlaylistModel (
        private val logger: Logger,
        private val listMgr: ImmutableListMgr<Track> = ImmutableListMgr(logger = logger)
) :
    Diffable by listMgr,
    Observable by listMgr,
    Adaptable<Track> by listMgr {

    fun removeTrack(index: Int) {
        logger.i("removeTrack() $index")
        checkIndex(index)
        listMgr.changeList {
            it.removeAt(index)
        }
    }

    fun removeAllTracks() {
        logger.i("removeAllTracks()")
        listMgr.changeList {
            it.clear()
        }
    }

    fun increasePlaysForTrack(index: Int) {
        logger.i("increasePlaysForTrack() $index")
        checkIndex(index)
        listMgr.changeList {
            it[index].increasePlaysRequested()
        }
    }

    fun decreasePlaysForTrack(index: Int) {
        logger.i("decreasePlaysForTrack() $index")
        checkIndex(index)
        listMgr.changeList {
            it[index].decreasePlaysRequested()
        }
    }

    fun addNTracks(n: Int) {
        logger.i("addNTracks() n:$n")

        val newTracks = ArrayList<Track>()
        for (ii in 0 until n) {
            newTracks.add(Track(generateRandomColourResource(), randomLong()))
        }

        listMgr.changeList {
            it.addAll(newTracks)
        }
    }

    fun removeNTracks(n: Int) {
        logger.i("removeNTracks() n:$n")
        if (getItemCount() > n - 1) {
            listMgr.changeList {
                it.subList(0, n).clear()
            }
        }
    }

    fun isEmpty(): Boolean {
        return !hasAtLeastNItems(1)
    }

    fun hasAtLeastNItems(n: Int): Boolean {
        return getItemCount() >= n
    }

    fun replaceTracks(newTrackList: List<Track>) {
        logger.i("replaceTracks()")
        listMgr.replaceList {
            newTrackList
        }
    }

    private fun checkIndex(index: Int) {
        if (isEmpty()) {
            throw IndexOutOfBoundsException("tracklist has no items in it, can not get index:$index")
        } else if (index < 0 || index > getItemCount() - 1) {
            throw IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (getItemCount() - 1) + " not:" + index)
        }
    }
}
