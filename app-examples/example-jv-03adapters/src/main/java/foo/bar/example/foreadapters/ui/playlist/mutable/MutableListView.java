package foo.bar.example.foreadapters.ui.playlist.mutable;

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
import co.early.fore.core.ui.SyncableView;
import foo.bar.example.foreadapters.OG;
import foo.bar.example.foreadapters.R;
import foo.bar.example.foreadapters.feature.playlist.MutablePlaylistModel;

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
public class MutableListView extends RelativeLayout implements SyncableView {

    //models that we need
    private MutablePlaylistModel mutablePlaylistModel = OG.get(MutablePlaylistModel.class);

    //UI elements that we care about
    @BindView(R.id.playlist_totaltracks2_textview)
    public TextView totalTracksMutable;

    @BindView(R.id.playlist_list2_recycleview)
    public RecyclerView playListMutableRecyclerView;

    @BindView(R.id.playlist_addMany2_button)
    public Button add5MutableButton;

    @BindView(R.id.playlist_removeMany2_button)
    public Button clear5MutableButton;

    @BindView(R.id.playlist_add2_button)
    public Button addMutableButton;

    @BindView(R.id.playlist_clear2_button)
    public Button clearMutableButton;


    //single observer reference
    Observer observer = this::syncView;


    private MutablePlaylistAdapter mutablePlaylistAdapter;


    public MutableListView(Context context) {
        super(context);
    }

    public MutableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MutableListView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        add5MutableButton.setOnClickListener(v -> mutablePlaylistModel.add5NewTracks());
        clear5MutableButton.setOnClickListener(v -> mutablePlaylistModel.remove5Tracks());
        addMutableButton.setOnClickListener(v -> mutablePlaylistModel.addNewTrack());
        clearMutableButton.setOnClickListener(v -> mutablePlaylistModel.removeAllTracks());
    }

    private void setupAdapters() {

        mutablePlaylistAdapter = new MutablePlaylistAdapter(mutablePlaylistModel);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);

        playListMutableRecyclerView.setLayoutManager(linearLayoutManager1);
        playListMutableRecyclerView.setAdapter(mutablePlaylistAdapter);
        playListMutableRecyclerView.setItemAnimator(new CrossFadeRemover());
        playListMutableRecyclerView.setHasFixedSize(true);
    }

    //data binding stuff below

    public void syncView() {

        clear5MutableButton.setEnabled(mutablePlaylistModel.getItemCount() > 4);
        clearMutableButton.setEnabled(mutablePlaylistModel.getItemCount() > 0);
        totalTracksMutable.setText("[" + mutablePlaylistModel.getItemCount() + "]");
        mutablePlaylistAdapter.notifyDataSetChangedAuto();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mutablePlaylistModel.addObserver(observer);
        syncView();  // <- don't forget this
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mutablePlaylistModel.removeObserver(observer);
    }
}
