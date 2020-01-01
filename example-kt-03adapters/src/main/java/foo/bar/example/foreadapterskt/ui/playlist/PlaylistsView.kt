package foo.bar.example.foreadapterskt.ui.playlist

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistAdvancedModel
import foo.bar.example.foreadapterskt.feature.playlist.PlaylistSimpleModel
import foo.bar.example.foreadapterskt.ui.playlist.advanced.PlaylistAdapterAdvanced
import foo.bar.example.foreadapterskt.ui.playlist.simple.PlaylistAdapterSimple
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_add1_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_add2_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_addMany1_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_addMany2_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_clear1_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_clear2_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_list1_recycleview
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_list2_recycleview
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_removeMany1_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_removeMany2_button
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_totaltracks1_textview
import kotlinx.android.synthetic.main.fragment_playlists.view.playlist_totaltracks2_textview

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class PlaylistsView @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    //models that we need to sync with
    private lateinit var playlistSimpleModel: PlaylistSimpleModel
    private lateinit var playlistAdvancedModel: PlaylistAdvancedModel


    //single observer reference
    private var observer = this::syncView


    private lateinit var adapterSimple: PlaylistAdapterSimple
    private lateinit var adapterAdvanced: PlaylistAdapterAdvanced


    override fun onFinishInflate() {
        super.onFinishInflate()

        getModelReferences()

        setupButtonClickListeners()

        setupAdapters()
    }


    private fun getModelReferences() {
        playlistAdvancedModel = OG[PlaylistAdvancedModel::class.java]
        playlistSimpleModel = OG[PlaylistSimpleModel::class.java]
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

        playlist_list1_recycleview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        playlist_list1_recycleview.adapter = adapterSimple

        playlist_list2_recycleview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        playlist_list2_recycleview.adapter = adapterAdvanced
    }

    //data binding stuff below

    fun syncView() {

        playlist_removeMany1_button.isEnabled = playlistSimpleModel.trackListSize > 4 // <- if we want we can move this logic to the model too (where we can unit test it) e.g. fun canRemoveMany(): Boolean
        playlist_removeMany2_button.isEnabled = playlistAdvancedModel.trackListSize > 4
        playlist_clear1_button.isEnabled = playlistSimpleModel.trackListSize > 0
        playlist_clear2_button.isEnabled = playlistAdvancedModel.trackListSize > 0
        playlist_totaltracks1_textview.text = "[${playlistSimpleModel.trackListSize}]"
        playlist_totaltracks2_textview.text = "[${playlistAdvancedModel.trackListSize}]"

        adapterSimple.notifyDataSetChanged()
        adapterAdvanced.notifyDataSetChangedAuto() // <- note the auto, automatically handles change animations
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playlistSimpleModel.addObserver(observer)
        playlistAdvancedModel.addObserver(observer)
        syncView()  // <- don't forget this
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        playlistSimpleModel.removeObserver(observer)
        playlistAdvancedModel.removeObserver(observer)
    }
}
