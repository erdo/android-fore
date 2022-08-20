package foo.bar.example.foreadapterskt.ui.playlist.listdiffer

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.early.fore.adapters.CrossFadeRemover
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.R

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

    private lateinit var listdifferAddButton: Button
    private lateinit var listdifferAdd5Button: Button
    private lateinit var listdifferAdd100Button: Button
    private lateinit var listdifferRemove5Button: Button
    private lateinit var listdifferRemove100Button: Button
    private lateinit var listdifferClearButton: Button
    private lateinit var listdifferTotaltracksTextview: TextView
    private lateinit var listdifferListRecycleview: RecyclerView

    private lateinit var listDifferAdapter: ListDifferPlaylistAdapter

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupUiReferences()

        setupClickListeners()

        setupAdapters()

        // Note: we are only doing this because this example is demonstrating google's AsyncListDiffer
        // usually in MVO we would have a model that we would be observing that would call this for us
        syncView()
    }

    private fun setupUiReferences() {
        listdifferAddButton = findViewById(R.id.listdiffer_add_button)
        listdifferAdd5Button = findViewById(R.id.listdiffer_add5_button)
        listdifferAdd100Button = findViewById(R.id.listdiffer_add100_button)
        listdifferRemove5Button = findViewById(R.id.listdiffer_remove5_button)
        listdifferRemove100Button = findViewById(R.id.listdiffer_remove100_button)
        listdifferClearButton = findViewById(R.id.listdiffer_clear_button)
        listdifferTotaltracksTextview = findViewById(R.id.listdiffer_totaltracks_textview)
        listdifferListRecycleview = findViewById(R.id.listdiffer_list_recycleview)
    }

    private fun setupClickListeners() {
        listdifferAddButton.setOnClickListener { listDifferAdapter.addNTracks(1) }
        listdifferClearButton.setOnClickListener { listDifferAdapter.removeAllTracks() }
        listdifferAdd5Button.setOnClickListener { listDifferAdapter.addNTracks(5) }
        listdifferRemove5Button.setOnClickListener { listDifferAdapter.removeNTracks(5) }
        listdifferAdd100Button.setOnClickListener { listDifferAdapter.addNTracks(100) }
        listdifferRemove100Button.setOnClickListener { listDifferAdapter.removeNTracks(100) }
    }

    private fun setupAdapters() {
        listDifferAdapter = ListDifferPlaylistAdapter(this, logger)
        listdifferListRecycleview.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listDifferAdapter
            itemAnimator = CrossFadeRemover()
            setHasFixedSize(true)
        }
    }

    override fun syncView() {
        logger.i("syncView()")
        listdifferTotaltracksTextview.text = listDifferAdapter.itemCount.toString()
        listdifferClearButton.isEnabled = !listDifferAdapter.isEmpty()
        listdifferRemove5Button.isEnabled = listDifferAdapter.hasAtLeastNItems(5)
        listdifferRemove100Button.isEnabled = listDifferAdapter.hasAtLeastNItems(100)
    }
}
