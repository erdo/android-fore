package foo.bar.example.foreadapterskt.feature.playlist


import co.early.fore.adapters.ChangeAwareArrayList
import co.early.fore.adapters.ChangeAwareList
import co.early.fore.adapters.UpdateSpec
import co.early.fore.adapters.Updateable
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.example.foreadapterskt.feature.playlist.RandomTrackGeneratorUtil.generateRandomColourResource
import java.util.ArrayList

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class PlaylistAdvancedModel(
        systemTimeWrapper: SystemTimeWrapper,
        workMode: WorkMode,
        private val logger: Logger
) : Observable by ObservableImp(workMode, logger),
    Updateable {

    private val trackList: ChangeAwareList<Track>

    val trackListSize: Int
        get() = trackList.size


    init {
        trackList = ChangeAwareArrayList(systemTimeWrapper)
    }

    fun addNewTrack() {
        logger.i("addNewTrack()")
        trackList.add(Track(generateRandomColourResource()))
        notifyObservers()
    }

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

    fun add5NewTracks() {
        logger.i("add5NewTracks()")
        val newTracks = ArrayList<Track>()
        for (ii in 0..4) {
            newTracks.add(Track(generateRandomColourResource()))
        }
        trackList.addAll(0, newTracks)
        notifyObservers()
    }

    fun remove5Tracks() {
        logger.i("remove5Tracks()")
        if (trackListSize > 4) {
            trackList.removeRange(0, 5)
            notifyObservers()
        }
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
