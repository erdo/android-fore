package foo.bar.example.foredatabinding.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import foo.bar.example.foredatabinding.R;


public class WalletsActivity extends FragmentActivity {

    public static void start(Context context) {
        Intent intent = build(context);
        context.startActivity(intent);
    }

    public static Intent build(Context context) {
        Intent intent = new Intent(context, WalletsActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_activity_base);

        if (savedInstanceState == null) {
            setFragment(
                    WalletsFragment.newInstance(),
                    WalletsFragment.class.getSimpleName());
        }
    }

    private void setFragment(Fragment fragment, String fragmentTag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(
                R.id.content_main,
                fragment,
                fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
    }

}
