package foo.bar.example.foreadapterskt.feature.playlist.mutable

import co.early.fore.adapters.Adaptable
import co.early.fore.adapters.mutable.ChangeAwareList
import co.early.fore.adapters.mutable.Updateable
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.adapters.mutable.ChangeAwareArrayList
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.generateRandomColourResource
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.randomLong
import foo.bar.example.foreadapterskt.feature.playlist.Track
import java.util.ArrayList

/**
 * Example model based on **mutable** list data
 *
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
class MutablePlaylistModel(
        private val logger: Logger,
        private val trackList: ChangeAwareList<Track> = ChangeAwareArrayList()
) : Observable by ObservableImp(),
    Updateable by trackList,
    Adaptable<Track> {

    fun removeTrack(index: Int) {
        logger.i("removeTrack() $index")
        checkIndex(index)
        trackList.removeAt(index)
        notifyObservers()
    }

    fun removeAllTracks() {
        logger.i("removeAllTracks()")
        trackList.clear()
        notifyObservers()
    }

    fun increasePlaysForTrack(index: Int) {
        logger.i("increasePlaysForTrack() $index")
        getItem(index).increasePlaysRequested()
        trackList.makeAwareOfDataChange(index)
        notifyObservers()
    }

    fun decreasePlaysForTrack(index: Int) {
        logger.i("decreasePlaysForTrack() $index")
        getItem(index).decreasePlaysRequested()
        trackList.makeAwareOfDataChange(index)
        notifyObservers()
    }

    fun addNTracks(n: Int) {
        logger.i("addNTracks() n:$n")
        val newTracks = ArrayList<Track>()
        for (ii in 0 until n) {
            newTracks.add(Track(generateRandomColourResource(), randomLong()))
        }
        trackList.addAll(newTracks)
        logger.i("addNTracks() updated")
        notifyObservers()
    }

    fun removeNTracks(n: Int) {
        logger.i("removeNTracks() n:$n")
        if (itemCount > n - 1) {
            trackList.removeRange(0, n)
            notifyObservers()
        }
    }

    fun isEmpty(): Boolean {
        return trackList.isEmpty()
    }

    fun hasAtLeastNItems(n: Int): Boolean {
        return trackList.size >= n
    }

    override fun getItem(index: Int): Track {
        checkIndex(index)
        return trackList[index]
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    private fun checkIndex(index: Int) {
        if (trackList.size == 0) {
            throw IndexOutOfBoundsException("tracklist has no items in it, can not get index:$index")
        } else if (index < 0 || index > trackList.size - 1) {
            throw IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (trackList.size - 1) + " not:" + index)
        }
    }
}
