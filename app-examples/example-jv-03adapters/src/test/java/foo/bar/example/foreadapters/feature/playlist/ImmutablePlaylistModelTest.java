package foo.bar.example.foreadapters.feature.playlist;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.time.SystemTimeWrapper;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class ImmutablePlaylistModelTest {


    private static Logger logger = new SystemLogger();
    private SystemTimeWrapper mockSystemTimeWrapper;
    private ImmutablePlaylistModel immutablePlaylistModel;


    @Before
    public void setUp() throws Exception {
        mockSystemTimeWrapper = mock(SystemTimeWrapper.class);
        immutablePlaylistModel = new ImmutablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger);
    }


    @Test
    public void initialConditions() throws Exception {

        //arrange

        //act

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount());
        Assert.assertEquals(false, immutablePlaylistModel.hasObservers());
    }


    @Test
    public void addNewTrack() throws Exception {

        //arrange

        //act
        immutablePlaylistModel.addNewTrack();

        //assert
        Assert.assertEquals(1, immutablePlaylistModel.getItemCount());
        Assert.assertEquals(1, immutablePlaylistModel.getItem(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeTrack() throws Exception {

        //arrange
        immutablePlaylistModel.addNewTrack();

        //act
        immutablePlaylistModel.removeTrack(0);

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount());
    }


    @Test
    public void add5NewTracks() throws Exception {

        //arrange

        //act
        immutablePlaylistModel.add5NewTracks();

        //assert
        Assert.assertEquals(5, immutablePlaylistModel.getItemCount());
        Assert.assertEquals(1, immutablePlaylistModel.getItem(4).getNumberOfPlaysRequested());
    }


    @Test
    public void remove5Tracks() throws Exception {

        //arrange
        immutablePlaylistModel.add5NewTracks();

        //act
        immutablePlaylistModel.remove5Tracks();

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount());
    }


    @Test
    public void increasePlays() throws Exception {

        //arrange
        immutablePlaylistModel.addNewTrack();

        //act
        immutablePlaylistModel.increasePlaysForTrack(0);

        //assert
        Assert.assertEquals(2, immutablePlaylistModel.getItem(0).getNumberOfPlaysRequested());
    }


    @Test
    public void decreasePlays() throws Exception {

        //arrange
        immutablePlaylistModel.addNewTrack();
        immutablePlaylistModel.increasePlaysForTrack(0);

        //act
        immutablePlaylistModel.decreasePlaysForTrack(0);

        //assert
        Assert.assertEquals(1, immutablePlaylistModel.getItem(0).getNumberOfPlaysRequested());
    }


    @Test
    public void removeAllTracks() throws Exception {

        //arrange
        immutablePlaylistModel.add5NewTracks();
        immutablePlaylistModel.addNewTrack();
        immutablePlaylistModel.addNewTrack();

        //act
        immutablePlaylistModel.removeAllTracks();

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount());
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
        Observer mockObserver = mock(Observer.class);
        immutablePlaylistModel.addObserver(mockObserver);

        //act
        immutablePlaylistModel.addNewTrack();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }


    @Test
    public void observersNotifiedAtLeastOnceForIncreasePlays() throws Exception {

        //arrange
        immutablePlaylistModel.addNewTrack();
        Observer mockObserver = mock(Observer.class);
        immutablePlaylistModel.addObserver(mockObserver);

        //act
        immutablePlaylistModel.increasePlaysForTrack(0);

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }
}
