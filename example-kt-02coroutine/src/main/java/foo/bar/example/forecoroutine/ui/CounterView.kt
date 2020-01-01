package foo.bar.example.forecoroutine.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import foo.bar.example.forecoroutine.OG
import foo.bar.example.forecoroutine.feature.counter.Counter
import foo.bar.example.forecoroutine.feature.counter.CounterWithProgress
import kotlinx.android.synthetic.main.fragment_counter.view.counter_busy_progress
import kotlinx.android.synthetic.main.fragment_counter.view.counter_current_txt
import kotlinx.android.synthetic.main.fragment_counter.view.counter_increase_btn
import kotlinx.android.synthetic.main.fragment_counter.view.counterwprog_busy_progress
import kotlinx.android.synthetic.main.fragment_counter.view.counterwprog_current_txt
import kotlinx.android.synthetic.main.fragment_counter.view.counterwprog_increase_btn
import kotlinx.android.synthetic.main.fragment_counter.view.counterwprog_progress_txt

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
class CounterView @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {


    //models that we need to sync with
    private lateinit var counterWithProgress: CounterWithProgress
    private lateinit var counter: Counter


    //single observer reference
    private var observer = this::syncView


    override fun onFinishInflate() {
        super.onFinishInflate()

        getModelReferences()

        setupButtonClickListeners()
    }

    private fun getModelReferences() {
        counter = OG[Counter::class.java]
        counterWithProgress = OG[CounterWithProgress::class.java]
    }

    private fun setupButtonClickListeners() {
        counter_increase_btn.setOnClickListener { counter.increaseBy20() }
        counterwprog_increase_btn.setOnClickListener { counterWithProgress.increaseBy20() }
    }


    //data binding stuff below

    fun syncView() {
        counterwprog_increase_btn.isEnabled = !counterWithProgress.isBusy
        counterwprog_busy_progress.visibility = if (counterWithProgress.isBusy) View.VISIBLE else View.INVISIBLE
        counterwprog_progress_txt.text = "${counterWithProgress.progress}"
        counterwprog_current_txt.text = "${counterWithProgress.count}"

        counter_increase_btn.isEnabled = !counter.isBusy
        counter_busy_progress.visibility = if (counter.isBusy) View.VISIBLE else View.INVISIBLE
        counter_current_txt.text = "${counter.count}"
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        counter.addObserver(observer)
        counterWithProgress.addObserver(observer)
        syncView() //<-- don't forget this
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        counter.removeObserver(observer)
        counterWithProgress.removeObserver(observer)
    }
}
