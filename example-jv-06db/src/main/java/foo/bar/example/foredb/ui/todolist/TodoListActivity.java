package foo.bar.example.foredb.ui.todolist;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import foo.bar.example.foredb.ui.BaseActivityNavDrawer;


public class TodoListActivity extends BaseActivityNavDrawer {

    public static void start(Context context) {
        Intent intent = build(context);
        context.startActivity(intent);
    }

    public static Intent build(Context context) {
        Intent intent = new Intent(context, TodoListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }


    @Override
    public int getCurrentMenuItemId() {
        return 0;
    }

    @Override
    public Fragment createNewContentFragment() {
        return TodoListFragment.newInstance();
    }
}
