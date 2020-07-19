package foo.bar.example.foreadapterskt.ui.playlist

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.logging.Logger
import foo.bar.example.foreadapterskt.OG
import foo.bar.example.foreadapterskt.R
import foo.bar.example.foreadapterskt.feature.playlist.diffable.DiffablePlaylistModel
import foo.bar.example.foreadapterskt.feature.playlist.updatable.UpdatablePlaylistModel
import foo.bar.example.foreadapterskt.ui.playlist.diffable.DiffablePlaylistModelAdapter
import foo.bar.example.foreadapterskt.ui.playlist.updatable.UpdatablePlaylistModelAdapter
import foo.bar.example.foreadapterskt.ui.playlist.listdiffer.ListDifferPlaylistAdapter
import kotlinx.android.synthetic.main.activity_playlists.*

class PlaylistsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlists)
    }
}
