package foo.bar.example.foreadapterskt.feature.playlist.updatable


import co.early.fore.adapters.ChangeAwareArrayList
import co.early.fore.adapters.ChangeAwareList
import co.early.fore.adapters.UpdateSpec
import co.early.fore.adapters.Updateable
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.generateRandomColourResource
import foo.bar.example.foreadapterskt.feature.playlist.RandomStuffGeneratorUtil.randomLong
import foo.bar.example.foreadapterskt.feature.playlist.Track
import java.util.ArrayList

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class UpdatablePlaylistModel(
        systemTimeWrapper: SystemTimeWrapper,
        workMode: WorkMode,
        private val logger: Logger
) : Observable by ObservableImp(workMode, logger),
        Updateable {

    private val trackList: ChangeAwareList<Track> = ChangeAwareArrayList(systemTimeWrapper)

    val trackListSize: Int
        get() = trackList.size


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
        getTrack(index).increasePlaysRequested()
        trackList.makeAwareOfDataChange(index)
        notifyObservers()
    }

    fun decreasePlaysForTrack(index: Int) {
        logger.i("decreasePlaysForTrack() $index")
        getTrack(index).decreasePlaysRequested()
        trackList.makeAwareOfDataChange(index)
        notifyObservers()
    }

    fun getTrack(index: Int): Track {
        checkIndex(index)
        return trackList[index]
    }

    fun addNTracks(n: Int) {
        logger.i("addNTracks() n:$n")
        val newTracks = ArrayList<Track>()
        for (ii in 0 until n) {
            newTracks.add(Track(generateRandomColourResource(), randomLong()))
        }
        trackList.addAll(0, newTracks)
        notifyObservers()
    }

    fun removeNTracks(n: Int) {
        logger.i("removeNTracks() n:$n")
        if (trackListSize > n - 1) {
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

    private fun checkIndex(index: Int) {
        if (trackList.size == 0) {
            throw IndexOutOfBoundsException("tracklist has no items in it, can not get index:$index")
        } else if (index < 0 || index > trackList.size - 1) {
            throw IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (trackList.size - 1) + " not:" + index)
        }
    }

    override fun getAndClearLatestUpdateSpec(maxAgeMs: Long): UpdateSpec {
        return trackList.getAndClearLatestUpdateSpec(maxAgeMs)
    }

}
