package foo.bar.example.foreadapterskt.feature.playlist.diffable


import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
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
        private val logger: Logger
) : Observable by ObservableImp(workMode, logger), Diffable {

    private var currentTrackList = listOf<Track>()
    private var latestDiffSpec: DiffSpec? = createFullDiffSpec()


    fun removeTrack(index: Int) {
        logger.i("removeTrack() $index")
        checkIndex(index)
        val trackList = getListCopy()
        trackList.removeAt(index)
        updateList(trackList)
    }

    fun removeAllTracks() {
        logger.i("removeAllTracks()")
        val trackList = getListCopy()
        trackList.clear()
        updateList(trackList)
    }

    fun increasePlaysForTrack(index: Int) {
        logger.i("increasePlaysForTrack() $index")
        checkIndex(index)
        val trackList = getListCopy()
        trackList[index].increasePlaysRequested()
        updateList(trackList)
    }

    fun decreasePlaysForTrack(index: Int) {
        logger.i("decreasePlaysForTrack() $index")
        checkIndex(index)
        val trackList = getListCopy()
        trackList[index].decreasePlaysRequested()
        updateList(trackList)
    }

    fun getTrack(index: Int): Track {
        checkIndex(index)
        return currentTrackList[index]
    }

    fun addNTracks(n: Int) {
        logger.i("addNTracks() n:$n")
        val newTracks = ArrayList<Track>()
        for (ii in 0 until n) {
            newTracks.add(Track(generateRandomColourResource(), randomLong()))
        }
        val trackList = getListCopy()
        trackList.addAll(newTracks)
        updateList(trackList)
    }

    fun removeNTracks(n: Int) {
        logger.i("removeNTracks() n:$n")
        if (size() > n - 1) {
            val trackList = getListCopy()
            trackList.subList(0, n).clear()
            updateList(trackList)
        }
    }

    fun size(): Int {
        return currentTrackList.size
    }

    fun isEmpty(): Boolean {
        return currentTrackList.isEmpty()
    }

    fun hasAtLeastNItems(n: Int): Boolean {
        return currentTrackList.size >= n
    }

    private fun checkIndex(index: Int) {
        if (currentTrackList.isEmpty()) {
            throw IndexOutOfBoundsException("tracklist has no items in it, can not get index:$index")
        } else if (index < 0 || index > currentTrackList.size - 1) {
            throw IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (currentTrackList.size - 1) + " not:" + index)
        }
    }


    private fun updateList(newList: List<Track>){

        launchMain(workMode) {

            val result = withContextDefault(workMode) {

                // work out the differences in the lists
                val diffResult = DiffCalculator<Track>().createDiffResult(currentTrackList, newList)

                //return to the UI thread
                Pair(newList, DiffSpec(diffResult, systemTimeWrapper))
            }

            currentTrackList = result.first ?: emptyList()
            latestDiffSpec = result.second ?: createFullDiffSpec()
            logger.i("list updated")
            notifyObservers()
        }

    }

    private fun getListCopy(): MutableList<Track> {
        val listCopy = currentTrackList.map { it.copy() } //deep copy
        return listCopy.toMutableList()
    }

    /**
     * If the DiffResult is old, then we assume that whatever changes
     * were made to the list last time were never picked up by a
     * recyclerView (maybe because the list was not visible at the time).
     * In this case we clear the DiffResult and create a fresh one with a
     * full diff spec.
     *
     * @return the latest DiffResult for the list
     */
    override fun getAndClearLatestDiffSpec(maxAgeMs: Long): DiffSpec {

        val latestDiffSpecAvailable = latestDiffSpec
        val fullDiffSpec = createFullDiffSpec()

        latestDiffSpec = fullDiffSpec

        return if (systemTimeWrapper.currentTimeMillis() - latestDiffSpecAvailable!!.timeStamp < maxAgeMs) {
            latestDiffSpecAvailable
        } else {
            fullDiffSpec
        }
    }

    private fun createFullDiffSpec(): DiffSpec {
        return DiffSpec(null, systemTimeWrapper)
    }

}
