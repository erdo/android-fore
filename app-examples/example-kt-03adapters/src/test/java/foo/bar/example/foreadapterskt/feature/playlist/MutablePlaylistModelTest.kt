package foo.bar.example.foreadapterskt.feature.playlist

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import co.early.fore.adapters.mutable.UpdateSpec
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.mockk

/**
 *
 */
class MutablePlaylistModelTest {

    private val logger = SystemLogger()
    private lateinit var playlistAdvancedModel: MutablePlaylistModel

    @MockK
    private lateinit var mockSystemTimeWrapper: SystemTimeWrapper

    private val RELAXED_AGE_LIMIT_FOR_UPDATE_SPEC: Long = 500
    private val ACTUAL_AGE_LIMIT_FOR_UPDATE_SPEC: Long = 50

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        Fore.setDelegate(TestDelegateDefault())

        playlistAdvancedModel = MutablePlaylistModel(logger)
    }


    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange

        //act

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getItemCount().toLong())
        Assert.assertEquals(false, playlistAdvancedModel.hasObservers())
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, playlistAdvancedModel.getAndClearLatestUpdateSpec(50).type)
    }


    @Test
    @Throws(Exception::class)
    fun addNewTrack() {

        //arrange

        //act
        playlistAdvancedModel.addNTracks(1)

        //assert
        Assert.assertEquals(1, playlistAdvancedModel.getItemCount().toLong())
        Assert.assertEquals(1, playlistAdvancedModel.getItem(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeTrack() {

        //arrange
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.removeTrack(0)

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getItemCount().toLong())
    }


    @Test
    @Throws(Exception::class)
    fun add5NewTracks() {

        //arrange

        //act
        playlistAdvancedModel.addNTracks(5)

        //assert
        Assert.assertEquals(5, playlistAdvancedModel.getItemCount().toLong())
        Assert.assertEquals(1, playlistAdvancedModel.getItem(4).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun remove5Tracks() {

        //arrange
        playlistAdvancedModel.addNTracks(5)

        //act
        playlistAdvancedModel.removeNTracks(5)

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getItemCount().toLong())
    }


    @Test
    @Throws(Exception::class)
    fun increasePlays() {

        //arrange
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.increasePlaysForTrack(0)

        //assert
        Assert.assertEquals(2, playlistAdvancedModel.getItem(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun decreasePlays() {

        //arrange
        playlistAdvancedModel.addNTracks(1)
        playlistAdvancedModel.increasePlaysForTrack(0)

        //act
        playlistAdvancedModel.decreasePlaysForTrack(0)

        //assert
        Assert.assertEquals(1, playlistAdvancedModel.getItem(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeAllTracks() {

        //arrange
        playlistAdvancedModel.addNTracks(5)
        playlistAdvancedModel.addNTracks(1)
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.removeAllTracks()

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.getItemCount().toLong())
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
        playlistAdvancedModel.addObserver(mockObserver)

        //act
        playlistAdvancedModel.addNTracks(1)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }


    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncreasePlays() {

        //arrange
        playlistAdvancedModel.addNTracks(1)
        val mockObserver: Observer = mockk(relaxed = true)
        playlistAdvancedModel.addObserver(mockObserver)

        //act
        playlistAdvancedModel.increasePlaysForTrack(0)

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }


    /**
     *
     * Here we make some tests to verify the UpdateSpec returned is correct so that we
     * know that we can correctly drive a ChangeAwareAdapter with this model
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForAddTrack() {

        //arrange

        //act
        playlistAdvancedModel.addNTracks(1)

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(RELAXED_AGE_LIMIT_FOR_UPDATE_SPEC)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_INSERTED, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(1, updateSpec.rowsEffected.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForIncreasePlays() {

        //arrange
        playlistAdvancedModel.addNTracks(1)
        playlistAdvancedModel.addNTracks(5)

        //act
        playlistAdvancedModel.increasePlaysForTrack(3)

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(RELAXED_AGE_LIMIT_FOR_UPDATE_SPEC)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_CHANGED, updateSpec.type)
        Assert.assertEquals(3, updateSpec.rowPosition.toLong())
        Assert.assertEquals(1, updateSpec.rowsEffected.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForRemove5Tracks() {

        //arrange
        playlistAdvancedModel.addNTracks(5)
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.removeNTracks(5)

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(RELAXED_AGE_LIMIT_FOR_UPDATE_SPEC)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(5, updateSpec.rowsEffected.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForClearAllTracks() {

        //arrange
        playlistAdvancedModel.addNTracks(5)
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.removeAllTracks()

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(RELAXED_AGE_LIMIT_FOR_UPDATE_SPEC)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(6, updateSpec.rowsEffected.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectWithinMaxAge() {

        //arrange
        Fore.setDelegate(TestDelegateDefault(systemTimeWrapper = mockSystemTimeWrapper))
        playlistAdvancedModel.addNTracks(5)
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.increasePlaysForTrack(3)

        every {
            mockSystemTimeWrapper.currentTimeMillis()
        } returns (ACTUAL_AGE_LIMIT_FOR_UPDATE_SPEC - 1)

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(ACTUAL_AGE_LIMIT_FOR_UPDATE_SPEC)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_CHANGED, updateSpec.type)
        Assert.assertEquals(3, updateSpec.rowPosition.toLong())
        Assert.assertEquals(1, updateSpec.rowsEffected.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectPastMaxAge() {

        //arrange
        Fore.setDelegate(TestDelegateDefault(systemTimeWrapper = mockSystemTimeWrapper))
        playlistAdvancedModel.addNTracks(5)
        playlistAdvancedModel.addNTracks(1)

        //act
        playlistAdvancedModel.increasePlaysForTrack(3)

        every {
            mockSystemTimeWrapper.currentTimeMillis()
        } returns (ACTUAL_AGE_LIMIT_FOR_UPDATE_SPEC + 1)

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(ACTUAL_AGE_LIMIT_FOR_UPDATE_SPEC)
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(0, updateSpec.rowsEffected.toLong())
    }

}
