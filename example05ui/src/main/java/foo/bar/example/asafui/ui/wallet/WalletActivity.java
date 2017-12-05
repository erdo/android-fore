package foo.bar.example.asafui.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import foo.bar.example.asafui.R;

public class WalletActivity extends AppCompatActivity {


    public static void start(AppCompatActivity activity) {
        Intent intent = build(activity);
        activity.startActivity(intent);
    }

    public static Intent build(AppCompatActivity activity) {
        Intent intent = new Intent(activity, WalletActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_base_activity);

        if (savedInstanceState == null) {
            setFragment(
                    WalletsFragment.newInstance(),
                    WalletsFragment.class.getSimpleName());
        }

        overridePendingTransition(0, 0);
    }

    private void setFragment(Fragment fragment, String fragmentTag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(
                R.id.content_main,
                fragment,
                fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
