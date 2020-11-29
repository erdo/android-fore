package foo.bar.example.foreapollokt.ui.launch


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.R
import foo.bar.example.foreapollokt.feature.launch.LaunchFetcher
import foo.bar.example.foreapollokt.message.UserMessage
import kotlinx.android.synthetic.main.activity_fruit.*


class LaunchActivity : FragmentActivity(R.layout.activity_fruit) {


    //models that we need to sync with
    private val launchFetcher: LaunchFetcher = OG[LaunchFetcher::class.java]


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
    private val failureWithPayload: FailureWithPayload<UserMessage> = { userMessage ->
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
        fruit_fetchsuccess_btn.setOnClickListener { launchFetcher.fetchLaunchesAsync(success, failureWithPayload) }
        fruit_fetchchainsuccess_btn.setOnClickListener { launchFetcher.fetchManyThings(success, failureWithPayload) }
        fruit_fetchfailbasic_btn.setOnClickListener { launchFetcher.fetchLaunchesButFail(success, failureWithPayload) }
        fruit_fetchfailadvanced_btn.setOnClickListener { launchFetcher.fetchLaunchesButFailAdvanced(success, failureWithPayload) }
    }


    //data binding stuff below

    fun syncView() {
        fruit_fetchsuccess_btn.isEnabled = !launchFetcher.isBusy
        fruit_fetchchainsuccess_btn.isEnabled = !launchFetcher.isBusy
        fruit_fetchfailbasic_btn.isEnabled = !launchFetcher.isBusy
        fruit_fetchfailadvanced_btn.isEnabled = !launchFetcher.isBusy
        fruit_name_textview.text = launchFetcher.currentLaunch.site
        fruit_citrus_img.setImageResource(
            if (launchFetcher.currentLaunch.isCitrus)
                R.drawable.lemon_positive else R.drawable.lemon_negative
        )
        fruit_tastyrating_tastybar.setTastyPercent(launchFetcher.currentLaunch.tastyPercentScore.toFloat())
        fruit_tastyrating_textview.text = String.format(
            this.getString(R.string.fruit_percent), launchFetcher.currentLaunch.tastyPercentScore.toString()
        )
        fruit_busy_progbar.visibility = if (launchFetcher.isBusy)
            View.VISIBLE else View.INVISIBLE
        fruit_detailcontainer_linearlayout.visibility = if (launchFetcher.isBusy)
            View.GONE else View.VISIBLE
    }


    override fun onStart() {
        super.onStart()
        launchFetcher.addObserver(observer)
        syncView() //  <- don't forget this
    }


    override fun onStop() {
        super.onStop()
        launchFetcher.removeObserver(observer)
    }
}
