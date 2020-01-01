package foo.bar.example.foreadapterskt.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import foo.bar.example.foreadapterskt.R


class PlaylistsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, null)
    }

    companion object {

        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

}
