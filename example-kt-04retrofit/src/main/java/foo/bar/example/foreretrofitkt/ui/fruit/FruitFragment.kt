package foo.bar.example.foreretrofitkt.ui.fruit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import foo.bar.example.foreretrofitkt.R


class FruitFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fruit, null)
    }

    companion object {
        fun newInstance(): FruitFragment {
            return FruitFragment()
        }
    }

}
