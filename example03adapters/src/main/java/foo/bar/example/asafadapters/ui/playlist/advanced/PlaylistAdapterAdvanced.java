package foo.bar.example.asafadapters.ui.playlist.advanced;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.asaf.adapters.SimpleChangeAwareAdapter;
import foo.bar.example.asafadapters.R;
import foo.bar.example.asafadapters.feature.playlist.PlaylistAdvancedModel;
import foo.bar.example.asafadapters.feature.playlist.Track;

/**
 *
 */
public class PlaylistAdapterAdvanced extends SimpleChangeAwareAdapter<PlaylistAdapterAdvanced.ViewHolder> {

    private static final String TAG = PlaylistAdapterAdvanced.class.getSimpleName();

    private final PlaylistAdvancedModel playlistAdvancedModel;

    public PlaylistAdapterAdvanced(final PlaylistAdvancedModel playlistAdvancedModel) {
        super(playlistAdvancedModel);
        this.playlistAdvancedModel = playlistAdvancedModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_playlists_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Track item = playlistAdvancedModel.getTrack(position);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int betterPosition = holder.getAdapterPosition();
                if (betterPosition!=-1) {
                    playlistAdvancedModel.increasePlaysForTrack(betterPosition);
                }
            }
        });

        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int betterPosition = holder.getAdapterPosition();
                if (betterPosition!=-1) {
                    playlistAdvancedModel.decreasePlaysForTrack(betterPosition);
                }
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //yuk, can't find a way around this, without checking
                //here you will occasionally get outofindex errors
                //if you tap very fast on different rows removing them
                //while you are using adapter animations
                int betterPosition = holder.getAdapterPosition();
                if (betterPosition!=-1) {
                    playlistAdvancedModel.removeTrack(betterPosition);
                }
            }
        });

        holder.itemView.setBackgroundResource(item.getColourResource());
        holder.playsRequested.setText("" + item.getNumberOfPlaysRequested());
        holder.increase.setEnabled(item.canIncreasePlays());
        holder.decrease.setEnabled(item.canDecreasePlays());
    }

    @Override
    public int getItemCount() {
        return playlistAdvancedModel.getTrackListSize();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.track_playsrequested_text)
        protected TextView playsRequested;

        @BindView(R.id.track_increaseplays_button)
        protected Button increase;

        @BindView(R.id.track_decreaseplays_button)
        protected Button decrease;

        @BindView(R.id.track_remove_button)
        protected Button remove;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
