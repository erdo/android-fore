package foo.bar.example.foreadapterskt.feature.playlist

import org.junit.Assert
import org.junit.Test

import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.foreadapterskt.feature.playlist.immutable.ImmutablePlaylistModel
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before


/**
 *
 */
class ImmutablePlaylistModelTest {

    private val logger = SystemLogger()
    private lateinit var immutablePlaylistModel: ImmutablePlaylistModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        ForeDelegateHolder.setDelegate(TestDelegateDefault())
        immutablePlaylistModel = ImmutablePlaylistModel(logger)
    }
    
    @Test
    @Throws(Exception::class)
    fun initialConditions() {
        
        //arrange
        
        //act

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount())
        Assert.assertEquals(false, immutablePlaylistModel.hasObservers())
    }


    @Test
    @Throws(Exception::class)
    fun addNewTrack() {
        
        //arrange

        //act
        immutablePlaylistModel.addNTracks(1)

        //assert
        Assert.assertEquals(1, immutablePlaylistModel.getItemCount())
        Assert.assertEquals(1, immutablePlaylistModel.getItem(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeTrack() {
        
        //arrange
        immutablePlaylistModel.addNTracks(1)

        //act
        immutablePlaylistModel.removeTrack(0)

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount())
    }


    @Test
    @Throws(Exception::class)
    fun add5NewTracks() {

        //arrange

        //act
        immutablePlaylistModel.addNTracks(5)

        //assert
        Assert.assertEquals(5, immutablePlaylistModel.getItemCount())
        Assert.assertEquals(1, immutablePlaylistModel.getItem(4).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun remove5Tracks() {

        //arrange
        immutablePlaylistModel.addNTracks(5)

        //act
        immutablePlaylistModel.removeNTracks(5)

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount())
    }


    @Test
    @Throws(Exception::class)
    fun increasePlays() {

        //arrange
        immutablePlaylistModel.addNTracks(1)

        //act
        immutablePlaylistModel.increasePlaysForTrack(0)

        //assert
        Assert.assertEquals(2, immutablePlaylistModel.getItem(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun decreasePlays() {

        //arrange
        immutablePlaylistModel.addNTracks(1)
        immutablePlaylistModel.increasePlaysForTrack(0)

        //act
        immutablePlaylistModel.decreasePlaysForTrack(0)

        //assert
        Assert.assertEquals(1, immutablePlaylistModel.getItem(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeAllTracks() {

        //arrange
        immutablePlaylistModel.addNTracks(5)
        immutablePlaylistModel.addNTracks(1)
        immutablePlaylistModel.addNTracks(1)

        //act
        immutablePlaylistModel.removeAllTracks()

        //assert
        Assert.assertEquals(0, immutablePlaylistModel.getItemCount())
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
        immutablePlaylistModel.addObserver(mockObserver)

        //act
        immutablePlaylistModel.addNTracks(1)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }


    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncreasePlays() {

        //arrange
        immutablePlaylistModel.addNTracks(1)
        val mockObserver: Observer = mockk(relaxed = true)
        immutablePlaylistModel.addObserver(mockObserver)

        //act
        immutablePlaylistModel.increasePlaysForTrack(0)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

}
