package co.early.asaf.ui.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.observer.Observable;
import co.early.asaf.core.ui.SyncableView;
import co.early.asaf.ui.LifecycleSyncer;

/**
 * <p>
 *      Convenience class that uses a {@link LifecycleSyncer} instance to ensure that
 *      {@link SyncableView#syncView()} is called whenever the relevant Observable models change.
 *      Also uses android lifecycle hooks to tell {@link LifecycleSyncer} when to add and remove
 *      observers to prevent memory leaks.</p>
 * <p>
 * To use this class, you need to:
 * </p>
 * <ul>
 *      <li>Extend it</li>
 *      <li>Implement {@link #getResourceIdForSyncableView()} by returning a
 *      layoutId that refers to an xml layout whose top most element
 *      is a custom view that implements {@link SyncableView}</li>
 *      <li>Implement {@link #getThingsToObserve()} by returning a {@link LifecycleSyncer.Observables}
 *      instance constructed with all the {@link Observable} models that the view is interested in</li>
 * </ul>
 *
 */
public abstract class SyncableAppCompatActivity extends AppCompatActivity {


    private LifecycleSyncer lifecycleSyncer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SyncableView syncableView = Affirm.notNull((SyncableView)(getLayoutInflater().inflate(getResourceIdForSyncableView(), null)));

        setContentView((View)syncableView);

        setLifecycleSyncer(new LifecycleSyncer(syncableView,
                getThingsToObserve()));
    }

    private View setLifecycleSyncer(LifecycleSyncer lifecycleSyncer){
        this.lifecycleSyncer = Affirm.notNull(lifecycleSyncer);
        return (View)lifecycleSyncer.syncableView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (lifecycleSyncer == null){
            throw new RuntimeException("You must call super.onCreate() from within your onCreate() method");
        }
        // add our observer to any models we want to observe
        lifecycleSyncer.addObserversAndSync();
    }

    @Override
    public void onPause() {
        super.onPause();
        // remove our observer from any models we are observing
        lifecycleSyncer.removeObservers();
    }

    public SyncableView getSyncableView(){
        return lifecycleSyncer.syncableView;
    }

    public abstract int getResourceIdForSyncableView();

    public abstract LifecycleSyncer.Observables getThingsToObserve();

}
