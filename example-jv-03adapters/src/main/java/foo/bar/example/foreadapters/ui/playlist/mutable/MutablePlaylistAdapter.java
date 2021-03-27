package foo.bar.example.foreadapters.ui.playlist.mutable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.adapters.Notifyable;
import co.early.fore.adapters.NotifyableImp;
import co.early.fore.core.Affirm;
import foo.bar.example.foreadapters.R;
import foo.bar.example.foreadapters.feature.playlist.MutablePlaylistModel;
import foo.bar.example.foreadapters.feature.playlist.Track;
import foo.bar.example.foreadapters.ui.widget.PercentPie;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static foo.bar.example.foreadapters.ui.playlist.mutable.MutablePlaylistAdapter.*;

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
public class MutablePlaylistAdapter extends RecyclerView.Adapter<ViewHolder> implements Notifyable<ViewHolder> {

    private final MutablePlaylistModel mutablePlaylistModel;
    private final NotifyableImp<ViewHolder> notifyableImp;

    public MutablePlaylistAdapter(final MutablePlaylistModel mutablePlaylistModel) {
        this.mutablePlaylistModel = Affirm.notNull(mutablePlaylistModel);
        this.notifyableImp = new NotifyableImp<>(this, mutablePlaylistModel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_playlists_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Track item = mutablePlaylistModel.getItem(position);

        holder.increase.setOnClickListener(v -> {
            //if you tap very fast on different rows removing them
            //while you are using adapter animations you will crash unless
            //you check for this
            int betterPosition = holder.getAdapterPosition();
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.increasePlaysForTrack(betterPosition);
            }
        });

        holder.decrease.setOnClickListener(v -> {
            int betterPosition = holder.getAdapterPosition();
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.decreasePlaysForTrack(betterPosition);
            }
        });

        holder.remove.setOnClickListener(v -> {
            int betterPosition = holder.getAdapterPosition();
            if (betterPosition != NO_POSITION) {
                mutablePlaylistModel.removeTrack(betterPosition);
            }
        });

        holder.itemView.setBackgroundResource(item.getColourResource());
        holder.playsRequested.setText("" + item.getNumberOfPlaysRequested());
        holder.increase.setEnabled(item.canIncreasePlays());
        holder.decrease.setEnabled(item.canDecreasePlays());
        holder.pie.setPercentDone(item.getUniqueId(), item.getNumberOfPlaysRequested()*100/Track.MAX_PLAYS_REQUESTED);
    }

    @Override
    public int getItemCount() {
        return mutablePlaylistModel.getItemCount();
    }

    @Override
    public void notifyDataSetChangedAuto() {
        notifyableImp.notifyDataSetChangedAuto();
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

        @BindView(R.id.track_percent_pie)
        protected PercentPie pie;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
