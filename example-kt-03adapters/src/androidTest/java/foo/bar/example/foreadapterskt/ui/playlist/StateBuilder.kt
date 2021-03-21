package foo.bar.example.foreadapterskt.ui.playlist

import androidx.test.rule.ActivityTestRule
import co.early.fore.adapters.mutable.UpdateSpec
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.Track
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk


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

                ForeDelegateHolder.setDelegate(TestDelegateDefault())

                //inject our mocks so our UI layer will pick them up
                OG.putMock(UpdatablePlaylistModel::class.java, mockUpdatablePlaylistModel)
                OG.putMock(DiffablePlaylistModel::class.java, mockDiffablePlaylistModel)
            }
        }
    }

}
