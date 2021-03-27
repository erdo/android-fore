package foo.bar.example.foreadapters.ui.playlist;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import co.early.fore.adapters.immutable.DiffSpec;
import co.early.fore.adapters.mutable.UpdateSpec;
import co.early.fore.core.WorkMode;
import co.early.fore.core.time.SystemTimeWrapper;
import foo.bar.example.foreadapters.App;
import foo.bar.example.foreadapters.OG;
import foo.bar.example.foreadapters.feature.playlist.ImmutablePlaylistModel;
import foo.bar.example.foreadapters.feature.playlist.MutablePlaylistModel;
import foo.bar.example.foreadapters.feature.playlist.Track;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StateBuilder {

    private MutablePlaylistModel mockMutablePlaylistModel;
    private ImmutablePlaylistModel mockImmutablePlaylistModel;

    StateBuilder(MutablePlaylistModel mockMutablePlaylistModel, ImmutablePlaylistModel mockImmutablePlaylistModel) {
        this.mockMutablePlaylistModel = mockMutablePlaylistModel;
        this.mockImmutablePlaylistModel = mockImmutablePlaylistModel;

        UpdateSpec updateSpec = new UpdateSpec(UpdateSpec.UpdateType.FULL_UPDATE, 0, 0, mock(SystemTimeWrapper.class));
        when(mockMutablePlaylistModel.getAndClearLatestUpdateSpec(anyLong())).thenReturn(updateSpec);

        DiffSpec diffSpec = new DiffSpec(null, mock(SystemTimeWrapper.class));
        when(mockImmutablePlaylistModel.getAndClearLatestDiffSpec(anyLong())).thenReturn(diffSpec);
    }

    StateBuilder withMutablePlaylistHavingTracks(int numberOfTracks) {
        when(mockMutablePlaylistModel.getItemCount()).thenReturn(numberOfTracks);
        return this;
    }

    StateBuilder withImmutablePlaylistHavingTracks(int numberOfTracks) {
        when(mockImmutablePlaylistModel.getItemCount()).thenReturn(numberOfTracks);
        return this;
    }

    StateBuilder withPlaylistsContainingTracks(Track track) {
        when(mockMutablePlaylistModel.getItem(anyInt())).thenReturn(track);
        when(mockImmutablePlaylistModel.getItem(anyInt())).thenReturn(track);
        return this;
    }

    ActivityTestRule<PlaylistsActivity>  createRule(){

        return new ActivityTestRule<PlaylistsActivity>(PlaylistsActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                App app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
                OG.setApplication(app, WorkMode.SYNCHRONOUS);

                //inject our mocks so our UI layer will pick them up
                OG.putMock(MutablePlaylistModel.class, mockMutablePlaylistModel);
                OG.putMock(ImmutablePlaylistModel.class, mockImmutablePlaylistModel);
            }
        };
    }
}
