package foo.bar.example.foreadapters.ui.playlist;

import android.app.Activity;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import foo.bar.example.foreadapters.R;
import foo.bar.example.foreadapters.feature.playlist.ImmutablePlaylistModel;
import foo.bar.example.foreadapters.feature.playlist.MutablePlaylistModel;
import foo.bar.example.foreadapters.feature.playlist.Track;
import foo.bar.example.foreadapters.ui.EspressoTestMatchers;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Here we make sure that the view elements accurately reflect the state of the models
 * and that clicking the buttons results in the correct action being performed
 */
@RunWith(AndroidJUnit4.class)
public class PlaylistsViewTest {


    private ImmutablePlaylistModel mockImmutablePlaylistModel;
    private MutablePlaylistModel mockMutablePlaylistModel;


    @Before
    public void setup(){

        //MockitoAnnotations.initMocks(WalletView.this);
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        mockMutablePlaylistModel = mock(MutablePlaylistModel.class);
        mockImmutablePlaylistModel = mock(ImmutablePlaylistModel.class);
    }


    @Test
    public void emptyPlaylists() throws Exception {

        //arrange
        new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withMutablePlaylistHavingTracks(0)
                .withImmutablePlaylistHavingTracks(0)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.playlist_add1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_add2_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_clear1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_clear2_button)).check(matches(not(isEnabled())));

        onView(withId(R.id.playlist_addMany1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_addMany2_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_removeMany1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_removeMany2_button)).check(matches(not(isEnabled())));

        onView(withId(R.id.playlist_totaltracks1_textview)).check(matches(withText("[0]")));
        onView(withId(R.id.playlist_totaltracks2_textview)).check(matches(withText("[0]")));

        onView(withId(R.id.playlist_list1_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(0)));
        onView(withId(R.id.playlist_list2_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(0)));

    }


    @Test
    public void with3ItemsInEachPlaylist() throws Exception {

        //arrange
        new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withMutablePlaylistHavingTracks(3)
                .withImmutablePlaylistHavingTracks(3)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.playlist_add1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_add2_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_clear1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_clear2_button)).check(matches(isEnabled()));

        onView(withId(R.id.playlist_addMany1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_addMany2_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_removeMany1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_removeMany2_button)).check(matches(not(isEnabled())));

        onView(withId(R.id.playlist_totaltracks1_textview)).check(matches(withText("[3]")));
        onView(withId(R.id.playlist_totaltracks2_textview)).check(matches(withText("[3]")));

        onView(withId(R.id.playlist_list1_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(3)));
        onView(withId(R.id.playlist_list2_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(3)));

    }


    @Test
    public void with5ItemsInEachPlaylist() throws Exception {

        //arrange
        new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withMutablePlaylistHavingTracks(5)
                .withImmutablePlaylistHavingTracks(5)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.playlist_add1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_add2_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_clear1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_clear2_button)).check(matches(isEnabled()));

        onView(withId(R.id.playlist_addMany1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_addMany2_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_removeMany1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_removeMany2_button)).check(matches(isEnabled()));

        onView(withId(R.id.playlist_totaltracks1_textview)).check(matches(withText("[5]")));
        onView(withId(R.id.playlist_totaltracks2_textview)).check(matches(withText("[5]")));

        onView(withId(R.id.playlist_list1_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(5)));
        onView(withId(R.id.playlist_list2_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(5)));

    }


    @Test
    public void withDifferentItemsInEachPlaylist() throws Exception {

        //arrange
        new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withImmutablePlaylistHavingTracks(0)
                .withMutablePlaylistHavingTracks(5)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act

        //assert
        onView(withId(R.id.playlist_clear1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_clear2_button)).check(matches(isEnabled()));

        onView(withId(R.id.playlist_removeMany1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_removeMany2_button)).check(matches(isEnabled()));

        onView(withId(R.id.playlist_totaltracks1_textview)).check(matches(withText("[0]")));
        onView(withId(R.id.playlist_totaltracks2_textview)).check(matches(withText("[5]")));

        onView(withId(R.id.playlist_list1_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(0)));
        onView(withId(R.id.playlist_list2_recycleview)).check(matches(EspressoTestMatchers.withRecyclerViewItems(5)));

    }


    @Test
    public void stateMaintainedAfterRotation() throws Exception {

        //arrange
        Activity activity = new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withMutablePlaylistHavingTracks(3)
                .withImmutablePlaylistHavingTracks(3)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);

        onView(withId(R.id.playlist_addMany1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_removeMany1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_totaltracks1_textview)).check(matches(withText("[3]")));

        //act
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        //assert
        onView(withId(R.id.playlist_addMany1_button)).check(matches(isEnabled()));
        onView(withId(R.id.playlist_removeMany1_button)).check(matches(not(isEnabled())));
        onView(withId(R.id.playlist_totaltracks1_textview)).check(matches(withText("[3]")));
    }


    @Test
    public void clickAddTrackMutableCallsModel() throws Exception {
        //arrange
        new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withMutablePlaylistHavingTracks(0)
                .withImmutablePlaylistHavingTracks(0)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act
        onView(withId(R.id.playlist_add2_button)).perform(click());

        //assert
        verify(mockMutablePlaylistModel).addNewTrack();
    }


    @Test
    public void clickAddTrackImmutableCallsModel() throws Exception {
        //arrange
        new StateBuilder(mockMutablePlaylistModel, mockImmutablePlaylistModel)
                .withMutablePlaylistHavingTracks(0)
                .withImmutablePlaylistHavingTracks(0)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act
        onView(withId(R.id.playlist_add1_button)).perform(click());

        //assert
        verify(mockImmutablePlaylistModel).addNewTrack();
    }

}
