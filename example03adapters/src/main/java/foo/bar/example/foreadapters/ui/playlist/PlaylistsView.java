package foo.bar.example.foreadapters.ui.playlist;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.observer.Observer;
import foo.bar.example.foreadapters.CustomApp;
import foo.bar.example.foreadapters.R;
import foo.bar.example.foreadapters.feature.playlist.PlaylistAdvancedModel;
import foo.bar.example.foreadapters.feature.playlist.PlaylistSimpleModel;
import foo.bar.example.foreadapters.ui.playlist.advanced.PlaylistAdapterAdvanced;
import foo.bar.example.foreadapters.ui.playlist.simple.PlaylistAdapterSimple;

/**
 *
 */
public class PlaylistsView extends LinearLayout {

    //models that we need to sync with
    private PlaylistSimpleModel playlistSimpleModel;
    private PlaylistAdvancedModel playlistAdvancedModel;


    //UI elements that we care about
    @BindView(R.id.playlist_totaltracks1_textview)
    public TextView totalTracksSimple;

    @BindView(R.id.playlist_list1_recycleview)
    public RecyclerView playListSimpleRecyclerView;

    @BindView(R.id.playlist_addMany1_button)
    public Button add5SimpleButton;

    @BindView(R.id.playlist_removeMany1_button)
    public Button clear5SimpleButton;

    @BindView(R.id.playlist_add1_button)
    public Button addSimpleButton;

    @BindView(R.id.playlist_clear1_button)
    public Button clearSimpleButton;

    @BindView(R.id.playlist_totaltracks2_textview)
    public TextView totalTracksAdvanced;

    @BindView(R.id.playlist_list2_recycleview)
    public RecyclerView playListAdvancedRecyclerView;

    @BindView(R.id.playlist_addMany2_button)
    public Button add5AdvancedButton;

    @BindView(R.id.playlist_removeMany2_button)
    public Button clear5AdvancedButton;

    @BindView(R.id.playlist_add2_button)
    public Button addAdvancedButton;

    @BindView(R.id.playlist_clear2_button)
    public Button clearAdvancedButton;


    //single observer reference
    Observer observer = this::syncView;


    private PlaylistAdapterSimple adapterSimple;
    private PlaylistAdapterAdvanced adapterAdvanced;



    public PlaylistsView(Context context) {
        super(context);
    }

    public PlaylistsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaylistsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlaylistsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        getModelReferences();

        setupButtonClickListeners();

        setupAdapters();
    }


    private void getModelReferences(){
        playlistAdvancedModel = CustomApp.get(PlaylistAdvancedModel.class);
        playlistSimpleModel = CustomApp.get(PlaylistSimpleModel.class);
    }

    private void setupButtonClickListeners() {

        add5SimpleButton.setOnClickListener(v -> playlistSimpleModel.add5NewTracks());

        clear5SimpleButton.setOnClickListener(v -> playlistSimpleModel.remove5Tracks());

        add5AdvancedButton.setOnClickListener(v -> playlistAdvancedModel.add5NewTracks());

        clear5AdvancedButton.setOnClickListener(v -> playlistAdvancedModel.remove5Tracks());

        addSimpleButton.setOnClickListener(v -> playlistSimpleModel.addNewTrack());

        clearSimpleButton.setOnClickListener(v -> playlistSimpleModel.removeAllTracks());

        addAdvancedButton.setOnClickListener(v -> playlistAdvancedModel.addNewTrack());

        clearAdvancedButton.setOnClickListener(v -> playlistAdvancedModel.removeAllTracks());

    }

    private void setupAdapters(){

        adapterSimple = new PlaylistAdapterSimple(playlistSimpleModel);
        adapterAdvanced = new PlaylistAdapterAdvanced(playlistAdvancedModel);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);

        playListSimpleRecyclerView.setLayoutManager(linearLayoutManager1);
        playListSimpleRecyclerView.setAdapter(adapterSimple);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);

        playListAdvancedRecyclerView.setLayoutManager(linearLayoutManager2);
        playListAdvancedRecyclerView.setAdapter(adapterAdvanced);
    }

    //data binding stuff below

    public void syncView(){
        clear5SimpleButton.setEnabled(playlistSimpleModel.getTrackListSize()>4);
        clear5AdvancedButton.setEnabled(playlistAdvancedModel.getTrackListSize()>4);
        clearSimpleButton.setEnabled(playlistSimpleModel.getTrackListSize()>0);
        clearAdvancedButton.setEnabled(playlistAdvancedModel.getTrackListSize()>0);
        totalTracksSimple.setText("[" + playlistSimpleModel.getTrackListSize() + "]");
        totalTracksAdvanced.setText("[" + playlistAdvancedModel.getTrackListSize() + "]");
        adapterSimple.notifyDataSetChanged();
        adapterAdvanced.notifyDataSetChangedAuto();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        playlistSimpleModel.addObserver(observer);
        playlistAdvancedModel.addObserver(observer);
        syncView();  // <- don't forget this
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        playlistSimpleModel.removeObserver(observer);
        playlistAdvancedModel.removeObserver(observer);
    }
}
