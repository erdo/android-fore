package foo.bar.example.foredb.ui;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;

import co.early.fore.core.logging.Logger;
import co.early.fore.core.ui.SyncableView;
import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.feature.bossmode.BossMode;


public class NavContainer extends NavigationView implements SyncableView{

    public static final String LOG_TAG = NavContainer.class.getSimpleName();


    //models we want
    private BossMode bossMode;
    private Logger logger;


    //see note in the syncView() method as to why we are keeping this state here
    private boolean bossModeIsOn = false;


    public NavContainer(Context context) {
        super(context);
    }

    public NavContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        bossMode = App.get(BossMode.class);
        logger = App.get(Logger.class);
    }


    //this is called by BaseActivityNavDrawer
    public void syncView(){

        logger.i(LOG_TAG, "syncView() boss mode on: " + bossMode.isBossModeOn());

        // Usually we prefer to just sync the view here. Because Android menus don't make use
        // of their adapter framework like a regular RecyclerView would, changing menu items is a
        // very inefficient process - it requires doing inflation etc. Because of that, we are only
        // syncing the menu if we detect that the state has actually changed (makes the code
        // less nice, but hey ho). If this really bothers you, I suspect making a NavigationView
        // that just uses a regular RecyclerView for its menu is probably fairly trivial.
        boolean tempBossMode = bossMode.isBossModeOn();
        if (getMenu().size() == 0 || bossModeIsOn != tempBossMode){
            //boss mode status has changed, update menu
            getMenu().clear();
            inflateMenu(tempBossMode ? R.menu.menu_bossmode_on : R.menu.menu_bossmode_off);
            bossModeIsOn = tempBossMode;
        }
   }

}
