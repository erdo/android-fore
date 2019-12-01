
package foo.bar.example.foreretrofit.ui.fruit

import android.content.pm.ActivityInfo
import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import co.early.fore.core.WorkMode
import co.early.fore.core.callbacks.SuccessWithPayload
import co.early.fore.core.logging.SystemLogger
import co.early.fore.retrofit.MessageProvider
import foo.bar.example.foreretrofit.ProgressBarIdler
import foo.bar.example.foreretrofitcoroutine.App
import foo.bar.example.foreretrofitcoroutine.OG
import foo.bar.example.foreretrofitcoroutine.api.fruits.FruitPojo
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import foo.bar.example.foreretrofitcoroutine.ui.fruit.FruitActivity
import io.mockk.every
import io.mockk.slot


class FruitViewRotationTestStateBuilder internal constructor(private val fruitViewRotationTest: FruitViewRotationTest) {

    internal fun withDelayedCallProcessor(): FruitViewRotationTestStateBuilder {

        val slot = slot<co.early.fore.core.callbacks.SuccessWithPayload<List<FruitPojo>>>()

        every {
            fruitViewRotationTest.mockCallProcessor.processCall(any(), any() as Class<MessageProvider<UserMessage>>, capture(slot), any(), any())
        } answers {
            // We need to store this callback so that we can trigger it later
            // We can't use any kind of post delayed thing because
            // espresso needs all threads to be idle before proceeding
            // from this method
            fruitViewRotationTest.setCachedSuccessCallback(slot.captured)
        }

        return this
    }

    internal fun createRule(): ActivityTestRule<FruitActivity> {

        return object : ActivityTestRule<FruitActivity>(FruitActivity::class.java) {

            override fun beforeActivityLaunched() {

                SystemLogger().i("FruitViewRotationTestStateBuilder", "beforeActivityLaunched()")

                //get hold of the application
                val application = InstrumentationRegistry.getTargetContext().applicationContext as App
                application.registerActivityLifecycleCallbacks(ProgressBarIdler())

                //inject our mocks so our UI layer will pick them up
                OG.setApplication(application, WorkMode.SYNCHRONOUS)
                OG.putMock(FruitFetcher::class.java, fruitViewRotationTest.fruitFetcher)
            }

            override fun afterActivityFinished() {
                super.afterActivityFinished()

                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

}
