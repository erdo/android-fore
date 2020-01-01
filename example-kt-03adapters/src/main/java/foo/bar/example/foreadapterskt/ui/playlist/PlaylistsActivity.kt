package foo.bar.example.foreadapterskt.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import foo.bar.example.foreadapterskt.R


class PlaylistsActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.common_activity_base)

        if (savedInstanceState == null) {
            setFragment(
                PlaylistsFragment.newInstance(),
                PlaylistsFragment::class.java.getSimpleName()
            )
        }
    }

    private fun setFragment(fragment: Fragment, fragmentTag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.content_main,
            fragment,
            fragmentTag
        )
        fragmentTransaction.commitAllowingStateLoss()
    }

    companion object {

        fun start(context: Context) {
            val intent = build(context)
            context.startActivity(intent)
        }

        fun build(context: Context): Intent {
            return Intent(context, PlaylistsActivity::class.java)
        }
    }

}
