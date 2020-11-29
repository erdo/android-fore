package foo.bar.example.foreapollokt.ui.launch

import android.content.pm.ActivityInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.apollo.Either
import foo.bar.example.foreapollokt.App
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.ProgressBarIdler
import foo.bar.example.foreapollokt.api.fruits.Launch
import foo.bar.example.foreapollokt.feature.launch.FruitFetcher
import foo.bar.example.foreapollokt.message.UserMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Response


class FruitViewRotationTestStateBuilder internal constructor(private val fruitViewRotationTest: FruitViewRotationTest) {

    internal fun withDelayedCallProcessor(): FruitViewRotationTestStateBuilder {

        val deferred = CompletableDeferred<Either<UserMessage, List<Launch>>>()

        coEvery {
            fruitViewRotationTest.mockCallProcessor.processCallAsync(
                any() as suspend () -> Response<List<Launch>>
            )
        } returns deferred

        fruitViewRotationTest.setDeferredResult(deferred)

        return this
    }

    internal fun createRule(): ActivityTestRule<LaunchActivity> {

        return object : ActivityTestRule<LaunchActivity>(LaunchActivity::class.java) {

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
