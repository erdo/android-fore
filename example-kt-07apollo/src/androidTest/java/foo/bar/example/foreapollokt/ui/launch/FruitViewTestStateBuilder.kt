
package foo.bar.example.foreapollokt.ui.launch

import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import foo.bar.example.foreapollokt.ProgressBarIdler
import foo.bar.example.foreapollokt.App
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.api.fruits.Launch
import foo.bar.example.foreapollokt.feature.launch.FruitFetcher
import io.mockk.every


class FruitViewTestStateBuilder internal constructor(private val mockFruitFetcher: FruitFetcher) {

    internal fun isBusy(busy: Boolean): FruitViewTestStateBuilder {
        every { mockFruitFetcher.isBusy } returns (busy)
        return this
    }

    internal fun hasFruit(launch: Launch): FruitViewTestStateBuilder {
        every { mockFruitFetcher.currentFruit } returns (launch)
        return this
    }

    internal fun createRule(): ActivityTestRule<LaunchActivity> {

        return object : ActivityTestRule<LaunchActivity>(LaunchActivity::class.java) {
            override fun beforeActivityLaunched() {

                //get hold of the application
                val app = ApplicationProvider.getApplicationContext() as App
                app.registerActivityLifecycleCallbacks(ProgressBarIdler())

                //inject our mocks so our UI layer will pick them up
                OG.setApplication(app, WorkMode.SYNCHRONOUS)
                OG.putMock(FruitFetcher::class.java, mockFruitFetcher)
            }
        }
    }

}
