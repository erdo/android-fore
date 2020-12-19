package foo.bar.example.foreretrofitkt.ui.fruit

import android.content.pm.ActivityInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.core.Either
import foo.bar.example.foreretrofitkt.App
import foo.bar.example.foreretrofitkt.OG
import foo.bar.example.foreretrofitkt.ProgressBarIdler
import foo.bar.example.foreretrofitkt.api.fruits.FruitPojo
import foo.bar.example.foreretrofitkt.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitkt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response


class FruitViewRotationTestStateBuilder internal constructor(private val fruitViewRotationTest: FruitViewRotationTest) {

    internal fun withDelayedCallProcessor(): FruitViewRotationTestStateBuilder {

        val deferred = CompletableDeferred<Either<ErrorMessage, List<FruitPojo>>>()

        coEvery {
            fruitViewRotationTest.mockCallProcessorRetrofit2.processCallAsync(
                any() as suspend () -> Response<List<FruitPojo>>
            )
        } returns deferred

        fruitViewRotationTest.setDeferredResult(deferred)

        return this
    }

    internal fun createRule(): ActivityTestRule<FruitActivity> {

        return object : ActivityTestRule<FruitActivity>(FruitActivity::class.java) {

            override fun beforeActivityLaunched() {

                SystemLogger().i("FruitViewRotationTestStateBuilder", "beforeActivityLaunched()")

                //get hold of the application
                val app = ApplicationProvider.getApplicationContext() as App
                app.registerActivityLifecycleCallbacks(ProgressBarIdler())

                //inject our mocks so our UI layer will pick them up
                OG.setApplication(app, WorkMode.SYNCHRONOUS)
                OG.putMock(FruitFetcher::class.java, fruitViewRotationTest.fruitFetcher)
            }

            override fun afterActivityFinished() {
                super.afterActivityFinished()

                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }
}
