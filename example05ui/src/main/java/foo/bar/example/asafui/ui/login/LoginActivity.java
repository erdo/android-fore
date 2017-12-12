package foo.bar.example.asafui.ui.login;

import android.content.Context;
import android.content.Intent;

import co.early.asaf.ui.LifecycleSyncer;
import co.early.asaf.ui.activity.SyncableAppCompatActivity;
import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.feature.authentication.Authentication;

/**
 * This is an example of implementing ASAF at the <b>Activity</b> level, no fragments used here
 */
public class LoginActivity extends SyncableAppCompatActivity {

    public static void start(Context context) {
        Intent intent = build(context);
        context.startActivity(intent);
    }

    public static Intent build(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public int getResourceIdForSyncableView() {
        return R.layout.activity_login;
    }

    @Override
    public LifecycleSyncer.Observables getThingsToObserve() {
        return new LifecycleSyncer.Observables(CustomApp.get(Authentication.class));
    }
}
