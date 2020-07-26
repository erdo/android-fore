package foo.bar.example.foreadapterskt.ui.playlist.updatable

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.time.measureNanos
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import kotlinx.android.synthetic.main.view_playlists_updateable.view.*
import kotlin.system.measureNanoTime

/**
 * Demonstrating list animations with [Updatable]
 *
 * fore's [Updatable] classes use android's notifyItem... methods behind the scenes. This is
 * quite a lot more efficient than using DiffUtil but you can only use this method when you know
 * what changes have been made to the list. There are other situations when this information is
 * not available and all you have is an old list and a new list: in that case, DiffUtil is required
 * (fore uses the [Diffable] classes for that)
 *
 * In this example you'll see the adapter is much less verbose than if we were using google's
 * [AsyncListDiffer] method (you'll find most of the code in [UpdatablePlaylistModel])
 *
 * Because using Updatable is a lot less resource intensive than using DiffUtil, here it's not
 * necessary to use coroutines in the Model (unlike the Diffable example)
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
        logger.i("syncView()")
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
