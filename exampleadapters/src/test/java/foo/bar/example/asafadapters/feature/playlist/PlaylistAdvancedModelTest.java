package foo.bar.example.asafadapters.feature.playlist;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.early.asaf.adapters.UpdateSpec;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.logging.SystemLogger;
import co.early.asaf.core.observer.Observer;
import co.early.asaf.core.time.SystemTimeWrapper;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class PlaylistAdvancedModelTest {


    private static Logger logger = new SystemLogger();
    private SystemTimeWrapper mockSystemTimeWrapper;


    @Before
    public void setUp() throws Exception {
        mockSystemTimeWrapper = mock(SystemTimeWrapper.class);
    }


    @Test
    public void initialConditions() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getTrackListSize());
        Assert.assertEquals(false, playlistAdvancedModel.hasObservers());
        // 50ms won't have any effect here as the mocked systemTimeWrapper will always give the current
        // time as 0 anyway, so we will never have the chance to be over maxAgeMs
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, playlistAdvancedModel.getAndClearLatestUpdateSpec(50).type);
    }


    @Test
    public void addNewTrack() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act
        playlistAdvancedModel.addNewTrack();

        //assert
        Assert.assertEquals(1, playlistAdvancedModel.getTrackListSize());
        Assert.assertEquals(1, playlistAdvancedModel.getTrack(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeTrack() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.addNewTrack();

        //act
        playlistAdvancedModel.removeTrack(0);

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getTrackListSize());
    }


    @Test
    public void add5NewTracks() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act
        playlistAdvancedModel.add5NewTracks();

        //assert
        Assert.assertEquals(5, playlistAdvancedModel.getTrackListSize());
        Assert.assertEquals(1, playlistAdvancedModel.getTrack(4).getNumberOfPlaysRequested());
    }


    @Test
    public void remove5Tracks() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.add5NewTracks();

        //act
        playlistAdvancedModel.remove5Tracks();

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getTrackListSize());
    }


    @Test
    public void increasePlays() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.addNewTrack();

        //act
        playlistAdvancedModel.increasePlaysForTrack(0);

        //assert
        Assert.assertEquals(2, playlistAdvancedModel.getTrack(0).getNumberOfPlaysRequested());
    }


    @Test
    public void decreasePlays() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.addNewTrack();
        playlistAdvancedModel.increasePlaysForTrack(0);

        //act
        playlistAdvancedModel.decreasePlaysForTrack(0);

        //assert
        Assert.assertEquals(1, playlistAdvancedModel.getTrack(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeAllTracks() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.add5NewTracks();
        playlistAdvancedModel.addNewTrack();
        playlistAdvancedModel.addNewTrack();

        //act
        playlistAdvancedModel.removeAllTracks();

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getTrackListSize());
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
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        Observer mockObserver = mock(Observer.class);
        playlistAdvancedModel.addObserver(mockObserver);

        //act
        playlistAdvancedModel.addNewTrack();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


    @Test
    public void observersNotifiedAtLeastOnceForIncreasePlays() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.addNewTrack();
        Observer mockObserver = mock(Observer.class);
        playlistAdvancedModel.addObserver(mockObserver);

        //act
        playlistAdvancedModel.increasePlaysForTrack(0);

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


    /**
     *
     * Here we make some tests to verify the UpdateSpec returned is correct so that we
     * know that we can correctly drive a ChangeAwareAdapter with this model
     *
     * @throws Exception
     */
    @Test
    public void updateSpecCorrectForAddTrack() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act
        playlistAdvancedModel.addNewTrack();

        //assert
        UpdateSpec updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_INSERTED, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(1, updateSpec.rowsEffected);
    }


    @Test
    public void updateSpecCorrectForIncreasePlays() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.addNewTrack();
        playlistAdvancedModel.add5NewTracks();

        //act
        playlistAdvancedModel.increasePlaysForTrack(3);

        //assert
        UpdateSpec updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_CHANGED, updateSpec.type);
        Assert.assertEquals(3, updateSpec.rowPosition);
        Assert.assertEquals(1, updateSpec.rowsEffected);
    }


    @Test
    public void updateSpecCorrectForRemove5Tracks() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.add5NewTracks();
        playlistAdvancedModel.addNewTrack();

        //act
        playlistAdvancedModel.remove5Tracks();

        //assert
        UpdateSpec updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(5, updateSpec.rowsEffected);
    }

    @Test
    public void updateSpecCorrectForClearAllTracks() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.add5NewTracks();
        playlistAdvancedModel.addNewTrack();

        //act
        playlistAdvancedModel.removeAllTracks();

        //assert
        UpdateSpec updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(6, updateSpec.rowsEffected);
    }


    @Test
    public void updateSpecCorrectPastMaxAge() throws Exception {

        //arrange
        PlaylistAdvancedModel playlistAdvancedModel = new PlaylistAdvancedModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        playlistAdvancedModel.add5NewTracks();
        playlistAdvancedModel.addNewTrack();

        //act
        playlistAdvancedModel.increasePlaysForTrack(3);
        when(mockSystemTimeWrapper.currentTimeMillis()).thenReturn((long)51);

        //assert
        UpdateSpec updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(0, updateSpec.rowsEffected);
    }


}
