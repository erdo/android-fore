package foo.bar.example.forektorkt

import android.R
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage.RESUMED

/**
 * If you have any spinning progress bars in your UI it will mess
 * up espresso tests (espresso will time out waiting for all UI
 * threads to be idle before proceeding).
 *
 *
 * This class will go through all your view elements views checking for
 * indeterminate progress bars and replacing the indeterminate drawable
 * with a static one so that espresso tests can continue
 *
 *
 * http://stackoverflow.com/a/37049916/3680389
 * https://stackoverflow.com/questions/33289152/progressbars-and-espresso#36201647
 */
class ProgressBarIdler : Application.ActivityLifecycleCallbacks {


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        makeAllProgressBarsIdle(activity.findViewById<View>(R.id.content).rootView)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    companion object {

        private fun makeAllProgressBarsIdle(view: View) {
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    makeAllProgressBarsIdle(view.getChildAt(i))
                }
            } else if (view is ProgressBar) {
                if (view.isIndeterminate) {
                    view.indeterminateDrawable = ContextCompat.getDrawable(InstrumentationRegistry.getInstrumentation().targetContext, android.R.drawable.ic_lock_lock)
                }
            }
        }

        fun makeAllProgressBarsIdle(instrumentation: Instrumentation, activity: Activity) {
            instrumentation.runOnMainSync { makeAllProgressBarsIdle(activity.findViewById<View>(R.id.content).rootView) }
        }

        fun makeAllProgressBarsIdleForAllResumedActivities(instrumentation: Instrumentation) {
            instrumentation.runOnMainSync {
                val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED)
                if (resumedActivities.isEmpty()) {
                    throw RuntimeException("Could not change orientation")
                }
                for (activity in resumedActivities) {
                    makeAllProgressBarsIdle(activity.findViewById<View>(R.id.content).rootView)
                }
            }
        }
    }

}
