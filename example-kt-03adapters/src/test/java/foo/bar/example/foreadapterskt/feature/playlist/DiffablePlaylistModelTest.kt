package foo.bar.example.foreadapterskt.feature.playlist

import org.junit.Assert
import org.junit.Test

import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.core.time.SystemTimeWrapper
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before


/**
 *
 */
class DiffablePlaylistModelTest {

    @MockK
    private lateinit var mockSystemTimeWrapper: SystemTimeWrapper
    private val logger = SystemLogger()
    private lateinit var diffablePlaylistModel: DiffablePlaylistModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        diffablePlaylistModel = DiffablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
    }
    
    @Test
    @Throws(Exception::class)
    fun initialConditions() {
        
        //arrange
        
        //act

        //assert
        Assert.assertEquals(0, diffablePlaylistModel.size())
        Assert.assertEquals(false, diffablePlaylistModel.hasObservers())
    }


    @Test
    @Throws(Exception::class)
    fun addNewTrack() {
        
        //arrange

        //act
        diffablePlaylistModel.addNTracks(1)

        //assert
        Assert.assertEquals(1, diffablePlaylistModel.size())
        Assert.assertEquals(1, diffablePlaylistModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeTrack() {
        
        //arrange
        diffablePlaylistModel.addNTracks(1)

        //act
        diffablePlaylistModel.removeTrack(0)

        //assert
        Assert.assertEquals(0, diffablePlaylistModel.size())
    }


    @Test
    @Throws(Exception::class)
    fun add5NewTracks() {

        //arrange

        //act
        diffablePlaylistModel.addNTracks(5)

        //assert
        Assert.assertEquals(5, diffablePlaylistModel.size())
        Assert.assertEquals(1, diffablePlaylistModel.getTrack(4).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun remove5Tracks() {

        //arrange
        diffablePlaylistModel.addNTracks(5)

        //act
        diffablePlaylistModel.removeNTracks(5)

        //assert
        Assert.assertEquals(0, diffablePlaylistModel.size())
    }


    @Test
    @Throws(Exception::class)
    fun increasePlays() {

        //arrange
        diffablePlaylistModel.addNTracks(1)

        //act
        diffablePlaylistModel.increasePlaysForTrack(0)

        //assert
        Assert.assertEquals(2, diffablePlaylistModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun decreasePlays() {

        //arrange
        diffablePlaylistModel.addNTracks(1)
        diffablePlaylistModel.increasePlaysForTrack(0)

        //act
        diffablePlaylistModel.decreasePlaysForTrack(0)

        //assert
        Assert.assertEquals(1, diffablePlaylistModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeAllTracks() {

        //arrange
        diffablePlaylistModel.addNTracks(5)
        diffablePlaylistModel.addNTracks(1)
        diffablePlaylistModel.addNTracks(1)

        //act
        diffablePlaylistModel.removeAllTracks()

        //assert
        Assert.assertEquals(0, diffablePlaylistModel.size())
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
        val mockObserver: Observer = mockk(relaxed = true)
        diffablePlaylistModel.addObserver(mockObserver)

        //act
        diffablePlaylistModel.addNTracks(1)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }


    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncreasePlays() {

        //arrange
        diffablePlaylistModel.addNTracks(1)
        val mockObserver: Observer = mockk(relaxed = true)
        diffablePlaylistModel.addObserver(mockObserver)

        //act
        diffablePlaylistModel.increasePlaysForTrack(0)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

}
