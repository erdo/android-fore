package foo.bar.example.asafui.ui.launch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.ui.SyncableView;
import co.early.asaf.ui.LifecycleSyncer;
import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.feature.authentication.Authentication;

/**
 * Nothing to sync here, all this does is show a spinner while we check for the log in status of the
 * user, this work is done in {@link LaunchView}
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(getLayoutInflater().inflate(R.layout.activity_launch, null));
    }

}
