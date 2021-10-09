package foo.bar.example.foreadapterskt.ui.playlist

import androidx.test.rule.ActivityTestRule
import co.early.fore.adapters.mutable.UpdateSpec
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.immutable.ImmutablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.Track
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk


/**
 *
 */
class StateBuilder internal constructor(private val mockMutablePlaylistModel: MutablePlaylistModel, private val mockImmutablePlaylistModel: ImmutablePlaylistModel) {

    init {

        val updateSpec = UpdateSpec(UpdateSpec.UpdateType.FULL_UPDATE, 0, 0, mockk<SystemTimeWrapper>(relaxed = true))

        every {
            mockMutablePlaylistModel.getAndClearLatestUpdateSpec(any())
        } returns updateSpec

    }

    internal fun withUpdatablePlaylistHavingTracks(numberOfTracks: Int): StateBuilder {

        every {
            mockMutablePlaylistModel.itemCount
        } returns numberOfTracks

        every {
            mockMutablePlaylistModel.isEmpty()
        } returns (numberOfTracks == 0)

        var slot = CapturingSlot<Int>()
        every {
            mockMutablePlaylistModel.hasAtLeastNItems(capture(slot))
        } answers { numberOfTracks >= slot.captured }

        return this
    }

    internal fun withDiffablePlaylistHavingTracks(numberOfTracks: Int): StateBuilder {

        every {
            mockImmutablePlaylistModel.getItemCount()
        } returns numberOfTracks

        every {
            mockImmutablePlaylistModel.isEmpty()
        } returns (numberOfTracks == 0)

        var slot = CapturingSlot<Int>()
        every {
            mockImmutablePlaylistModel.hasAtLeastNItems(capture(slot))
        } answers { numberOfTracks >= slot.captured }

        return this
    }

    internal fun withPlaylistsContainingTrack(track: Track): StateBuilder {

        every {
            mockMutablePlaylistModel.getItem(any())
        } returns track

        every {
            mockImmutablePlaylistModel.getItem(any())
        } returns track

        return this
    }

    internal fun createRule(): ActivityTestRule<PlaylistsActivity> {

        return object : ActivityTestRule<PlaylistsActivity>(PlaylistsActivity::class.java) {
            override fun beforeActivityLaunched() {

                Fore.setDelegate(TestDelegateDefault())

                //inject our mocks so our UI layer will pick them up
                OG.putMock(MutablePlaylistModel::class.java, mockMutablePlaylistModel)
                OG.putMock(ImmutablePlaylistModel::class.java, mockImmutablePlaylistModel)
            }
        }
    }

}
