package foo.bar.example.foreretrofitcoroutine.ui.fruit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import co.early.fore.core.callbacks.FailureWithPayload
import co.early.fore.core.callbacks.Success
import foo.bar.example.foreretrofit.R
import foo.bar.example.foreretrofitcoroutine.OG
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_busy_progbar
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_citrus_img
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_detailcontainer_linearlayout
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_fetchchainsuccess_btn
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_fetchfailadvanced_btn
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_fetchfailbasic_btn
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_fetchsuccess_btn
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_name_textview
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_tastyrating_tastybar
import kotlinx.android.synthetic.main.fragment_fruit.view.fruit_tastyrating_textview

/**
 *
 */
class FruitView @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    //models that we need to sync with
    private lateinit var fruitFetcher: FruitFetcher


    //single observer reference
    private var observer = this::syncView


    //just because we re-use these in 3 different button clicks
    //in this example we define them here
    private val success: Success = {
        Toast.makeText(
            context, "Success - you can use this trigger to " +
                    "perform a one off action like starting a new activity or " +
                    "something", Toast.LENGTH_SHORT
        ).show()
    }
    private val failureWithPayload: FailureWithPayload<UserMessage> = { userMessage ->
        Toast.makeText(
            context, "Fail - maybe tell the user to try again, message:" + userMessage.localisedMessage,
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun onFinishInflate() {
        super.onFinishInflate()

        getModelReferences()

        setupButtonClickListeners()
    }

    private fun getModelReferences() {
        fruitFetcher = OG[FruitFetcher::class.java]
    }

    private fun setupButtonClickListeners() {
        fruit_fetchsuccess_btn.setOnClickListener { fruitFetcher.fetchFruits(success, failureWithPayload) }
        fruit_fetchchainsuccess_btn.setOnClickListener { fruitFetcher.fetchManyThings(success, failureWithPayload) }
        fruit_fetchfailbasic_btn.setOnClickListener { fruitFetcher.fetchFruitsButFail(success, failureWithPayload) }
        fruit_fetchfailadvanced_btn.setOnClickListener { fruitFetcher.fetchFruitsButFailAdvanced(success, failureWithPayload) }
    }


    //data binding stuff below

    fun syncView() {
        fruit_fetchsuccess_btn.isEnabled = !fruitFetcher.isBusy
        fruit_fetchchainsuccess_btn.isEnabled = !fruitFetcher.isBusy
        fruit_fetchfailbasic_btn.isEnabled = !fruitFetcher.isBusy
        fruit_fetchfailadvanced_btn.isEnabled = !fruitFetcher.isBusy
        fruit_name_textview.text = fruitFetcher.currentFruit.name
        fruit_citrus_img.setImageResource(
            if (fruitFetcher.currentFruit.isCitrus)
                R.drawable.lemon_positive else R.drawable.lemon_negative
        )
        fruit_tastyrating_tastybar.setTastyPercent(fruitFetcher.currentFruit.tastyPercentScore.toFloat())
        fruit_tastyrating_textview.text = String.format(
            context.getString(R.string.fruit_percent), fruitFetcher.currentFruit.tastyPercentScore.toString()
        )
        fruit_busy_progbar.visibility = if (fruitFetcher.isBusy)
            View.VISIBLE else View.INVISIBLE
        fruit_detailcontainer_linearlayout.visibility = if (fruitFetcher.isBusy)
            View.GONE else View.VISIBLE
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fruitFetcher.addObserver(observer)
        syncView() //  <- don't forget this
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fruitFetcher.removeObserver(observer)
    }
}
