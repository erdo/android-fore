package foo.bar.example.asafui.ui.launch;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.feature.authentication.Authentication;
import foo.bar.example.asafui.ui.fruitcollector.FruitCollectorActivity;
import foo.bar.example.asafui.ui.login.LoginActivity;

import foo.bar.example.asafui.ui.ViewUtils;

/**
 * This view has nothing to sync, if the user is logged in, they are sent to the
 * {@link foo.bar.example.asafui.ui.fruitcollector.FruitCollectorActivity}, if not they are sent
 * to the {@link foo.bar.example.asafui.ui.login.LoginActivity}
 */
public class LaunchView extends RelativeLayout{

    private static final String TAG = LaunchView.class.getSimpleName();

    private Authentication authentication;

    public LaunchView(Context context) {
        super(context);
    }

    public LaunchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LaunchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        // grab a reference to any models we need here
        authentication = CustomApp.get(Authentication.class);


        //redirect based on logged in status
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (isShown()) {
                    if (authentication.hasSessionToken()) {
                        FruitCollectorActivity.start(ViewUtils.getActivityFromContext(getContext()));
                    } else {
                        LoginActivity.start(ViewUtils.getActivityFromContext(getContext()));
                    }
//                }
                ViewUtils.getActivityFromContext(getContext()).finish();
            }
        }, 1000);

    }


}