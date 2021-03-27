package foo.bar.example.foreadapters.feature.playlist;

import java.util.ArrayList;
import java.util.List;

import co.early.fore.adapters.immutable.ImmutableListMgr;
import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.time.SystemTimeWrapper;

import static foo.bar.example.foreadapters.feature.playlist.RandomTrackGeneratorUtil.generateRandomColourResource;

/**
 * Example model based on **immutable** list data
 *
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
public class ImmutablePlaylistModel extends ImmutableListMgr<Track> {

    private static final String LOG_TAG = ImmutablePlaylistModel.class.getSimpleName();

    private final Logger logger;

    public ImmutablePlaylistModel(SystemTimeWrapper systemTimeWrapper, WorkMode workMode, Logger logger) {
        super(systemTimeWrapper, workMode, logger);
        this.logger = Affirm.notNull(logger);
    }

    public void addNewTrack() {
        logger.i(LOG_TAG, "addNewTrack()");
        changeList(listCopy -> listCopy.add(new Track(generateRandomColourResource())));
    }

    public void removeTrack(int index) {
        logger.i(LOG_TAG, "removeTrack() " + index);
        checkIndex(index);
        changeList(listCopy -> listCopy.remove(index));
    }

    public void removeAllTracks() {
        logger.i(LOG_TAG, "removeAllTracks()");
        changeList(listCopy -> listCopy.clear());
    }

    public void increasePlaysForTrack(int index) {
        logger.i(LOG_TAG, "increasePlaysForTrack() " + index);
        checkIndex(index);
        changeList(listCopy -> listCopy.get(index).increasePlaysRequested());
    }

    public void decreasePlaysForTrack(int index) {
        logger.i(LOG_TAG, "decreasePlaysForTrack() " + index);
        checkIndex(index);
        changeList(listCopy -> listCopy.get(index).decreasePlaysRequested());
    }

    public void add5NewTracks() {
        logger.i(LOG_TAG, "add5NewTracks()");
        List<Track> newTracks = new ArrayList<>();
        for (int ii = 0; ii < 5; ii++) {
            newTracks.add(new Track(generateRandomColourResource()));
        }
        changeList(listCopy -> listCopy.addAll(0, newTracks));
    }

    public void remove5Tracks() {
        logger.i(LOG_TAG, "remove5Tracks()");
        if (getItemCount() > 4) {
            changeList(listCopy -> listCopy.subList(0, 5).clear());
        }
    }

    public void replaceTracks(List<Track> newTrackList) {
        logger.i(LOG_TAG, "replaceTracks()");
        if (getItemCount() > 4) {
            replaceList(() -> newTrackList);
        }
    }

    private void checkIndex(int index) {
        if (getItemCount() == 0) {
            throw new IndexOutOfBoundsException("tracklist has no items in it, can not get index:" + index);
        } else if (index < 0 || index > getItemCount() - 1) {
            throw new IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (getItemCount() - 1) + " not:" + index);
        }
    }
}
