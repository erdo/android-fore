package foo.bar.example.forektorkt.ui.fruit

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import co.early.fore.kt.core.ui.showOrGone
import co.early.fore.kt.core.ui.showOrInvisible
import foo.bar.example.forektorkt.OG
import foo.bar.example.forektorkt.R
import foo.bar.example.forektorkt.databinding.ActivityFruitBinding
import foo.bar.example.forektorkt.feature.fruit.FruitFetcher
import foo.bar.example.forektorkt.message.ErrorMessage

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

        binding.fruitFetchsuccessBtn.setOnClickListener {
            fruitFetcher.fetchFruitsAsync({
                showToast("success!")
            }, { userMessage ->
                showToast(userMessage)
            })
        }

        binding.fruitFetchchainsuccessBtn.setOnClickListener {
            fruitFetcher.chainedCall({
                showToast("success!")
            }, { userMessage ->
                showToast(userMessage)
            })
        }

        binding.fruitFetchfailbasicBtn.setOnClickListener {
            fruitFetcher.fetchFruitsButFail({
                showToast("success!")
            }, { userMessage ->
                showToast(userMessage)
            })
        }

        binding.fruitFetchfailadvancedBtn.setOnClickListener {
            fruitFetcher.fetchFruitsButFailAdvanced({
                showToast("success!")
            }, { userMessage ->
                showToast(userMessage)
            })
        }
    }

    override fun syncView() {
        binding.fruitFetchsuccessBtn.isEnabled = !fruitFetcher.isBusy
        binding.fruitFetchchainsuccessBtn.isEnabled = !fruitFetcher.isBusy
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
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: ErrorMessage) {
    Toast.makeText(this, message.localisedMessage, Toast.LENGTH_LONG).show()
}
