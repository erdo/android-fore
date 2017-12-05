package foo.bar.example.asafui.feature.fruitcollector;


import java.util.ArrayList;
import java.util.List;

import co.early.asaf.adapters.ChangeAwareArrayList;
import co.early.asaf.adapters.ChangeAwareList;
import co.early.asaf.adapters.UpdateSpec;
import co.early.asaf.adapters.Updateable;
import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.core.time.SystemTimeWrapper;

import static foo.bar.example.asafadapters.feature.playlist.RandomTrackGeneratorUtil.generateRandomColourResource;

/**
 *
 */
public class FruitCollectorModel extends ObservableImp implements Updateable {

    private static String TAG = FruitCollectorModel.class.getSimpleName();

    private final Logger logger;
    private ChangeAwareList<Track> trackList;


    public FruitCollectorModel(SystemTimeWrapper systemTimeWrapper, WorkMode workMode, Logger logger) {
        super(workMode, logger);
        this.logger = Affirm.notNull(logger);
        trackList = new ChangeAwareArrayList<>(Affirm.notNull(systemTimeWrapper));
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

    public void add5NewTracks() {
        logger.i(TAG, "add5NewTracks()");
        List<Track> newTracks = new ArrayList<>();
        for (int ii=0; ii<5; ii++){
            newTracks.add(new Track(generateRandomColourResource()));
        }
        trackList.addAll(0, newTracks);
        notifyObservers();
    }

    public void remove5Tracks() {
        logger.i(TAG, "remove5Tracks()");
        if (getTrackListSize()>4){
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
    public UpdateSpec getAndClearLatestUpdateSpec(long maxAgeMs) {
        return trackList.getAndClearLatestUpdateSpec(maxAgeMs);
    }

}