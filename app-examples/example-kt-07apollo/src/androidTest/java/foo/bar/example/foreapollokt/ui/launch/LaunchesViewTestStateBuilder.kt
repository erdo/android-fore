
package foo.bar.example.foreapollokt.ui.launch

import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import foo.bar.example.foreapollokt.ProgressBarIdler
import foo.bar.example.foreapollokt.App
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.feature.launch.Launch
import foo.bar.example.foreapollokt.feature.launch.LaunchesModel
import io.mockk.every


class LaunchesViewTestStateBuilder internal constructor(private val mockLaunchesModel: LaunchesModel) {

    internal fun isBusy(busy: Boolean): LaunchesViewTestStateBuilder {
        every { mockLaunchesModel.isBusy } returns (busy)
        return this
    }

    internal fun hasLaunch(launch: Launch): LaunchesViewTestStateBuilder {
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
