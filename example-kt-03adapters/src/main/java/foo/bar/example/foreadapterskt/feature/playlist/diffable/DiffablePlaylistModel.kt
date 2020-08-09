package foo.bar.example.foreadapterskt.feature.playlist.diffable

import co.early.fore.adapters.DiffCalculator
import co.early.fore.adapters.DiffSpec
import co.early.fore.adapters.Diffable
import co.early.fore.core.WorkMode
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.coroutine.withContextDefault
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.generateRandomColourResource
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.randomLong
import foo.bar.example.foreadapterskt.feature.playlist.Track
import java.util.ArrayList

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class DiffablePlaylistModel(
        private val systemTimeWrapper: SystemTimeWrapper,
        private val workMode: WorkMode,
        private val logger: Logger,
        private val diffable: DiffableImpl<Track> = DiffableImpl<Track>(systemTimeWrapper, workMode, logger)
) : Observable by diffable,
    Diffable by diffable {

    fun removeTrack(index: Int) {
        logger.i("removeTrack() $index")
        checkIndex(index)
        val trackList = diffable.getListCopy()
        trackList.removeAt(index)
        diffable.updateList(trackList)
    }

    fun removeAllTracks() {
        logger.i("removeAllTracks()")
        val trackList = diffable.getListCopy()
        trackList.clear()
        diffable.updateList(trackList)
    }

    fun increasePlaysForTrack(index: Int) {
        logger.i("increasePlaysForTrack() $index")
        checkIndex(index)
        val trackList = diffable.getListCopy()
        trackList[index].increasePlaysRequested()
        diffable.updateList(trackList)
    }

    fun decreasePlaysForTrack(index: Int) {
        logger.i("decreasePlaysForTrack() $index")
        checkIndex(index)
        val trackList = diffable.getListCopy()
        trackList[index].decreasePlaysRequested()
        diffable.updateList(trackList)
    }

    fun getTrack(index: Int): Track {
        checkIndex(index)
        return diffable.getItem[index]
    }

    fun addNTracks(n: Int) {
        logger.i("addNTracks() n:$n")
        val newTracks = ArrayList<Track>()
        for (ii in 0 until n) {
            newTracks.add(Track(generateRandomColourResource(), randomLong()))
        }
        val trackList = diffable.getListCopy()
        trackList.addAll(newTracks)
        diffable.updateList(trackList)
    }

    fun removeNTracks(n: Int) {
        logger.i("removeNTracks() n:$n")
        if (size() > n - 1) {
            val trackList = diffable.getListCopy()
            trackList.subList(0, n).clear()
            diffable.updateList(trackList)
        }
    }

    fun isEmpty(): Boolean {
        return !hasAtLeastNItems(1)
    }

    fun hasAtLeastNItems(n: Int): Boolean {
        return size() >= n
    }

    fun size(): Int {
        return diffable.size
    }

    private fun checkIndex(index: Int) {
        if (isEmpty()) {
            throw IndexOutOfBoundsException("tracklist has no items in it, can not get index:$index")
        } else if (index < 0 || index > size() - 1) {
            throw IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (size() - 1) + " not:" + index)
        }
    }
}
