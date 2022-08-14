package foo.bar.example.foreretrofitkt.ui.fruit

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import co.early.fore.kt.core.ui.showOrGone
import co.early.fore.kt.core.ui.showOrInvisible
import foo.bar.example.foreretrofitkt.OG
import foo.bar.example.foreretrofitkt.R
import foo.bar.example.foreretrofitkt.feature.fruit.FailureCallback
import foo.bar.example.foreretrofitkt.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitkt.feature.fruit.SuccessCallback
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import kotlinx.android.synthetic.main.activity_fruit.*

class FruitActivity : FragmentActivity(R.layout.activity_fruit), SyncableView {


    //models that we need to sync with
    private val fruitFetcher: FruitFetcher = OG[FruitFetcher::class.java]


    private val success: SuccessCallback = {
        showToast("Success!")
    }
    private val failureWithPayload: FailureCallback<ErrorMessage> = { userMessage ->
        showToast(userMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(LifecycleObserver(this, fruitFetcher))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        fruit_fetchsuccess_btn.setOnClickListener { fruitFetcher.fetchFruitsAsync(success, failureWithPayload) }
        fruit_fetchchainsuccess_btn.setOnClickListener { fruitFetcher.chainedCall(success, failureWithPayload) }
        fruit_fetchfailbasic_btn.setOnClickListener { fruitFetcher.fetchFruitsButFail(success, failureWithPayload) }
        fruit_fetchfailadvanced_btn.setOnClickListener { fruitFetcher.fetchFruitsButFailAdvanced(success, failureWithPayload) }
    }

    override fun syncView() {
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
            this.getString(R.string.fruit_percent), fruitFetcher.currentFruit.tastyPercentScore.toString()
        )
        fruit_busy_progbar.showOrInvisible(fruitFetcher.isBusy)
        fruit_detailcontainer_linearlayout.showOrGone(!fruitFetcher.isBusy)
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: ErrorMessage) {
    Toast.makeText(this, message.localisedMessage, Toast.LENGTH_LONG).show()
}