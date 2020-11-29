
package foo.bar.example.foreapollokt.ui.fruit

import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import foo.bar.example.foreapollokt.ProgressBarIdler
import foo.bar.example.foreapollokt.App
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.api.fruits.FruitPojo
import foo.bar.example.foreapollokt.feature.fruit.FruitFetcher
import io.mockk.every


class FruitViewTestStateBuilder internal constructor(private val mockFruitFetcher: FruitFetcher) {

    internal fun isBusy(busy: Boolean): FruitViewTestStateBuilder {
        every { mockFruitFetcher.isBusy } returns (busy)
        return this
    }

    internal fun hasFruit(fruitPojo: FruitPojo): FruitViewTestStateBuilder {
        every { mockFruitFetcher.currentFruit } returns (fruitPojo)
        return this
    }

    internal fun createRule(): ActivityTestRule<FruitActivity> {

        return object : ActivityTestRule<FruitActivity>(FruitActivity::class.java) {
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