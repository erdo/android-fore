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
import foo.bar.example.foreretrofitkt.databinding.ActivityFruitBinding
import foo.bar.example.foreretrofitkt.feature.fruit.FailureCallback
import foo.bar.example.foreretrofitkt.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitkt.feature.fruit.SuccessCallback
import foo.bar.example.foreretrofitkt.message.ErrorMessage

class FruitActivity : FragmentActivity(), SyncableView {

    //models that we need to sync with
    private val fruitFetcher: FruitFetcher = OG[FruitFetcher::class.java]

    private lateinit var binding: ActivityFruitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFruitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(LifecycleObserver(this, fruitFetcher))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.fruitFetchsuccessBtn.setOnClickListener { fruitFetcher.fetchFruitsAsync(success, failureWithPayload) }
        binding.fruitFetchchainsuccessBtn.setOnClickListener { fruitFetcher.chainedCall(success, failureWithPayload) }
        binding.fruitFetchfailbasicBtn.setOnClickListener { fruitFetcher.fetchFruitsButFail(success, failureWithPayload) }
        binding.fruitFetchfailadvancedBtn.setOnClickListener { fruitFetcher.fetchFruitsButFailAdvanced(success, failureWithPayload) }
    }

    override fun syncView() {
        binding.fruitFetchsuccessBtn.isEnabled = !fruitFetcher.isBusy
        binding.fruitFetchsuccessBtn.isEnabled = !fruitFetcher.isBusy
        binding.fruitFetchfailbasicBtn.isEnabled = !fruitFetcher.isBusy
        binding.fruitFetchfailadvancedBtn.isEnabled = !fruitFetcher.isBusy
        binding.fruitNameTextview.text = fruitFetcher.currentFruit.name
        binding.fruitCitrusImg.setImageResource(
            if (fruitFetcher.currentFruit.isCitrus)
                R.drawable.lemon_positive else R.drawable.lemon_negative
        )
        binding.fruitTastyratingTastybar.setTastyPercent(fruitFetcher.currentFruit.tastyPercentScore.toFloat())
        binding.fruitTastyratingTextview.text = String.format(
            this.getString(R.string.fruit_percent), fruitFetcher.currentFruit.tastyPercentScore.toString()
        )
        binding.fruitBusyProgbar.showOrInvisible(fruitFetcher.isBusy)
        binding.fruitDetailcontainerLinearlayout.showOrGone(!fruitFetcher.isBusy)
    }

    private val success: SuccessCallback = {
        showToast("Success!")
    }
    private val failureWithPayload: FailureCallback<ErrorMessage> = { userMessage ->
        showToast(userMessage)
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: ErrorMessage) {
    Toast.makeText(this, message.localisedMessage, Toast.LENGTH_LONG).show()
}