package foo.bar.example.asafui.ui.counter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import foo.bar.example.asafthreading.R;


public class CounterActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = build(context);
        context.startActivity(intent);
    }

    public static Intent build(Context context) {
        Intent intent = new Intent(context, CounterActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_activity_base);

        if (savedInstanceState == null) {
            setFragment(
                    CounterFragment.newInstance(),
                    CounterFragment.class.getSimpleName());
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