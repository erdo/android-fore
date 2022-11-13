package foo.bar.example.foreadapterskt.ui.playlist.mutable

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
import foo.bar.example.foreadapterskt.feature.playlist.mutable.MutablePlaylistModel

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class MutableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), SyncableView {

    //models that we need to sync with
    private val mutablePlaylistModel: MutablePlaylistModel = OG[MutablePlaylistModel::class.java]
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var updatableAddButton: Button
    private lateinit var updatableAdd5Button: Button
    private lateinit var updatableAdd100Button: Button
    private lateinit var updatableRemove5Button: Button
    private lateinit var updatableRemove100Button: Button
    private lateinit var updatableClearButton: Button
    private lateinit var updatableTotaltracksTextview: TextView
    private lateinit var updatableListRecycleview: RecyclerView

    private lateinit var mutablePlaylistAdapter: MutablePlaylistAdapter

    //single observer reference
    private var observer = Observer { syncView() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupUiReferences()

        setupClickListeners()

        setupAdapters()
    }

    private fun setupUiReferences() {
        updatableAddButton = findViewById(R.id.updatable_add_button)
        updatableAdd5Button = findViewById(R.id.updatable_add5_button)
        updatableAdd100Button = findViewById(R.id.updatable_add100_button)
        updatableRemove5Button = findViewById(R.id.updatable_remove5_button)
        updatableRemove100Button = findViewById(R.id.updatable_remove100_button)
        updatableClearButton = findViewById(R.id.updatable_clear_button)
        updatableTotaltracksTextview = findViewById(R.id.updatable_totaltracks_textview)
        updatableListRecycleview = findViewById(R.id.updatable_list_recycleview)
    }

    private fun setupClickListeners() {
        updatableAddButton.setOnClickListener { mutablePlaylistModel.addNTracks(1) }
        updatableClearButton.setOnClickListener { mutablePlaylistModel.removeAllTracks() }
        updatableAdd5Button.setOnClickListener { mutablePlaylistModel.addNTracks(5) }
        updatableRemove5Button.setOnClickListener { mutablePlaylistModel.removeNTracks(5) }
        updatableAdd100Button.setOnClickListener { mutablePlaylistModel.addNTracks(100) }
        updatableRemove100Button.setOnClickListener { mutablePlaylistModel.removeNTracks(100) }
    }

    private fun setupAdapters() {
        mutablePlaylistAdapter = MutablePlaylistAdapter(mutablePlaylistModel)
        updatableListRecycleview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mutablePlaylistAdapter
            itemAnimator = CrossFadeRemover()
            setHasFixedSize(true)
        }
    }

    override fun syncView() {
        logger.i("syncView()")
        updatableTotaltracksTextview.text = mutablePlaylistModel.getItemCount().toString()
        updatableClearButton.isEnabled = !mutablePlaylistModel.isEmpty()
        updatableRemove5Button.isEnabled = mutablePlaylistModel.hasAtLeastNItems(5)
        updatableRemove100Button.isEnabled = mutablePlaylistModel.hasAtLeastNItems(100)

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
