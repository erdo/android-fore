package foo.bar.example.foreadapterskt.ui.playlist.updatable

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import kotlinx.android.synthetic.main.view_playlists_diffable.view.*
import kotlinx.android.synthetic.main.view_playlists_updateable.view.*

/**
 * Demonstrating list animations with [Updatable]
 */
class UpdatableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    //models that we need to sync with
    private val updatablePlaylistModel: UpdatablePlaylistModel = OG[UpdatablePlaylistModel::class.java]
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var updatablePlaylistModelAdapter: UpdatablePlaylistModelAdapter

    //single observer reference
    private var observer = Observer { syncView() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupClickListeners()

        setupAdapters()
    }

    private fun setupClickListeners() {
        updatable_add_button.setOnClickListener { updatablePlaylistModel.addNTracks(1) }
        updatable_clear_button.setOnClickListener { updatablePlaylistModel.removeAllTracks() }
        updatable_add5_button.setOnClickListener { updatablePlaylistModel.addNTracks(5) }
        updatable_remove5_button.setOnClickListener { updatablePlaylistModel.removeNTracks(5) }
        updatable_add100_button.setOnClickListener { updatablePlaylistModel.addNTracks(100) }
        updatable_remove100_button.setOnClickListener { updatablePlaylistModel.removeNTracks(100) }
    }

    private fun setupAdapters() {
        updatablePlaylistModelAdapter = UpdatablePlaylistModelAdapter(updatablePlaylistModel)
        updatable_list_recycleview.adapter = updatablePlaylistModelAdapter
        updatable_list_recycleview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun syncView() {
        updatable_totaltracks_textview.text = updatablePlaylistModel.trackListSize.toString()
        updatable_clear_button.isEnabled = !updatablePlaylistModel.isEmpty()
        updatable_remove5_button.isEnabled = updatablePlaylistModel.hasAtLeastNItems(5)
        updatable_remove100_button.isEnabled = updatablePlaylistModel.hasAtLeastNItems(100)

        updatablePlaylistModelAdapter.notifyDataSetChangedAuto()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updatablePlaylistModel.addObserver(observer)
        syncView()  // <- don't forget this
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        updatablePlaylistModel.removeObserver(observer)
    }
}
