package foo.bar.example.foreadapterskt.ui.playlist

import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import co.early.fore.adapters.UpdateSpec
import co.early.fore.core.WorkMode
import co.early.fore.core.time.SystemTimeWrapper
import foo.bar.example.foreadapterskt.App
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.Track
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot


/**
 *
 */
class StateBuilder internal constructor(private val mockUpdatablePlaylistModel: UpdatablePlaylistModel, private val mockDiffablePlaylistModel: DiffablePlaylistModel) {

    init {

        val updateSpec = UpdateSpec(UpdateSpec.UpdateType.FULL_UPDATE, 0, 0, mockk<SystemTimeWrapper>(relaxed = true))

        every {
            mockUpdatablePlaylistModel.getAndClearLatestUpdateSpec(any())
        } returns updateSpec

    }

    internal fun withUpdatablePlaylistHavingTracks(numberOfTracks: Int): StateBuilder {

        every {
            mockUpdatablePlaylistModel.trackListSize
        } returns numberOfTracks

        every {
            mockUpdatablePlaylistModel.isEmpty()
        } returns (numberOfTracks == 0)

        var slot = CapturingSlot<Int>()
        every {
            mockUpdatablePlaylistModel.hasAtLeastNItems(capture(slot))
        } answers { numberOfTracks >= slot.captured }

        return this
    }

    internal fun withDiffablePlaylistHavingTracks(numberOfTracks: Int): StateBuilder {

        every {
            mockDiffablePlaylistModel.size()
        } returns numberOfTracks

        every {
            mockDiffablePlaylistModel.isEmpty()
        } returns (numberOfTracks == 0)

        var slot = CapturingSlot<Int>()
        every {
            mockDiffablePlaylistModel.hasAtLeastNItems(capture(slot))
        } answers { numberOfTracks >= slot.captured }

        return this
    }

    internal fun withPlaylistsContainingTrack(track: Track): StateBuilder {

        every {
            mockUpdatablePlaylistModel.getTrack(any())
        } returns track

        every {
            mockDiffablePlaylistModel.getTrack(any())
        } returns track

        return this
    }

    internal fun createRule(): ActivityTestRule<PlaylistsActivity> {

        return object : ActivityTestRule<PlaylistsActivity>(PlaylistsActivity::class.java) {
            override fun beforeActivityLaunched() {

                //get hold of the application
                OG.setApplication(InstrumentationRegistry.getTargetContext().applicationContext as App, WorkMode.SYNCHRONOUS)

                //inject our mocks so our UI layer will pick them up
                OG.putMock(UpdatablePlaylistModel::class.java, mockUpdatablePlaylistModel)
                OG.putMock(DiffablePlaylistModel::class.java, mockDiffablePlaylistModel)
            }
        }
    }

}
