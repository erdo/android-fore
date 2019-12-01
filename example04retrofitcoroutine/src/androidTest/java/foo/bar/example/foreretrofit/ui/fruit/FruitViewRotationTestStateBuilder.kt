/*
package foo.bar.example.foreretrofit.ui.fruit

import android.content.pm.ActivityInfo
import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule

import org.mockito.ArgumentCaptor

import co.early.fore.core.callbacks.SuccessCallbackWithPayload
import co.early.fore.core.callbacks.SuccessWithPayload
import co.early.fore.core.logging.SystemLogger
import foo.bar.example.foreretrofitcoroutine.CustomApp
import foo.bar.example.foreretrofit.ProgressBarIdler
import foo.bar.example.foreretrofitcoroutine.feature.fruit.FruitFetcher
import foo.bar.example.foreretrofitcoroutine.message.UserMessage
import foo.bar.example.foreretrofitcoroutine.ui.fruit.FruitActivity
import org.mockito.ArgumentMatchers

import org.mockito.Matchers.any
import org.mockito.Mockito.doAnswer

*/
/**
 *
 *//*

class FruitViewRotationTestStateBuilder internal constructor(private val fruitViewRotationTest: FruitViewRotationTest) {

    internal fun withDelayedCallProcessor(): FruitViewRotationTestStateBuilder {



//        argumentCaptor<String>().apply {
//            verify(myClass, times(2)).setItems(capture())
//
//            assertEquals(2, allValues.size)
//            assertEquals("test", firstValue)
//        }
//
//
//
//

        val callback = ArgumentCaptor.forClass(SuccessWithPayload<*>::class.java)
        doAnswer { __ ->

            // We need to store this callback so that we can trigger it later
            // We can't use any kind of post delayed thing because
            // espresso needs all threads to be idle before proceeding
            // from this method
            fruitViewRotationTest.setCachedSuccessCallback(callback.value)

            null
        }
            .`when`<CallProcessor<UserMessage>>(fruitViewRotationTest.mockCallProcessor)
            .processCall(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), callback.capture(), ArgumentMatchers.any())

        return this
    }

    internal fun createRule(): ActivityTestRule<FruitActivity> {

        return object : ActivityTestRule<FruitActivity>(FruitActivity::class.java) {
            override fun beforeActivityLaunched() {

                SystemLogger().i("FruitViewRotationTestStateBuilder", "beforeActivityLaunched()")

                val customApp = InstrumentationRegistry.getTargetContext().applicationContext as CustomApp
                customApp.injectSynchronousObjectGraph()
                //inject our test model
                customApp.injectMockObject(FruitFetcher::class.java, fruitViewRotationTest.fruitFetcher)
                customApp.registerActivityLifecycleCallbacks(ProgressBarIdler())

            }

            override fun afterActivityFinished() {
                super.afterActivityFinished()

                this.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

}
*/
