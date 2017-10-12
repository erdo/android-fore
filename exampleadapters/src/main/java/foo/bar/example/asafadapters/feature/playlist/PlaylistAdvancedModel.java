package foo.bar.example.asafadapters.feature.playlist;


import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.adapters.SimpleChangeAwareList;
import co.early.asaf.adapters.UpdateSpec;
import co.early.asaf.adapters.Updateable;

import static foo.bar.example.asafadapters.feature.playlist.RandomTrackGeneratorUtil.generateRandomColourResource;

/**
 *
 */
public class PlaylistAdvancedModel extends ObservableImp implements Updateable {

    private static String TAG = PlaylistAdvancedModel.class.getSimpleName();

    private final Logger logger;
    private SimpleChangeAwareList<Track> trackList = new SimpleChangeAwareList<>();


    public PlaylistAdvancedModel(WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.logger = Affirm.notNull(logger);
    }

    public void addNewTrack() {
        logger.i(TAG, "addNewTrack()");
        trackList.add(new Track(generateRandomColourResource()));
        notifyObservers();
    }

    public void removeTrack(int index) {
        logger.i(TAG, "removeTrack() " + index);
        checkIndex(index);
        trackList.remove(index);
        notifyObservers();
    }

    public void removeAllTracks() {
        logger.i(TAG, "removeAllTracks()");
        trackList.clear();
        notifyObservers();
    }

    public void increasePlaysForTrack(int index) {
        logger.i(TAG, "increasePlaysForTrack() " + index);
        getTrack(index).increasePlaysRequested();
        trackList.makeAwareOfDataChange(index);
        notifyObservers();
    }

    public void decreasePlaysForTrack(int index) {
        logger.i(TAG, "decreasePlaysForTrack() " + index);
        getTrack(index).decreasePlaysRequested();
        trackList.makeAwareOfDataChange(index);
        notifyObservers();
    }

    public Track getTrack(int index) {
        checkIndex(index);
        return trackList.get(index);
    }

    public int getTrackListSize() {
        return trackList.size();
    }

    private void checkIndex(int index) {
        if (trackList.size() == 0) {
            throw new IndexOutOfBoundsException("tracklist has no items in it, can not get index:" + index);
        } else if (index < 0 || index > trackList.size() - 1) {
            throw new IndexOutOfBoundsException("tracklist index needs to be between 0 and " + (trackList.size() - 1) + " not:" + index);
        }
    }

    @Override
    public UpdateSpec getAndClearMostRecentUpdateSpec() {
        return trackList.getAndClearMostRecentUpdateSpec();
    }
}