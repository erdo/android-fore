package foo.bar.example.forecoroutine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import foo.bar.example.forecoroutine.OG
import foo.bar.example.forecoroutine.databinding.ActivityCounterBinding
import foo.bar.example.forecoroutine.feature.counter.Counter
import foo.bar.example.forecoroutine.feature.counter.CounterWithProgress

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class CounterActivity : FragmentActivity(), SyncableView {

    //models that we need to sync with
    private val counterWithProgress: CounterWithProgress = OG[CounterWithProgress::class.java]
    private val counter: Counter = OG[Counter::class.java]

    private lateinit var binding: ActivityCounterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCounterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(LifecycleObserver(this, counter, counterWithProgress))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.counterIncreaseBtn.setOnClickListener { counter.increaseBy20() }
        binding.counterwprogIncreaseBtn.setOnClickListener { counterWithProgress.increaseBy20() }
    }

    override fun syncView() {
        binding.counterwprogIncreaseBtn.isEnabled = !counterWithProgress.isBusy
        binding.counterwprogBusyProgress.visibility = if (counterWithProgress.isBusy) View.VISIBLE else View.INVISIBLE
        binding.counterwprogProgressTxt.text = "${counterWithProgress.progress}"
        binding.counterwprogCurrentTxt.text = "${counterWithProgress.count}"

        binding.counterIncreaseBtn.isEnabled = !counter.isBusy
        binding.counterBusyProgress.visibility = if (counter.isBusy) View.VISIBLE else View.INVISIBLE
        binding.counterCurrentTxt.text = "${counter.count}"
    }
}
