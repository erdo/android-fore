package foo.bar.example.foreadapterskt.ui.playlist.mutable

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.adapters.CrossFadeRemover
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel
import kotlinx.android.synthetic.main.view_playlists_immutable.view.*
import kotlinx.android.synthetic.main.view_playlists_mutable.view.*

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class MutableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    //models that we need to sync with
    private val mutablePlaylistModel: MutablePlaylistModel = OG[MutablePlaylistModel::class.java]
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var mutablePlaylistAdapter: MutablePlaylistAdapter

    //single observer reference
    private var observer = Observer { syncView() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupClickListeners()

        setupAdapters()
    }

    private fun setupClickListeners() {
        updatable_add_button.setOnClickListener { mutablePlaylistModel.addNTracks(1) }
        updatable_clear_button.setOnClickListener { mutablePlaylistModel.removeAllTracks() }
        updatable_add5_button.setOnClickListener { mutablePlaylistModel.addNTracks(5) }
        updatable_remove5_button.setOnClickListener { mutablePlaylistModel.removeNTracks(5) }
        updatable_add100_button.setOnClickListener { mutablePlaylistModel.addNTracks(100) }
        updatable_remove100_button.setOnClickListener { mutablePlaylistModel.removeNTracks(100) }
    }

    private fun setupAdapters() {
        mutablePlaylistAdapter = MutablePlaylistAdapter(mutablePlaylistModel)
        updatable_list_recycleview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mutablePlaylistAdapter
            itemAnimator = CrossFadeRemover()
            setHasFixedSize(true)
        }
    }

    fun syncView() {
        logger.i("syncView()")
        updatable_totaltracks_textview.text = mutablePlaylistModel.getItemCount().toString()
        updatable_clear_button.isEnabled = !mutablePlaylistModel.isEmpty()
        updatable_remove5_button.isEnabled = mutablePlaylistModel.hasAtLeastNItems(5)
        updatable_remove100_button.isEnabled = mutablePlaylistModel.hasAtLeastNItems(100)

        mutablePlaylistAdapter.notifyDataSetChangedAuto()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mutablePlaylistModel.addObserver(observer)
        syncView()  // <- don't forget this
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mutablePlaylistModel.removeObserver(observer)
    }
}
