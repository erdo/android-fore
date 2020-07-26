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
 * Demonstrating list animations with [AsyncListDiffer]
 *
 * This is google's wrapper for DiffUtil (this discussion of AsyncListDiffer applies similarly for
 * ListAdapter which uses AsyncListDiffer internally). The main thing to be aware of regarding
 * AsyncListDiffer is that the source of truth for the list is maintained inside
 * AsyncListDiffer itself. This has three important ramifications:
 *
 * 1) If you need to refer to it (for example to calculate some property of the current list and
 * display it on the UI) you need to use asyncListDiffer.currentList. It's important to
 * remember that if you copy this list rather than refer directly to asyncListDiffer.currentList
 * then your list copy may not accurately reflect the truth (which is carefully managed
 * inside AsyncListDiffer)
 *
 * 2) If you want to alter this list and then have the changes reflected by the adapter, you need
 * to deep copy the list, change it and then submit it back to asyncListDiffer. If you don't deep
 * copy the list, any changes you make to the items will be made to both the items in the new list
 * and the items in the old list that is maintained by asyncListDiffer, when it then compares the
 * two lists with DiffUtil it will not detect any changes and your list will be incorrectly updated.
 * If your architecture is database driven (or viewState driven along the lines of MVI), this
 * should be less of a problem because each time your list changes, you should be receiving a
 * brand new list
 * (the deep copy should be done by MVI in the interactor - however if your implementation of MVI
 * is not deep copying the list items when they change, then the same problem will present - and
 * unless you happen to know that this is how AsyncListDiffer, you're not going to have a fun day)
 *
 * 3) Because AsyncListDiffer manages the truth, you loose the ability to keep the true list
 * inside something like a view model where you can add associated logic. In this case the logic
 * resides inside the adapter itself so with this type of code, your test focus moves from testing
 * models, to testing a more complicated adapter (that does remove some of the inherent advantages
 * of using MVO style models which are shared across different views).
 *
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
