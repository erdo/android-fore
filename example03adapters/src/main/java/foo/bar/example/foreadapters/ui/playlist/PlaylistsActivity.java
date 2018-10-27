package foo.bar.example.foreadapters.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import foo.bar.example.foreadapters.R;


public class PlaylistsActivity extends FragmentActivity {

    public static void start(Context context) {
        Intent intent = build(context);
        context.startActivity(intent);
    }

    public static Intent build(Context context) {
        Intent intent = new Intent(context, PlaylistsActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_activity_base);

        if (savedInstanceState == null) {
            setFragment(
                    PlaylistsFragment.newInstance(),
                    PlaylistsFragment.class.getSimpleName());
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
