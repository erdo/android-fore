package foo.bar.example.foreretrofitkt.ui.fruit


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import foo.bar.example.foreretrofitkt.OG
import foo.bar.example.foreretrofitkt.R
import foo.bar.example.foreretrofitkt.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import kotlinx.android.synthetic.main.activity_fruit.*


class FruitActivity : FragmentActivity(R.layout.activity_fruit) {


    //models that we need to sync with
    private val fruitFetcher: FruitFetcher = OG[FruitFetcher::class.java]


    //single observer reference
    private var observer = Observer { syncView() }


    //just because we re-use these in 3 different button clicks
    //in this example we define them here
    private val success: Success = {
        Toast.makeText(
            this, "Success - you can use this trigger to " +
                    "perform a one off action like starting a new activity or " +
                    "something", Toast.LENGTH_SHORT
        ).show()
    }
    private val failureWithPayload: FailureWithPayload<ErrorMessage> = { userMessage ->
        Toast.makeText(
            this, "Fail - maybe tell the user to try again, message:" + userMessage.localisedMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupButtonClickListeners()
    }


    private fun setupButtonClickListeners() {
        fruit_fetchsuccess_btn.setOnClickListener { fruitFetcher.fetchFruitsAsync(success, failureWithPayload) }
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
            this.getString(R.string.fruit_percent), fruitFetcher.currentFruit.tastyPercentScore.toString()
        )
        fruit_busy_progbar.visibility = if (fruitFetcher.isBusy)
            View.VISIBLE else View.INVISIBLE
        fruit_detailcontainer_linearlayout.visibility = if (fruitFetcher.isBusy)
            View.GONE else View.VISIBLE
    }


    override fun onStart() {
        super.onStart()
        fruitFetcher.addObserver(observer)
        syncView() //  <- don't forget this
    }


    override fun onStop() {
        super.onStop()
        fruitFetcher.removeObserver(observer)
    }
}
