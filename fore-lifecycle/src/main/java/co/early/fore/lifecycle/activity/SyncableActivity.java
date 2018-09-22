package co.early.fore.lifecycle.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import co.early.fore.core.Affirm;
import co.early.fore.core.observer.Observable;
import co.early.fore.core.ui.SyncableView;
import co.early.fore.lifecycle.LifecycleSyncer;

/**
 * <p>
 *      Convenience class that uses a {@link LifecycleSyncer} instance to ensure that
 *      {@link SyncableView#syncView()} is called whenever the relevant Observable models change.
 *      Also uses android lifecycle hooks to tell {@link LifecycleSyncer} when to add and remove
 *      observers to prevent memory leaks.</p>
 *
 * <p>
 *      If your app architecture does not use fragments, and your activities extend
 *      {@link android.app.Activity}
 *      This is probably the right class to use to add ASAF behaviour to your app,
 *      start by extending this class instead of Activity
 * </p>
 *
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
public abstract class SyncableActivity extends Activity {


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
