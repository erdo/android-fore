package foo.bar.example.foreapollokt.ui.launch

import android.content.pm.ActivityInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.kt.net.apollo.CallProcessorApollo
import com.apollographql.apollo.ApolloCall
import foo.bar.example.foreapollokt.App
import foo.bar.example.foreapollokt.OG
import foo.bar.example.foreapollokt.ProgressBarIdler
import foo.bar.example.foreapollokt.feature.launch.LaunchesModel
import foo.bar.example.foreapollokt.graphql.LaunchListQuery
import foo.bar.example.foreapollokt.message.ErrorMessage
import io.mockk.coEvery
import kotlinx.coroutines.CompletableDeferred


class LaunchViewRotationTestStateBuilder internal constructor(private val launchViewRotationTest: LaunchViewRotationTest) {

    internal fun withDelayedCallProcessor(): LaunchViewRotationTestStateBuilder {

        val deferred = CompletableDeferred<Either<ErrorMessage, CallProcessorApollo.SuccessResult<LaunchListQuery.Data>>>()

        coEvery {
            launchViewRotationTest.mockCallProcessorApollo.processCallAsync(
                any() as () -> ApolloCall<LaunchListQuery.Data>
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
                OG.setApplication(app)
                OG.putMock(LaunchesModel::class.java, launchViewRotationTest.launchesModel)
            }

            override fun afterActivityFinished() {
                super.afterActivityFinished()

                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }
}
