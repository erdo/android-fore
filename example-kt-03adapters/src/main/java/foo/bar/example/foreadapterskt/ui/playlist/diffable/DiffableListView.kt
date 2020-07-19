package foo.bar.example.foreadapterskt.ui.playlist.diffable

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import kotlinx.android.synthetic.main.view_playlists_diffable.view.*

/**
 * Demonstrating list animations with [Diffable]
 */
class DiffableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    //models that we need to sync with
    private val diffablePlaylistModel: DiffablePlaylistModel = OG[DiffablePlaylistModel::class.java]
    private val logger: Logger = OG[Logger::class.java]

    private lateinit var diffablePlaylistModelAdapter : DiffablePlaylistModelAdapter

    //single observer reference
    private var observer = Observer { syncView() }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setupClickListeners()

        setupAdapters()
    }

    private fun setupClickListeners() {
        diffable_add_button.setOnClickListener { diffablePlaylistModel.addNTracks(1) }
        diffable_clear_button.setOnClickListener { diffablePlaylistModel.removeAllTracks() }
        diffable_add5_button.setOnClickListener { diffablePlaylistModel.addNTracks(5) }
        diffable_remove5_button.setOnClickListener { diffablePlaylistModel.removeNTracks(5) }
        diffable_add100_button.setOnClickListener { diffablePlaylistModel.addNTracks(100) }
        diffable_remove100_button.setOnClickListener { diffablePlaylistModel.removeNTracks(100) }
    }

    private fun setupAdapters() {
        diffablePlaylistModelAdapter = DiffablePlaylistModelAdapter(diffablePlaylistModel)
        diffable_list_recycleview.adapter = diffablePlaylistModelAdapter
        diffable_list_recycleview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun syncView() {
        diffable_totaltracks_textview.text = diffablePlaylistModel.trackListSize.toString()
        diffable_clear_button.isEnabled = !diffablePlaylistModel.isEmpty()
        diffable_remove5_button.isEnabled = diffablePlaylistModel.hasAtLeastNItems(5)
        diffable_remove100_button.isEnabled = diffablePlaylistModel.hasAtLeastNItems(100)

        diffablePlaylistModelAdapter.notifyDataSetChangedAuto()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        diffablePlaylistModel.addObserver(observer)
        syncView()  // <- don't forget this
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        diffablePlaylistModel.removeObserver(observer)
    }
}
