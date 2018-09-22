package foo.bar.example.foreretrofit;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Collection;

import static android.support.test.runner.lifecycle.Stage.RESUMED;

/**
 * If you have any spinning progress bars in your UI it will mess
 * up espresso tests (espresso will time out waiting for all UI
 * threads to be idle before proceeding).
 * <p>
 * This class will go through all your view elements views checking for
 * indeterminate progress bars and replacing the indeterminate drawable
 * with a static one so that espresso tests can continue
 * <p>
 * http://stackoverflow.com/a/37049916/3680389
 * https://stackoverflow.com/questions/33289152/progressbars-and-espresso#36201647
 */
public class ProgressBarIdler implements Application.ActivityLifecycleCallbacks {

    private static void makeAllProgressBarsIdle(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                makeAllProgressBarsIdle(viewGroup.getChildAt(i));
            }
        } else if (view instanceof ProgressBar) {
            ProgressBar progressBar = (ProgressBar) view;
            if (progressBar.isIndeterminate()) {
                progressBar.setIndeterminateDrawable(ContextCompat.getDrawable(InstrumentationRegistry.getTargetContext(), android.R.drawable.ic_lock_lock));
            }
        }
    }

    public static void makeAllProgressBarsIdle(Instrumentation instrumentation, final Activity activity) {
        instrumentation.runOnMainSync(new Runnable() {
            public void run() {
                makeAllProgressBarsIdle(activity.findViewById(android.R.id.content).getRootView());
            }
        });
    }

    public static void makeAllProgressBarsIdleForAllResumedActivities(Instrumentation instrumentation) {
        instrumentation.runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.isEmpty()) {
                    throw new RuntimeException("Could not change orientation");
                }
                for (Activity activity : resumedActivities) {
                    makeAllProgressBarsIdle(activity.findViewById(android.R.id.content).getRootView());
                }
            }
        });
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        makeAllProgressBarsIdle(activity.findViewById(android.R.id.content).getRootView());
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
