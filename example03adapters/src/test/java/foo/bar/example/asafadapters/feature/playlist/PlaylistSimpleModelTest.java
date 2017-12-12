package foo.bar.example.asafadapters.feature.playlist;

import org.junit.Assert;
import org.junit.Test;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.logging.SystemLogger;
import co.early.asaf.core.observer.Observer;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class PlaylistSimpleModelTest {


    private static Logger logger = new SystemLogger();
    

    @Test
    public void initialConditions() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);

        //act

        //assert
        Assert.assertEquals(0, playlistSimpleModel.getTrackListSize());
        Assert.assertEquals(false, playlistSimpleModel.hasObservers());
    }


    @Test
    public void addNewTrack() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);

        //act
        playlistSimpleModel.addNewTrack();

        //assert
        Assert.assertEquals(1, playlistSimpleModel.getTrackListSize());
        Assert.assertEquals(1, playlistSimpleModel.getTrack(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeTrack() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        playlistSimpleModel.addNewTrack();

        //act
        playlistSimpleModel.removeTrack(0);

        //assert
        Assert.assertEquals(0, playlistSimpleModel.getTrackListSize());
    }


    @Test
    public void add5NewTracks() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);

        //act
        playlistSimpleModel.add5NewTracks();

        //assert
        Assert.assertEquals(5, playlistSimpleModel.getTrackListSize());
        Assert.assertEquals(1, playlistSimpleModel.getTrack(4).getNumberOfPlaysRequested());
    }


    @Test
    public void remove5Tracks() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        playlistSimpleModel.add5NewTracks();

        //act
        playlistSimpleModel.remove5Tracks();

        //assert
        Assert.assertEquals(0, playlistSimpleModel.getTrackListSize());
    }


    @Test
    public void increasePlays() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        playlistSimpleModel.addNewTrack();

        //act
        playlistSimpleModel.increasePlaysForTrack(0);

        //assert
        Assert.assertEquals(2, playlistSimpleModel.getTrack(0).getNumberOfPlaysRequested());
    }


    @Test
    public void decreasePlays() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        playlistSimpleModel.addNewTrack();
        playlistSimpleModel.increasePlaysForTrack(0);

        //act
        playlistSimpleModel.decreasePlaysForTrack(0);

        //assert
        Assert.assertEquals(1, playlistSimpleModel.getTrack(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeAllTracks() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        playlistSimpleModel.add5NewTracks();
        playlistSimpleModel.addNewTrack();
        playlistSimpleModel.addNewTrack();

        //act
        playlistSimpleModel.removeAllTracks();

        //assert
        Assert.assertEquals(0, playlistSimpleModel.getTrackListSize());
    }


    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the databinding readme for more information about this
     *
     * @throws Exception
     */
    @Test
    public void observersNotifiedAtLeastOnceForAddTrack() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        Observer mockObserver = mock(Observer.class);
        playlistSimpleModel.addObserver(mockObserver);

        //act
        playlistSimpleModel.addNewTrack();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


    @Test
    public void observersNotifiedAtLeastOnceForIncreasePlays() throws Exception {

        //arrange
        PlaylistSimpleModel playlistSimpleModel = new PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger);
        playlistSimpleModel.addNewTrack();
        Observer mockObserver = mock(Observer.class);
        playlistSimpleModel.addObserver(mockObserver);

        //act
        playlistSimpleModel.increasePlaysForTrack(0);

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }

}
