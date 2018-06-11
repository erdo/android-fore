package foo.bar.example.asafadapters2.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.asaf.core.observer.Observer;
import foo.bar.example.asafadapters2.App;
import foo.bar.example.asafadapters2.R;
import foo.bar.example.asafadapters2.feature.bossmode.BossMode;
import foo.bar.example.asafadapters2.feature.remote.RemoteWorker;
import foo.bar.example.asafadapters2.feature.todoitems.TodoListModel;


/**
 *
 * NB: Usually you would navigate to different activities in the handleMenu() method below, but it's
 * not salient for our db list demo
 *
 */
public abstract class BaseActivityNavDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = BaseActivityNavDrawer.class.getSimpleName();

    //models we are interested in
    private BossMode bossMode;
    private RemoteWorker remoteWorker;
    private TodoListModel todoListModel;

    //ui elements we are interested in
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;
    @BindView(R.id.nav_view)
    protected NavContainer navContainer;


    protected ActionBarDrawerToggle toggle;
    protected Fragment contentFragment;

    private final static String FRAGMENT_TAG = "MAIN";


    //single observer reference
    private Observer observer = this::syncView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.init();

        overridePendingTransition(0, 0);

        setContentView(R.layout.common_activity_base);

        ButterKnife.bind(this, this);

        bossMode = App.get(BossMode.class);
        remoteWorker = App.get(RemoteWorker.class);
        todoListModel = App.get(TodoListModel.class);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        navContainer.setNavigationItemSelectedListener(this);

        //make the item we are currently on selected - put this back if you want to use the navigation properly
        //navContainer.setCheckedItem(getCurrentMenuItemId());

        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();


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
    protected void onStart() {
        super.onStart();
        drawer.addDrawerListener(toggle);

        //data binding for the navigation menu
        bossMode.addObserver(observer);
        syncView(); //  <- don't forget this
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.removeDrawerListener(toggle);

        //data binding for the navigation menu
        bossMode.removeObserver(observer);
    }

    //data binding for the navigation menu
    private void syncView(){
        navContainer.syncView();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return handleMenu(item);
    }

    private boolean handleMenu(MenuItem item) {

        Log.i(TAG, "handleMenu() item:" + item);

        switch (item.getItemId()) {
            case R.id.action_add_25_web:
                remoteWorker.fetchTodoItems(
                        () -> { /* observers take care of everything */ },
                        failureMessage -> {Toast.makeText(this, failureMessage.getString(), Toast.LENGTH_SHORT).show();});
                break;
            case R.id.action_bossmode:
                bossMode.startBossModeFor(10*1000);
                break;
//            case R.id.action_goto_activity:
//                if (getCurrentMenuItemId() != R.id.action_clear) {
//                    OtherActivity.start(this);
//                }
//                drawer.closeDrawer(GravityCompat.START);
//                break;
            default:
                return false;
        }
        return true;
    }


    abstract public int getCurrentMenuItemId();

    abstract public Fragment createNewContentFragment();

}
