package foo.bar.example.foreapollo3.ui.launch


import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.ui.showOrGone
import co.early.fore.kt.core.ui.showOrInvisible
import coil.load
import foo.bar.example.foreapollo3.OG
import foo.bar.example.foreapollo3.R
import foo.bar.example.foreapollo3.feature.FailureCallback
import foo.bar.example.foreapollo3.feature.SuccessCallback
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import foo.bar.example.foreapollo3.message.ErrorMessage
import kotlinx.android.synthetic.main.activity_launches.*


class LaunchActivity : FragmentActivity(R.layout.activity_launches) {


    //models that we need to sync with
    private val launchesModel: LaunchesModel = OG[LaunchesModel::class.java]
    private val authenticator: Authenticator = OG[Authenticator::class.java]

    //single observer reference
    private var observer = Observer { syncView() }


    private val success: SuccessCallback = {
        showToast("Success!")
    }
    private val failureWithPayload: FailureCallback<ErrorMessage> = { userMessage ->
        showToast(userMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        launch_fetch_btn.setOnClickListener { launchesModel.fetchLaunches(success, failureWithPayload) }
        launch_login_btn.setOnClickListener { authenticator.login("example@test.com", success, failureWithPayload) }
        launch_logout_btn.setOnClickListener { authenticator.logout() }
        launch_chain_btn.setOnClickListener { launchesModel.chainedCall(success, failureWithPayload) }
    }

    //data binding stuff below

    fun syncView() {
        launch_login_btn.showOrGone(!authenticator.hasSessionToken())
        launch_logout_btn.showOrGone(authenticator.hasSessionToken())
        launch_login_btn.isEnabled = !authenticator.isBusy
        launch_logout_btn.isEnabled = !authenticator.isBusy
        launch_fetch_btn.isEnabled = !launchesModel.isBusy
        launch_chain_btn.isEnabled = !launchesModel.isBusy && !authenticator.isBusy
        launch_session_txt.text = "session token:${authenticator.sessionToken}"
        launch_authbusy_progbar.showOrGone(authenticator.isBusy)
        launch_session_txt.showOrGone(!authenticator.isBusy)
        launch_id_textview.text = "id:" + launchesModel.currentLaunch.id
        launch_patch_img.load(launchesModel.currentLaunch.patchImgUrl)
        launch_busy_progbar.showOrInvisible(launchesModel.isBusy)
        launch_detailcontainer_linearlayout.showOrGone(!launchesModel.isBusy)
        launch_booked_txt.showOrGone(!launchesModel.isBusy && authenticator.hasSessionToken())
        launch_booked_txt.text = "booked:${launchesModel.currentLaunch.isBooked}"
    }

    override fun onStart() {
        super.onStart()
        launchesModel.addObserver(observer)
        authenticator.addObserver(observer)
        syncView() //  <- don't forget this
    }

    override fun onStop() {
        super.onStop()
        launchesModel.removeObserver(observer)
        authenticator.removeObserver(observer)
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: ErrorMessage) {
    Toast.makeText(this, message.localisedMessage, Toast.LENGTH_LONG).show()
}
