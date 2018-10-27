package foo.bar.example.foredb.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;


public abstract class BaseActivity extends AppCompatActivity {

    protected Fragment contentFragment;

    private final static String FRAGMENT_TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.init();

        overridePendingTransition(0, 0);

        setContentView(R.layout.common_activity_base);

        getSupportActionBar().setTitle(R.string.app_name);

        //setup fragment
        if (savedInstanceState == null) {
            contentFragment = createNewContentFragment();
            if (contentFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_main, contentFragment, FRAGMENT_TAG);
                transaction.commitAllowingStateLoss();
            }
        } else {
            // we have been rotated or whatever, fragment manager will take care of putting fragment
            // back can get a reference like this if you want
            contentFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    abstract public Fragment createNewContentFragment();

}
