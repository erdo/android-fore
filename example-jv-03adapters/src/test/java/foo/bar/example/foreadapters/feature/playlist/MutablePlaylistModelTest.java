package foo.bar.example.foreadapters.feature.playlist;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.early.fore.adapters.mutable.UpdateSpec;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.time.SystemTimeWrapper;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class MutablePlaylistModelTest {


    private static Logger logger = new SystemLogger();
    private SystemTimeWrapper mockSystemTimeWrapper;


    @Before
    public void setUp() throws Exception {
        mockSystemTimeWrapper = mock(SystemTimeWrapper.class);
    }


    @Test
    public void initialConditions() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act

        //assert
        Assert.assertEquals(0, mutablePlaylistModel.getItemCount());
        Assert.assertEquals(false, mutablePlaylistModel.hasObservers());
        // 50ms won't have any effect here as the mocked systemTimeWrapper will always give the current
        // time as 0 anyway, so we will never have the chance to be over maxAgeMs
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, mutablePlaylistModel.getAndClearLatestUpdateSpec(50).type);
    }


    @Test
    public void addNewTrack() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act
        mutablePlaylistModel.addNewTrack();

        //assert
        Assert.assertEquals(1, mutablePlaylistModel.getItemCount());
        Assert.assertEquals(1, mutablePlaylistModel.getItem(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeTrack() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.addNewTrack();

        //act
        mutablePlaylistModel.removeTrack(0);

        //assert
        Assert.assertEquals(0, mutablePlaylistModel.getItemCount());
    }


    @Test
    public void add5NewTracks() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act
        mutablePlaylistModel.add5NewTracks();

        //assert
        Assert.assertEquals(5, mutablePlaylistModel.getItemCount());
        Assert.assertEquals(1, mutablePlaylistModel.getItem(4).getNumberOfPlaysRequested());
    }


    @Test
    public void remove5Tracks() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.add5NewTracks();

        //act
        mutablePlaylistModel.remove5Tracks();

        //assert
        Assert.assertEquals(0, mutablePlaylistModel.getItemCount());
    }


    @Test
    public void increasePlays() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.addNewTrack();

        //act
        mutablePlaylistModel.increasePlaysForTrack(0);

        //assert
        Assert.assertEquals(2, mutablePlaylistModel.getItem(0).getNumberOfPlaysRequested());
    }


    @Test
    public void decreasePlays() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.addNewTrack();
        mutablePlaylistModel.increasePlaysForTrack(0);

        //act
        mutablePlaylistModel.decreasePlaysForTrack(0);

        //assert
        Assert.assertEquals(1, mutablePlaylistModel.getItem(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeAllTracks() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.add5NewTracks();
        mutablePlaylistModel.addNewTrack();
        mutablePlaylistModel.addNewTrack();

        //act
        mutablePlaylistModel.removeAllTracks();

        //assert
        Assert.assertEquals(0, mutablePlaylistModel.getItemCount());
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
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        Observer mockObserver = mock(Observer.class);
        mutablePlaylistModel.addObserver(mockObserver);

        //act
        mutablePlaylistModel.addNewTrack();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


    @Test
    public void observersNotifiedAtLeastOnceForIncreasePlays() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.addNewTrack();
        Observer mockObserver = mock(Observer.class);
        mutablePlaylistModel.addObserver(mockObserver);

        //act
        mutablePlaylistModel.increasePlaysForTrack(0);

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
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);

        //act
        mutablePlaylistModel.addNewTrack();

        //assert
        UpdateSpec updateSpec = mutablePlaylistModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_INSERTED, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(1, updateSpec.rowsEffected);
    }


    @Test
    public void updateSpecCorrectForIncreasePlays() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.addNewTrack();
        mutablePlaylistModel.add5NewTracks();

        //act
        mutablePlaylistModel.increasePlaysForTrack(3);

        //assert
        UpdateSpec updateSpec = mutablePlaylistModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_CHANGED, updateSpec.type);
        Assert.assertEquals(3, updateSpec.rowPosition);
        Assert.assertEquals(1, updateSpec.rowsEffected);
    }


    @Test
    public void updateSpecCorrectForRemove5Tracks() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.add5NewTracks();
        mutablePlaylistModel.addNewTrack();

        //act
        mutablePlaylistModel.remove5Tracks();

        //assert
        UpdateSpec updateSpec = mutablePlaylistModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(5, updateSpec.rowsEffected);
    }

    @Test
    public void updateSpecCorrectForClearAllTracks() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.add5NewTracks();
        mutablePlaylistModel.addNewTrack();

        //act
        mutablePlaylistModel.removeAllTracks();

        //assert
        UpdateSpec updateSpec = mutablePlaylistModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(6, updateSpec.rowsEffected);
    }


    @Test
    public void updateSpecCorrectPastMaxAge() throws Exception {

        //arrange
        MutablePlaylistModel mutablePlaylistModel = new MutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
        mutablePlaylistModel.add5NewTracks();
        mutablePlaylistModel.addNewTrack();

        //act
        mutablePlaylistModel.increasePlaysForTrack(3);
        when(mockSystemTimeWrapper.currentTimeMillis()).thenReturn((long)51);

        //assert
        UpdateSpec updateSpec = mutablePlaylistModel.getAndClearLatestUpdateSpec(50);
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, updateSpec.type);
        Assert.assertEquals(0, updateSpec.rowPosition);
        Assert.assertEquals(0, updateSpec.rowsEffected);
    }


}
