
package foo.bar.example.foreretrofit.ui.fruit

import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import foo.bar.example.foreretrofit.ProgressBarIdler
import foo.bar.example.foreretrofitcoroutine.App
import foo.bar.example.foreretrofitcoroutine.OG
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitcoroutine.ui.fruit.FruitActivity
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
                val application = InstrumentationRegistry.getTargetContext().applicationContext as App
                application.registerActivityLifecycleCallbacks(ProgressBarIdler())

                //inject our mocks so our UI layer will pick them up
                OG.setApplication(application, WorkMode.SYNCHRONOUS)
                OG.putMock(FruitFetcher::class.java, mockFruitFetcher)
            }
        }
    }

}
