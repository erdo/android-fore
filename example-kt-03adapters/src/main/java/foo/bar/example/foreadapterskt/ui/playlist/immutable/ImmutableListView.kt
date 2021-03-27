package foo.bar.example.foreadapterskt.ui.playlist.immutable

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.adapters.CrossFadeRemover
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.immutable.ImmutablePlaylistModel
import kotlinx.android.synthetic.main.view_playlists_immutable.view.*

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
class ImmutableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    //models that we need to sync with
    private val immutablePlaylistModel: ImmutablePlaylistModel = OG[ImmutablePlaylistModel::class.java]
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var immutablePlaylistAdapter : ImmutablePlaylistAdapter

    //single observer reference
    private var observer = Observer { syncView() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupClickListeners()

        setupAdapters()
    }

    private fun setupClickListeners() {
        diffable_add_button.setOnClickListener { immutablePlaylistModel.addNTracks(1) }
        diffable_clear_button.setOnClickListener { immutablePlaylistModel.removeAllTracks() }
        diffable_add5_button.setOnClickListener { immutablePlaylistModel.addNTracks(5) }
        diffable_remove5_button.setOnClickListener { immutablePlaylistModel.removeNTracks(5) }
        diffable_add100_button.setOnClickListener { immutablePlaylistModel.addNTracks(100) }
        diffable_remove100_button.setOnClickListener { immutablePlaylistModel.removeNTracks(100) }
    }

    private fun setupAdapters() {
        immutablePlaylistAdapter = ImmutablePlaylistAdapter(immutablePlaylistModel)
        diffable_list_recycleview.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = immutablePlaylistAdapter
            itemAnimator = CrossFadeRemover()
            setHasFixedSize(true)
        }
    }

    fun syncView() {
        logger.i("syncView()")
        diffable_totaltracks_textview.text = immutablePlaylistModel.getItemCount().toString()
        diffable_clear_button.isEnabled = !immutablePlaylistModel.isEmpty()
        diffable_remove5_button.isEnabled = immutablePlaylistModel.hasAtLeastNItems(5)
        diffable_remove100_button.isEnabled = immutablePlaylistModel.hasAtLeastNItems(100)

        immutablePlaylistAdapter.notifyDataSetChangedAuto()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        immutablePlaylistModel.addObserver(observer)
        syncView()  // <- don't forget this
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        immutablePlaylistModel.removeObserver(observer)
    }
}
