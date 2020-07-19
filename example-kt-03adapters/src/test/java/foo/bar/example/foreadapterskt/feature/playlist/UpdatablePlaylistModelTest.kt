package foo.bar.example.foreadapterskt.feature.playlist

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import co.early.fore.adapters.UpdateSpec
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.core.time.SystemTimeWrapper
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.mockk

/**
 *
 */
class UpdatablePlaylistModelTest {

    private val logger = SystemLogger()

    @MockK
    private lateinit var mockSystemTimeWrapper: SystemTimeWrapper


    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)


    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)

        //act

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.trackListSize.toLong())
        Assert.assertEquals(false, playlistAdvancedModel.hasObservers())
        // 50ms won't have any effect here as the mocked systemTimeWrapper will always give the current
        // time as 0 anyway, so we will never have the chance to be over maxAgeMs
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, playlistAdvancedModel.getAndClearLatestUpdateSpec(50).type)
    }


    @Test
    @Throws(Exception::class)
    fun addNewTrack() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)

        //act
        playlistAdvancedModel.addNewTrack()

        //assert
        Assert.assertEquals(1, playlistAdvancedModel.trackListSize.toLong())
        Assert.assertEquals(1, playlistAdvancedModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeTrack() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.addNewTrack()

        //act
        playlistAdvancedModel.removeTrack(0)

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.trackListSize.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun add5NewTracks() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)

        //act
        playlistAdvancedModel.add5NewTracks()

        //assert
        Assert.assertEquals(5, playlistAdvancedModel.trackListSize.toLong())
        Assert.assertEquals(1, playlistAdvancedModel.getTrack(4).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun remove5Tracks() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.add5NewTracks()

        //act
        playlistAdvancedModel.remove5Tracks()

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.trackListSize.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun increasePlays() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.addNewTrack()

        //act
        playlistAdvancedModel.increasePlaysForTrack(0)

        //assert
        Assert.assertEquals(2, playlistAdvancedModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun decreasePlays() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.addNewTrack()
        playlistAdvancedModel.increasePlaysForTrack(0)

        //act
        playlistAdvancedModel.decreasePlaysForTrack(0)

        //assert
        Assert.assertEquals(1, playlistAdvancedModel.getTrack(0).numberOfPlaysRequested.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun removeAllTracks() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.add5NewTracks()
        playlistAdvancedModel.addNewTrack()
        playlistAdvancedModel.addNewTrack()

        //act
        playlistAdvancedModel.removeAllTracks()

        //assert
        Assert.assertEquals(0, playlistAdvancedModel.trackListSize.toLong())
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
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        val mockObserver: Observer = mockk(relaxed = true)
        playlistAdvancedModel.addObserver(mockObserver)

        //act
        playlistAdvancedModel.addNewTrack()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }


    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncreasePlays() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.addNewTrack()
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
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)

        //act
        playlistAdvancedModel.addNewTrack()

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_INSERTED, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(1, updateSpec.rowsEffected.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForIncreasePlays() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.addNewTrack()
        playlistAdvancedModel.add5NewTracks()

        //act
        playlistAdvancedModel.increasePlaysForTrack(3)

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_CHANGED, updateSpec.type)
        Assert.assertEquals(3, updateSpec.rowPosition.toLong())
        Assert.assertEquals(1, updateSpec.rowsEffected.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForRemove5Tracks() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.add5NewTracks()
        playlistAdvancedModel.addNewTrack()

        //act
        playlistAdvancedModel.remove5Tracks()

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(5, updateSpec.rowsEffected.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectForClearAllTracks() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.add5NewTracks()
        playlistAdvancedModel.addNewTrack()

        //act
        playlistAdvancedModel.removeAllTracks()

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50)
        Assert.assertEquals(UpdateSpec.UpdateType.ITEM_REMOVED, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(6, updateSpec.rowsEffected.toLong())
    }


    @Test
    @Throws(Exception::class)
    fun updateSpecCorrectPastMaxAge() {

        //arrange
        val playlistAdvancedModel = UpdatablePlaylistModel(mockSystemTimeWrapper, WorkMode.SYNCHRONOUS, logger)
        playlistAdvancedModel.add5NewTracks()
        playlistAdvancedModel.addNewTrack()

        //act
        playlistAdvancedModel.increasePlaysForTrack(3)

        every {
            mockSystemTimeWrapper.currentTimeMillis()
        } returns 51.toLong()

        //assert
        val updateSpec = playlistAdvancedModel.getAndClearLatestUpdateSpec(50)
        Assert.assertEquals(UpdateSpec.UpdateType.FULL_UPDATE, updateSpec.type)
        Assert.assertEquals(0, updateSpec.rowPosition.toLong())
        Assert.assertEquals(0, updateSpec.rowsEffected.toLong())
    }

}
