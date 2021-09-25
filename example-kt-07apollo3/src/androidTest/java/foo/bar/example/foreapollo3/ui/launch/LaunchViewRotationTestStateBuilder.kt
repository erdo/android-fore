package foo.bar.example.foreapollo3.ui.launch

import android.content.pm.ActivityInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.apollo3.CallProcessorApollo3
import com.apollographql.apollo3.api.ApolloResponse
import foo.bar.example.foreapollo3.App
import foo.bar.example.foreapollo3.LaunchListQuery
import foo.bar.example.foreapollo3.OG
import foo.bar.example.foreapollo3.ProgressBarIdler
import foo.bar.example.foreapollo3.feature.launch.LaunchesModel
import foo.bar.example.foreapollo3.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred


@ExperimentalStdlibApi
class LaunchViewRotationTestStateBuilder internal constructor(private val launchViewRotationTest: LaunchViewRotationTest) {

    internal fun withDelayedCallProcessor(): LaunchViewRotationTestStateBuilder {

        val deferred = CompletableDeferred<Either<ErrorMessage, CallProcessorApollo3.SuccessResult<LaunchListQuery.Data>>>()

        coEvery {
            launchViewRotationTest.mockCallProcessorApollo.processCallAsync(
                any() as suspend () -> ApolloResponse<LaunchListQuery.Data>
            )
        } returns deferred

        launchViewRotationTest.setDeferredResult(deferred)

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
                OG.putMock(LaunchesModel::class.java, launchViewRotationTest.launchesModel)
            }

            override fun afterActivityFinished() {
                super.afterActivityFinished()

                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }
}
