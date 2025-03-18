package foo.bar.example.foreapollo3.ui.launch

import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import foo.bar.example.foreapollo3.App
import foo.bar.example.foreapollo3.OG
import foo.bar.example.foreapollo3.ProgressBarIdler
import foo.bar.example.foreapollo3.feature.launch.Launch
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import io.mockk.every


@ExperimentalStdlibApi
class LaunchViewTestStateBuilder internal constructor(private val mockLaunchesModel: LaunchesModel) {

    internal fun isBusy(busy: Boolean): LaunchViewTestStateBuilder {
        every { mockLaunchesModel.isBusy } returns (busy)
        return this
    }

    internal fun hasLaunch(launch: Launch): LaunchViewTestStateBuilder {
        every { mockLaunchesModel.currentLaunch } returns (launch)
        return this
    }

    internal fun createRule(): ActivityTestRule<LaunchActivity> {

        return object : ActivityTestRule<LaunchActivity>(LaunchActivity::class.java) {
            override fun beforeActivityLaunched() {

                //get hold of the application
                val app = ApplicationProvider.getApplicationContext() as App
                app.registerActivityLifecycleCallbacks(ProgressBarIdler())

                //inject our mocks so our UI layer will pick them up
                OG.setApplication(app)
                OG.putMock(LaunchesModel::class.java, mockLaunchesModel)
            }
        }
    }

}
