package foo.bar.example.foreapollokt.ui.launch


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.callbacks.FailureWithPayload
import co.early.fore.kt.core.callbacks.Success
import coil.load
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.R
import foo.bar.example.foreapollokt.feature.authentication.Authenticator
import foo.bar.example.foreapollokt.feature.launch.LaunchesModel
import foo.bar.example.foreapollokt.message.ErrorMessage
import kotlinx.android.synthetic.main.activity_launches.*


class LaunchActivity : FragmentActivity(R.layout.activity_launches) {


    //models that we need to sync with
    private val launchesModel: LaunchesModel = OG[LaunchesModel::class.java]
    private val authenticator: Authenticator = OG[Authenticator::class.java]

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
        fruit_fetchsuccess_btn.setOnClickListener { launchesModel.fetchLaunches(success, failureWithPayload) }
        launch_login_btn.setOnClickListener { authenticator.login("example@test.com", success, failureWithPayload) }
        launch_fetchchainsuccess_btn.setOnClickListener { launchesModel.refreshLaunchDetails(success, failureWithPayload) }
    }


    //data binding stuff below

    fun syncView() {
        fruit_fetchsuccess_btn.isEnabled = !launchesModel.isBusy
        launch_fetchchainsuccess_btn.isEnabled = !launchesModel.isBusy
        fruit_name_textview.text = launchesModel.currentLaunch.site
        fruit_citrus_img.load(launchesModel.currentLaunch.patchImgUrl) {
            crossfade(true)
            placeholder(R.drawable.lemon_negative)
        }
        fruit_busy_progbar.visibility = if (launchesModel.isBusy)
            View.VISIBLE else View.INVISIBLE
        fruit_detailcontainer_linearlayout.visibility = if (launchesModel.isBusy)
            View.GONE else View.VISIBLE
    }


    override fun onStart() {
        super.onStart()
        launchesModel.addObserver(observer)
        syncView() //  <- don't forget this
    }


    override fun onStop() {
        super.onStop()
        launchesModel.removeObserver(observer)
    }
}
