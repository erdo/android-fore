package foo.bar.example.foreadapterskt.ui.playlist.immutable

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.early.fore.adapters.CrossFadeRemover
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.immutable.ImmutablePlaylistModel

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
class ImmutableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), SyncableView {

    //models that we need to sync with
    private val immutablePlaylistModel: ImmutablePlaylistModel = OG[ImmutablePlaylistModel::class.java]
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var diffableAddButton: Button
    private lateinit var diffableAdd5Button: Button
    private lateinit var diffableAdd100Button: Button
    private lateinit var diffableRemove5Button: Button
    private lateinit var diffableRemove100Button: Button
    private lateinit var diffableClearButton: Button
    private lateinit var diffableTotaltracksTextview: TextView
    private lateinit var diffableListRecycleview: RecyclerView

    private lateinit var immutablePlaylistAdapter : ImmutablePlaylistAdapter

    //single observer reference
    private var observer = Observer { syncView() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupUiReferences()

        setupClickListeners()

        setupAdapters()
    }

    private fun setupUiReferences() {
        diffableAddButton = findViewById(R.id.diffable_add_button)
        diffableAdd5Button = findViewById(R.id.diffable_add5_button)
        diffableAdd100Button = findViewById(R.id.diffable_add100_button)
        diffableRemove5Button = findViewById(R.id.diffable_remove5_button)
        diffableRemove100Button = findViewById(R.id.diffable_remove100_button)
        diffableClearButton = findViewById(R.id.diffable_clear_button)
        diffableTotaltracksTextview = findViewById(R.id.diffable_totaltracks_textview)
        diffableListRecycleview = findViewById(R.id.diffable_list_recycleview)
    }

    private fun setupClickListeners() {
        diffableAddButton.setOnClickListener { immutablePlaylistModel.addNTracks(1) }
        diffableAdd5Button.setOnClickListener { immutablePlaylistModel.addNTracks(5) }
        diffableAdd100Button.setOnClickListener { immutablePlaylistModel.addNTracks(100) }
        diffableRemove5Button.setOnClickListener { immutablePlaylistModel.removeNTracks(5) }
        diffableRemove100Button.setOnClickListener { immutablePlaylistModel.removeNTracks(100) }
        diffableClearButton.setOnClickListener { immutablePlaylistModel.removeAllTracks() }
    }

    private fun setupAdapters() {
        immutablePlaylistAdapter = ImmutablePlaylistAdapter(immutablePlaylistModel)
        diffableListRecycleview.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = immutablePlaylistAdapter
            itemAnimator = CrossFadeRemover()
            setHasFixedSize(true)
        }
    }

    override fun syncView() {
        logger.i("syncView()")
        diffableRemove5Button.isEnabled = immutablePlaylistModel.hasAtLeastNItems(5)
        diffableRemove100Button.isEnabled = immutablePlaylistModel.hasAtLeastNItems(100)
        diffableTotaltracksTextview.text = immutablePlaylistModel.getItemCount().toString()
        diffableClearButton.isEnabled = !immutablePlaylistModel.isEmpty()

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
