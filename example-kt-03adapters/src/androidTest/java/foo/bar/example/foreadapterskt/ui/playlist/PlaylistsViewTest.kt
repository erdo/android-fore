package foo.bar.example.foreadapterskt.ui.playlist

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.Track
import foo.bar.example.foreadapterskt.ui.EspressoTestMatchers
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */
@RunWith(AndroidJUnit4::class)
class PlaylistsViewTest {


    @MockK
    private lateinit var mockDiffablePlaylistModel: DiffablePlaylistModel
    @MockK
    private lateinit var mockUpdatablePlaylistModel: UpdatablePlaylistModel


    @Before
    fun setUp() = MockKAnnotations.init(this, relaxed = true)


    @Test
    @Throws(Exception::class)
    fun emptyPlaylists() {

        //arrange
        StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withUpdatablePlaylistHavingTracks(0)
            .withDiffablePlaylistHavingTracks(0)
            .createRule()
            .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.updatable_add_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_remove5_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.updatable_clear_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.updatable_totaltracks_textview)).check(matches(withText("0")))
        
        onView(withId(R.id.diffable_add_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_remove5_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.diffable_clear_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.diffable_totaltracks_textview)).check(matches(withText("0")))

        onView(withId(R.id.updatable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(0)))
        onView(withId(R.id.diffable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(0)))

    }


    @Test
    @Throws(Exception::class)
    fun with3ItemsInEachPlaylist() {

        //arrange
        StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withUpdatablePlaylistHavingTracks(3)
            .withDiffablePlaylistHavingTracks(3)
            .withPlaylistsContainingTrack(Track(R.color.pastel1, 123))
            .createRule()
            .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.updatable_add_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_add_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_clear_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_clear_button)).check(matches(isEnabled()))

        onView(withId(R.id.updatable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_remove5_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.diffable_remove5_button)).check(matches(not<View>(isEnabled())))

        onView(withId(R.id.updatable_totaltracks_textview)).check(matches(withText("3")))
        onView(withId(R.id.diffable_totaltracks_textview)).check(matches(withText("3")))

        onView(withId(R.id.updatable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(3)))
        onView(withId(R.id.diffable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(3)))

    }


    @Test
    @Throws(Exception::class)
    fun with5ItemsInEachPlaylist() {

        //arrange
        StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withUpdatablePlaylistHavingTracks(5)
            .withDiffablePlaylistHavingTracks(5)
            .withPlaylistsContainingTrack(Track(R.color.pastel1, 123))
            .createRule()
            .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.updatable_add_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_add_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_clear_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_clear_button)).check(matches(isEnabled()))

        onView(withId(R.id.updatable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_remove5_button)).check(matches(isEnabled()))
        onView(withId(R.id.diffable_remove5_button)).check(matches(isEnabled()))

        onView(withId(R.id.updatable_totaltracks_textview)).check(matches(withText("5")))
        onView(withId(R.id.diffable_totaltracks_textview)).check(matches(withText("5")))

        onView(withId(R.id.updatable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(5)))
        onView(withId(R.id.diffable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(5)))

    }


    @Test
    @Throws(Exception::class)
    fun withDifferentItemsInEachPlaylist() {

        //arrange
        StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withDiffablePlaylistHavingTracks(0)
            .withUpdatablePlaylistHavingTracks(5)
            .withPlaylistsContainingTrack(Track(R.color.pastel1, 123))
            .createRule()
            .launchActivity(null)

        //act

        //assert
        onView(withId(R.id.diffable_clear_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.updatable_clear_button)).check(matches(isEnabled()))

        onView(withId(R.id.diffable_remove5_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.updatable_remove5_button)).check(matches(isEnabled()))

        onView(withId(R.id.diffable_totaltracks_textview)).check(matches(withText("0")))
        onView(withId(R.id.updatable_totaltracks_textview)).check(matches(withText("5")))

        onView(withId(R.id.diffable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(0)))
        onView(withId(R.id.updatable_list_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(5)))

    }


    @Test
    @Throws(Exception::class)
    fun stateMaintainedAfterRotation() {

        //arrange
        val activity = StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withUpdatablePlaylistHavingTracks(3)
            .withDiffablePlaylistHavingTracks(3)
            .withPlaylistsContainingTrack(Track(R.color.pastel1, 123))
            .createRule()
            .launchActivity(null)
        activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        onView(withId(R.id.updatable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_remove5_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.updatable_totaltracks_textview)).check(matches(withText("3")))

        //act
        activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        //assert
        onView(withId(R.id.updatable_add5_button)).check(matches(isEnabled()))
        onView(withId(R.id.updatable_remove5_button)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.updatable_totaltracks_textview)).check(matches(withText("3")))
    }


    @Test
    @Throws(Exception::class)
    fun clickAddTrackDiffableCallsModel() {
        //arrange
        StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withUpdatablePlaylistHavingTracks(0)
            .withDiffablePlaylistHavingTracks(0)
            .createRule()
            .launchActivity(null)

        //act
        onView(withId(R.id.diffable_add_button)).perform(click())

        //assert
        verify(exactly = 1) {
            mockDiffablePlaylistModel.addNTracks(1)
        }
    }


    @Test
    @Throws(Exception::class)
    fun clickAddTrackUpdatableCallsModel() {
        //arrange
        StateBuilder(mockUpdatablePlaylistModel, mockDiffablePlaylistModel)
            .withUpdatablePlaylistHavingTracks(0)
            .withDiffablePlaylistHavingTracks(0)
            .createRule()
            .launchActivity(null)

        //act
        onView(withId(R.id.updatable_add_button)).perform(click())

        //assert
        verify(exactly = 1) {
            mockUpdatablePlaylistModel.addNTracks(1)
        }
    }

}
