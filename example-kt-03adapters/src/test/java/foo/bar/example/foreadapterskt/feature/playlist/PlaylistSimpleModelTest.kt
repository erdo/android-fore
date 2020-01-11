package foo.bar.example.foreadapterskt.feature.playlist

import org.junit.Assert
import org.junit.Test

import co.early.fore.core.WorkMode
import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import io.mockk.mockk
import io.mockk.verify


/**
 *
 */
class PlaylistSimpleModelTest {

    private val logger = SystemLogger()

    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)

        //act

        //assert
        Assert.assertEquals(0, playlistSimpleModel.trackListSize.toLong())
        Assert.assertEquals(false, playlistSimpleModel.hasObservers())
    }


    @Test
    @Throws(Exception::class)
    fun addNewTrack() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)

        //act
        playlistSimpleModel.addNewTrack()

        //assert
        Assert.assertEquals(1, playlistSimpleModel.trackListSize.toLong())
        Assert.assertEquals(1, playlistSimpleModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeTrack() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        playlistSimpleModel.addNewTrack()

        //act
        playlistSimpleModel.removeTrack(0)

        //assert
        Assert.assertEquals(0, playlistSimpleModel.trackListSize.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun add5NewTracks() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)

        //act
        playlistSimpleModel.add5NewTracks()

        //assert
        Assert.assertEquals(5, playlistSimpleModel.trackListSize.toLong())
        Assert.assertEquals(1, playlistSimpleModel.getTrack(4).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun remove5Tracks() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        playlistSimpleModel.add5NewTracks()

        //act
        playlistSimpleModel.remove5Tracks()

        //assert
        Assert.assertEquals(0, playlistSimpleModel.trackListSize.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun increasePlays() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        playlistSimpleModel.addNewTrack()

        //act
        playlistSimpleModel.increasePlaysForTrack(0)

        //assert
        Assert.assertEquals(2, playlistSimpleModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun decreasePlays() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        playlistSimpleModel.addNewTrack()
        playlistSimpleModel.increasePlaysForTrack(0)

        //act
        playlistSimpleModel.decreasePlaysForTrack(0)

        //assert
        Assert.assertEquals(1, playlistSimpleModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeAllTracks() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        playlistSimpleModel.add5NewTracks()
        playlistSimpleModel.addNewTrack()
        playlistSimpleModel.addNewTrack()

        //act
        playlistSimpleModel.removeAllTracks()

        //assert
        Assert.assertEquals(0, playlistSimpleModel.trackListSize.toLong())
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
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForAddTrack() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        val mockObserver: Observer = mockk(relaxed = true)
        playlistSimpleModel.addObserver(mockObserver)

        //act
        playlistSimpleModel.addNewTrack()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }


    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncreasePlays() {

        //arrange
        val playlistSimpleModel = PlaylistSimpleModel(WorkMode.SYNCHRONOUS, logger)
        playlistSimpleModel.addNewTrack()
        val mockObserver: Observer = mockk(relaxed = true)
        playlistSimpleModel.addObserver(mockObserver)

        //act
        playlistSimpleModel.increasePlaysForTrack(0)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

}
