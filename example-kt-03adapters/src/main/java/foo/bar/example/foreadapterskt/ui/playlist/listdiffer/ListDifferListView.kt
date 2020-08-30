package foo.bar.example.foreadapterskt.ui.playlist.listdiffer

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import kotlinx.android.synthetic.main.view_playlists_diffable.view.*
import kotlinx.android.synthetic.main.view_playlists_listadapter.view.*
import kotlinx.android.synthetic.main.view_playlists_updateable.view.*

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class ListDifferListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), SyncableView {

    //models that we need to sync with
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var listDifferAdapter: ListDifferPlaylistAdapter

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupClickListeners()

        setupAdapters()

        // Note: we are only doing this because this example is demonstrating google's AsyncListDiffer
        // usually in MVO we would have a model that we would be observing that would call this for us
        syncView()
    }

    private fun setupClickListeners() {
        listdiffer_add_button.setOnClickListener { listDifferAdapter.addNTracks(1) }
        listdiffer_clear_button.setOnClickListener { listDifferAdapter.removeAllTracks() }
        listdiffer_add5_button.setOnClickListener { listDifferAdapter.addNTracks(5) }
        listdiffer_remove5_button.setOnClickListener { listDifferAdapter.removeNTracks(5) }
        listdiffer_add100_button.setOnClickListener { listDifferAdapter.addNTracks(100) }
        listdiffer_remove100_button.setOnClickListener { listDifferAdapter.removeNTracks(100) }
    }

    private fun setupAdapters() {
        listDifferAdapter = ListDifferPlaylistAdapter(this, logger)
        listdiffer_list_recycleview.adapter = listDifferAdapter
        listdiffer_list_recycleview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun syncView() {
        logger.i("syncView()")
        listdiffer_totaltracks_textview.text = listDifferAdapter.itemCount.toString()
        listdiffer_clear_button.isEnabled = !listDifferAdapter.isEmpty()
        listdiffer_remove5_button.isEnabled = listDifferAdapter.hasAtLeastNItems(5)
        listdiffer_remove100_button.isEnabled = listDifferAdapter.hasAtLeastNItems(100)
    }
}
