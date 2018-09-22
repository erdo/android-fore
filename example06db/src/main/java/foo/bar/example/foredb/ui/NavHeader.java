package foo.bar.example.foredb.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.ui.SyncableView;
import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.feature.bossmode.BossMode;
import foo.bar.example.foredb.feature.remote.RemoteWorker;
import foo.bar.example.foredb.ui.common.widgets.PercentBar;


public class NavHeader extends RelativeLayout implements SyncableView{

    public static final String LOG_TAG = NavHeader.class.getSimpleName();

    //models we need
    private BossMode bossMode;
    private RemoteWorker remoteWorker;
    private Logger logger;


    //references to ui elements
    @BindView(R.id.navheader_networkbusy_progressbar)
    public ProgressBar busy;
    @BindView(R.id.navheader_bossmodeprog_percentbar)
    public PercentBar bossmodeProgress;
    @BindView(R.id.navheader_connections_textview)
    public TextView connectionsNumber;
    @BindView(R.id.navheader_bossmodestate_textview)
    public TextView bossModeState;


    //single observer reference
    Observer observer = this::syncView;


    public NavHeader(Context context) {
        super(context);
    }

    public NavHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NavHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        // ui references
        ButterKnife.bind(this, this);

        // inject models
        bossMode = App.get(BossMode.class);
        logger = App.get(Logger.class);
        remoteWorker = App.get(RemoteWorker.class);

        //supporting coloured progress bar for some older devices
        busy.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorWhite),
                android.graphics.PorterDuff.Mode.SRC_IN);

    }


    //data-binding stuff below

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        remoteWorker.addObserver(observer);
        bossMode.addObserver(observer);
        syncView(); //  <- don't forget this
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        remoteWorker.removeObserver(observer);
        bossMode.removeObserver(observer);
    }


    public void syncView(){

        logger.i(LOG_TAG, "syncView() boss mode on: " + bossMode.isBossModeOn() + " connections:" + remoteWorker.getConnections());

        busy.setVisibility(remoteWorker.isBusy() ? VISIBLE : INVISIBLE);
        connectionsNumber.setText("" + remoteWorker.getConnections());
        bossmodeProgress.setVisibility(bossMode.isBossModeOn() ? VISIBLE : INVISIBLE);
        bossmodeProgress.setPercentDone(bossMode.getProgressPercent());
        bossModeState.setText(App.instance().getString(bossMode.isBossModeOn() ?
                R.string.common_on : R.string.common_off));
        setBackground(App.instance().getResources().getDrawable(bossMode.isBossModeOn() ?
                R.drawable.nav_bg_bossmode_on : R.drawable.nav_bg_bossmode_off));
    }

}
