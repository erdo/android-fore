package foo.bar.example.foreadapterskt.ui.playlist;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import foo.bar.example.foreadapterskt.ui.EspressoTestMatchers;
import foo.bar.example.foreadapterskt.R;
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistAdvancedModel;
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistSimpleModel;
import foo.bar.example.foreadapterskt.feature.playlist.Track;

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


    private PlaylistSimpleModel mockPlaylistSimpleModel;
    private PlaylistAdvancedModel mockPlaylistAdvancedModel;


    @Before
    public void setup(){

        //MockitoAnnotations.initMocks(WalletView.this);
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        mockPlaylistAdvancedModel = mock(PlaylistAdvancedModel.class);
        mockPlaylistSimpleModel = mock(PlaylistSimpleModel.class);
    }


    @Test
    public void emptyPlaylists() throws Exception {

        //arrange
        new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withAdvancedPlaylistHavingTracks(0)
                .withSimplePlaylistHavingTracks(0)
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
        new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withAdvancedPlaylistHavingTracks(3)
                .withSimplePlaylistHavingTracks(3)
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
        new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withAdvancedPlaylistHavingTracks(5)
                .withSimplePlaylistHavingTracks(5)
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
        new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withSimplePlaylistHavingTracks(0)
                .withAdvancedPlaylistHavingTracks(5)
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
        Activity activity = new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withAdvancedPlaylistHavingTracks(3)
                .withSimplePlaylistHavingTracks(3)
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
    public void clickAddTrackAdvancedCallsModel() throws Exception {
        //arrange
        new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withAdvancedPlaylistHavingTracks(0)
                .withSimplePlaylistHavingTracks(0)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act
        onView(withId(R.id.playlist_add2_button)).perform(click());

        //assert
        verify(mockPlaylistAdvancedModel).addNewTrack();
    }


    @Test
    public void clickAddTrackSimpleCallsModel() throws Exception {
        //arrange
        new StateBuilder(mockPlaylistAdvancedModel, mockPlaylistSimpleModel)
                .withAdvancedPlaylistHavingTracks(0)
                .withSimplePlaylistHavingTracks(0)
                .withPlaylistsContainingTracks(new Track(R.color.pastel1))
                .createRule()
                .launchActivity(null);

        //act
        onView(withId(R.id.playlist_add1_button)).perform(click());

        //assert
        verify(mockPlaylistSimpleModel).addNewTrack();
    }

}
