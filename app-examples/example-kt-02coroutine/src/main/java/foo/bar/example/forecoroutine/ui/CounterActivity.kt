package foo.bar.example.forecoroutine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import foo.bar.example.forecoroutine.OG
import foo.bar.example.forecoroutine.R
import foo.bar.example.forecoroutine.feature.counter.Counter
import foo.bar.example.forecoroutine.feature.counter.CounterWithProgress
import kotlinx.android.synthetic.main.activity_counter.*

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class CounterActivity : FragmentActivity(R.layout.activity_counter), SyncableView {


    //models that we need to sync with
    private val counterWithProgress: CounterWithProgress = OG[CounterWithProgress::class.java]
    private val counter: Counter = OG[Counter::class.java]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(LifecycleObserver(this, counter, counterWithProgress))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        counter_increase_btn.setOnClickListener { counter.increaseBy20() }
        counterwprog_increase_btn.setOnClickListener { counterWithProgress.increaseBy20() }
    }

    override fun syncView() {
        counterwprog_increase_btn.isEnabled = !counterWithProgress.isBusy
        counterwprog_busy_progress.visibility = if (counterWithProgress.isBusy) View.VISIBLE else View.INVISIBLE
        counterwprog_progress_txt.text = "${counterWithProgress.progress}"
        counterwprog_current_txt.text = "${counterWithProgress.count}"

        counter_increase_btn.isEnabled = !counter.isBusy
        counter_busy_progress.visibility = if (counter.isBusy) View.VISIBLE else View.INVISIBLE
        counter_current_txt.text = "${counter.count}"
    }
}
