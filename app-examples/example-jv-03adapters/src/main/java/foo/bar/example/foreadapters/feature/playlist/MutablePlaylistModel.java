package foo.bar.example.foreadapters.feature.playlist;


import java.util.ArrayList;
import java.util.List;

import co.early.fore.adapters.Adaptable;
import co.early.fore.adapters.mutable.ChangeAwareArrayList;
import co.early.fore.adapters.mutable.ChangeAwareList;
import co.early.fore.adapters.mutable.UpdateSpec;
import co.early.fore.adapters.mutable.Updateable;
import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.time.SystemTimeWrapper;

import static foo.bar.example.foreadapters.feature.playlist.RandomTrackGeneratorUtil.generateRandomColourResource;

/**
 * Example model based on **mutable** list data
 *
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
public class MutablePlaylistModel extends ObservableImp implements  Adaptable<Track>, Updateable {

    private static final String LOG_TAG = MutablePlaylistModel.class.getSimpleName();

    private final Logger logger;
    private final ChangeAwareList<Track> trackList;


    public MutablePlaylistModel(SystemTimeWrapper systemTimeWrapper, WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.logger = Affirm.notNull(logger);
        trackList = new ChangeAwareArrayList<>(Affirm.notNull(systemTimeWrapper));
    }

    public void addNewTrack() {
        logger.i(LOG_TAG, "addNewTrack()");
        trackList.add(new Track(generateRandomColourResource()));
        notifyObservers();
    }

    public void removeTrack(int index) {
        logger.i(LOG_TAG, "removeTrack() " + index);
        checkIndex(index);
        trackList.remove(index);
        notifyObservers();
    }

    public void removeAllTracks() {
        logger.i(LOG_TAG, "removeAllTracks()");
        trackList.clear();
        notifyObservers();
    }

    public void increasePlaysForTrack(int index) {
        logger.i(LOG_TAG, "increasePlaysForTrack() " + index);
        getItem(index).increasePlaysRequested();
        trackList.makeAwareOfDataChange(index);
        notifyObservers();
    }

    public void decreasePlaysForTrack(int index) {
        logger.i(LOG_TAG, "decreasePlaysForTrack() " + index);
        getItem(index).decreasePlaysRequested();
        trackList.makeAwareOfDataChange(index);
        notifyObservers();
    }

    public void add5NewTracks() {
        logger.i(LOG_TAG, "add5NewTracks()");
        List<Track> newTracks = new ArrayList<>();
        for (int ii = 0; ii < 5; ii++) {
            newTracks.add(new Track(generateRandomColourResource()));
        }
        trackList.addAll(0, newTracks);
        notifyObservers();
    }

    public void remove5Tracks() {
        logger.i(LOG_TAG, "remove5Tracks()");
        if (getItemCount() > 4) {
            trackList.removeRange(0, 5);
            notifyObservers();
        }
    }

    private void checkIndex(int index) {
        if (trackList.size() == 0) {
            throw new IndexOutOfBoundsException("tracklist has no items in it, can not get index:" + index);
        } else if (index < 0 || index > trackList.size() - 1) {
            throw new IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (trackList.size() - 1) + " not:" + index);
        }
    }

    @Override
    public Track getItem(int index) {
        checkIndex(index);
        return trackList.get(index);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    @Override
    public UpdateSpec getAndClearLatestUpdateSpec(long maxAgeMs) {
        return trackList.getAndClearLatestUpdateSpec(maxAgeMs);
    }
}
