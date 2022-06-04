package foo.bar.example.foreadapters.ui.playlist.immutable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.adapters.CrossFadeRemover;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.ui.SyncableView;
import foo.bar.example.foreadapters.OG;
import foo.bar.example.foreadapters.R;
import foo.bar.example.foreadapters.feature.playlist.ImmutablePlaylistModel;
import foo.bar.example.foreadapters.ui.playlist.mutable.MutablePlaylistAdapter;

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
public class ImmutableListView extends RelativeLayout implements SyncableView {

    //models that we need
    private ImmutablePlaylistModel immutablePlaylistModel = OG.get(ImmutablePlaylistModel.class);

    //UI elements that we care about
    @BindView(R.id.playlist_totaltracks1_textview)
    public TextView totalTracksImmutable;

    @BindView(R.id.playlist_list1_recycleview)
    public RecyclerView playListImmutableRecyclerView;

    @BindView(R.id.playlist_addMany1_button)
    public Button add5ImmutableButton;

    @BindView(R.id.playlist_removeMany1_button)
    public Button clear5ImmutableButton;

    @BindView(R.id.playlist_add1_button)
    public Button addImmutableButton;

    @BindView(R.id.playlist_clear1_button)
    public Button clearImmutableButton;


    //single observer reference
    Observer observer = this::syncView;


    private ImmutablePlaylistAdapter immutablePlaylistAdapter;


    public ImmutableListView(Context context) {
        super(context);
    }

    public ImmutableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImmutableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        setupClickListeners();

        setupAdapters();
    }

    private void setupClickListeners() {
        add5ImmutableButton.setOnClickListener(v -> immutablePlaylistModel.add5NewTracks());
        clear5ImmutableButton.setOnClickListener(v -> immutablePlaylistModel.remove5Tracks());
        addImmutableButton.setOnClickListener(v -> immutablePlaylistModel.addNewTrack());
        clearImmutableButton.setOnClickListener(v -> immutablePlaylistModel.removeAllTracks());
    }

    private void setupAdapters() {

        immutablePlaylistAdapter = new ImmutablePlaylistAdapter(immutablePlaylistModel);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);

        playListImmutableRecyclerView.setLayoutManager(linearLayoutManager1);
        playListImmutableRecyclerView.setAdapter(immutablePlaylistAdapter);
        playListImmutableRecyclerView.setItemAnimator(new CrossFadeRemover());
        playListImmutableRecyclerView.setHasFixedSize(true);
    }

    //data binding stuff below

    public void syncView() {

        clear5ImmutableButton.setEnabled(immutablePlaylistModel.getItemCount() > 4);
        clearImmutableButton.setEnabled(immutablePlaylistModel.getItemCount() > 0);
        totalTracksImmutable.setText("[" + immutablePlaylistModel.getItemCount() + "]");
        immutablePlaylistAdapter.notifyDataSetChangedAuto();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        immutablePlaylistModel.addObserver(observer);
        syncView();  // <- don't forget this
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        immutablePlaylistModel.removeObserver(observer);
    }
}
