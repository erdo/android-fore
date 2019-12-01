/*
package foo.bar.example.foreretrofit.ui.fruit

import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import foo.bar.example.foreretrofit.ProgressBarIdler
import foo.bar.example.foreretrofitcoroutine.CustomApp
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitcoroutine.ui.fruit.FruitActivity
import org.mockito.Mockito.`when`

*/
/**
 *
 *//*

class FruitViewTestStateBuilder internal constructor(private val mockFruitFetcher: FruitFetcher) {

    internal fun isBusy(busy: Boolean): FruitViewTestStateBuilder {
        `when`(mockFruitFetcher.isBusy).thenReturn(busy)
        return this
    }

    internal fun hasFruit(fruitPojo: FruitPojo): FruitViewTestStateBuilder {
        `when`(mockFruitFetcher.currentFruit).thenReturn(fruitPojo)
        return this
    }

    internal fun createRule(): ActivityTestRule<FruitActivity> {

        return object : ActivityTestRule<FruitActivity>(FruitActivity::class.java) {
            override fun beforeActivityLaunched() {

                //get hold of the application
                val customApp = InstrumentationRegistry.getTargetContext().applicationContext as CustomApp
                customApp.injectSynchronousObjectGraph()

                //inject our mocks so our UI layer will pick them up
                customApp.injectMockObject(FruitFetcher::class.java, mockFruitFetcher)
                customApp.registerActivityLifecycleCallbacks(ProgressBarIdler())
            }

        }
    }

}
*/
