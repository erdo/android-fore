package foo.bar.example.asafui.ui.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import co.early.fore.lifecycle.LifecycleSyncer;
import co.early.fore.lifecycle.activity.SyncableAppCompatActivity;
import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.R;
import foo.bar.example.asafui.feature.tictactoe.Board;

/**
 * This is an example of implementing fore at the <b>Activity</b> level, no fragments used here
 *
 * alternatively:
 * {@link co.early.fore.lifecycle.activity.SyncableActivity},
 * {@link co.early.fore.lifecycle.fragment.SyncableSupportFragment},
 * {@link co.early.fore.lifecycle.fragment.SyncableFragment}
 */
public class TicTacToeActivity extends SyncableAppCompatActivity {

    public static void start(Context context) {
        Intent intent = build(context);
        context.startActivity(intent);
    }

    public static Intent build(Context context) {
        Intent intent = new Intent(context, TicTacToeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public int getResourceIdForSyncableView() {
        return R.layout.activity_tictactoe;
    }

    @Override
    public LifecycleSyncer.Observables getThingsToObserve() {
        return new LifecycleSyncer.Observables(CustomApp.get(Board.class));
    }





    // some stuff to handle the options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_board:
                CustomApp.get(Board.class).restart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
