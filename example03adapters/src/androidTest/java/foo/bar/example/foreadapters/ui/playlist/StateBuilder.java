package foo.bar.example.foreadapters.ui.playlist;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import co.early.fore.adapters.UpdateSpec;
import co.early.fore.core.time.SystemTimeWrapper;
import foo.bar.example.foreadapters.CustomApp;
import foo.bar.example.foreadapters.feature.playlist.PlaylistAdvancedModel;
import foo.bar.example.foreadapters.feature.playlist.PlaylistSimpleModel;
import foo.bar.example.foreadapters.feature.playlist.Track;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class StateBuilder {

    private PlaylistAdvancedModel mockPlaylistAdvancedModel;
    private PlaylistSimpleModel mockPlaylistSimpleModel;

    StateBuilder(PlaylistAdvancedModel mockPlaylistAdvancedModel, PlaylistSimpleModel mockPlaylistSimpleModel) {
        this.mockPlaylistAdvancedModel = mockPlaylistAdvancedModel;
        this.mockPlaylistSimpleModel = mockPlaylistSimpleModel;

        UpdateSpec updateSpec = new UpdateSpec(UpdateSpec.UpdateType.FULL_UPDATE, 0, 0, mock(SystemTimeWrapper.class));
        when(mockPlaylistAdvancedModel.getAndClearLatestUpdateSpec(anyLong())).thenReturn(updateSpec);
    }

    StateBuilder withAdvancedPlaylistHavingTracks(int numberOfTracks) {
        when(mockPlaylistAdvancedModel.getTrackListSize()).thenReturn(numberOfTracks);
        return this;
    }

    StateBuilder withSimplePlaylistHavingTracks(int numberOfTracks) {
        when(mockPlaylistSimpleModel.getTrackListSize()).thenReturn(numberOfTracks);
        return this;
    }

    StateBuilder withPlaylistsContainingTracks(Track track) {
        when(mockPlaylistAdvancedModel.getTrack(anyInt())).thenReturn(track);
        when(mockPlaylistSimpleModel.getTrack(anyInt())).thenReturn(track);
        return this;
    }

    ActivityTestRule<PlaylistsActivity>  createRule(){

        return new ActivityTestRule<PlaylistsActivity>(PlaylistsActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                CustomApp customApp = (CustomApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
                customApp.injectSynchronousObjectGraph();

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(PlaylistAdvancedModel.class, mockPlaylistAdvancedModel);
                customApp.injectMockObject(PlaylistSimpleModel.class, mockPlaylistSimpleModel);
            }

        };
    }

}
