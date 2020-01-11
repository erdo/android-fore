package foo.bar.example.forecoroutine.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import foo.bar.example.forecoroutine.R

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class CounterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_counter, null)
    }

    companion object {

        fun newInstance(): CounterFragment {
            return CounterFragment()
        }
    }

}
