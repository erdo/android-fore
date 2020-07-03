package foo.bar.example.foreadapterskt.ui.playlist


import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.core.observer.Observer
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistAdvancedModel
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistSimpleModel
import foo.bar.example.foreadapterskt.ui.playlist.advanced.PlaylistAdapterAdvanced
import foo.bar.example.foreadapterskt.ui.playlist.simple.PlaylistAdapterSimple
import kotlinx.android.synthetic.main.activity_playlists.playlist_add1_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_add2_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_addMany1_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_addMany2_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_clear1_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_clear2_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_list1_recycleview
import kotlinx.android.synthetic.main.activity_playlists.playlist_list2_recycleview
import kotlinx.android.synthetic.main.activity_playlists.playlist_removeMany1_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_removeMany2_button
import kotlinx.android.synthetic.main.activity_playlists.playlist_totaltracks1_textview
import kotlinx.android.synthetic.main.activity_playlists.playlist_totaltracks2_textview

class PlaylistsActivity : FragmentActivity() {


    //models that we need to sync with
    private val playlistSimpleModel: PlaylistSimpleModel = OG[PlaylistSimpleModel::class.java]
    private val playlistAdvancedModel: PlaylistAdvancedModel = OG[PlaylistAdvancedModel::class.java]


    //single observer reference
    private var observer = Observer { syncView() }


    private lateinit var adapterSimple: PlaylistAdapterSimple
    private lateinit var adapterAdvanced: PlaylistAdapterAdvanced


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_playlists)

        setupButtonClickListeners()

        setupAdapters()
    }

    private fun setupButtonClickListeners() {
        playlist_addMany1_button.setOnClickListener { playlistSimpleModel.add5NewTracks() }
        playlist_removeMany1_button.setOnClickListener { playlistSimpleModel.remove5Tracks() }
        playlist_addMany2_button.setOnClickListener { playlistAdvancedModel.add5NewTracks() }
        playlist_removeMany2_button.setOnClickListener { playlistAdvancedModel.remove5Tracks() }
        playlist_add1_button.setOnClickListener { playlistSimpleModel.addNewTrack() }
        playlist_clear1_button.setOnClickListener { playlistSimpleModel.removeAllTracks() }
        playlist_add2_button.setOnClickListener { playlistAdvancedModel.addNewTrack() }
        playlist_clear2_button.setOnClickListener { playlistAdvancedModel.removeAllTracks() }
    }

    private fun setupAdapters() {

        adapterSimple = PlaylistAdapterSimple(playlistSimpleModel)
        adapterAdvanced = PlaylistAdapterAdvanced(playlistAdvancedModel)

        playlist_list1_recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlist_list1_recycleview.adapter = adapterSimple

        playlist_list2_recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlist_list2_recycleview.adapter = adapterAdvanced
    }

    //data binding stuff below

    fun syncView() {

        playlist_removeMany1_button.isEnabled =
            playlistSimpleModel.trackListSize > 4 // <- if we want we can move this logic to the model too (where we can unit test it) e.g. fun canRemoveMany(): Boolean
        playlist_removeMany2_button.isEnabled = playlistAdvancedModel.trackListSize > 4
        playlist_clear1_button.isEnabled = playlistSimpleModel.trackListSize > 0
        playlist_clear2_button.isEnabled = playlistAdvancedModel.trackListSize > 0
        playlist_totaltracks1_textview.text = "[${playlistSimpleModel.trackListSize}]"
        playlist_totaltracks2_textview.text = "[${playlistAdvancedModel.trackListSize}]"

        adapterSimple.notifyDataSetChanged()
        adapterAdvanced.notifyDataSetChangedAuto() // <- note the auto, automatically handles change animations
    }

    override fun onStart() {
        super.onStart()
        playlistSimpleModel.addObserver(observer)
        playlistAdvancedModel.addObserver(observer)
        syncView()  // <- don't forget this
    }

    override fun onStop() {
        super.onStop()
        playlistSimpleModel.removeObserver(observer)
        playlistAdvancedModel.removeObserver(observer)
    }
}
