package foo.bar.example.foreapollo3.ui.launch

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import co.early.fore.kt.core.ui.showOrGone
import co.early.fore.kt.core.ui.showOrInvisible
import coil.load
import foo.bar.example.foreapollo3.OG
import foo.bar.example.foreapollo3.databinding.ActivityLaunchesBinding
import foo.bar.example.foreapollo3.feature.FailureCallback
import foo.bar.example.foreapollo3.feature.SuccessCallback
import foo.bar.example.foreapollo3.feature.authentication.Authenticator
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import foo.bar.example.foreapollo3.message.ErrorMessage

class LaunchActivity : FragmentActivity(), SyncableView {

    //models that we need to sync with
    private val launchesModel: LaunchesModel = OG[LaunchesModel::class.java]
    private val authenticator: Authenticator = OG[Authenticator::class.java]

    private lateinit var binding: ActivityLaunchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(LifecycleObserver(this, launchesModel, authenticator))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.launchFetchBtn.setOnClickListener { launchesModel.fetchLaunches(success, failureWithPayload) }
        binding.launchLoginBtn.setOnClickListener { authenticator.login("example@test.com", success, failureWithPayload) }
        binding.launchLogoutBtn.setOnClickListener { authenticator.logout() }
        binding.launchChainBtn.setOnClickListener { launchesModel.chainedCall(success, failureWithPayload) }
    }

    override fun syncView() {
        binding.launchLoginBtn.showOrGone(!authenticator.hasSessionToken())
        binding.launchLogoutBtn.showOrGone(authenticator.hasSessionToken())
        binding.launchLoginBtn.isEnabled = !authenticator.isBusy
        binding.launchLogoutBtn.isEnabled = !authenticator.isBusy
        binding.launchFetchBtn.isEnabled = !launchesModel.isBusy
        binding.launchChainBtn.isEnabled = !launchesModel.isBusy && !authenticator.isBusy
        binding.launchSessionTxt.text = "session token:${authenticator.sessionToken}"
        binding.launchAuthbusyProgbar.showOrGone(authenticator.isBusy)
        binding.launchSessionTxt.showOrGone(!authenticator.isBusy)
        binding.launchIdTextview.text = "id:" + launchesModel.currentLaunch.id
        binding.launchPatchImg.load(launchesModel.currentLaunch.patchImgUrl)
        binding.launchBusyProgbar.showOrInvisible(launchesModel.isBusy)
        binding.launchDetailcontainerLinearlayout.showOrGone(!launchesModel.isBusy)
        binding.launchBookedTxt.showOrGone(!launchesModel.isBusy && authenticator.hasSessionToken())
        binding.launchBookedTxt.text = "booked:${launchesModel.currentLaunch.isBooked}"
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
