package foo.bar.example.foreretrofitcoroutine.ui.fruit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import foo.bar.example.foreretrofit.R


class FruitActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.common_activity_base)

        if (savedInstanceState == null) {
            setFragment(
                FruitFragment.newInstance(),
                FruitFragment::class.java.simpleName
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
            return Intent(context, FruitActivity::class.java)
        }
    }

}
