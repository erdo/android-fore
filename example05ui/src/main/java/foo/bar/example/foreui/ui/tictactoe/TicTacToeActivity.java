package foo.bar.example.foreui.ui.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import foo.bar.example.foreui.CustomApp;
import foo.bar.example.foreui.R;
import foo.bar.example.foreui.feature.tictactoe.Board;


public class TicTacToeActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);
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
