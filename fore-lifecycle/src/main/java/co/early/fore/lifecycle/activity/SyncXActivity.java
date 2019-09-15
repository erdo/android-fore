package co.early.fore.lifecycle.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
 *      If your app architecture does not use fragments, and your activities extend android x
 *      {@link androidx.appcompat.app.AppCompatActivity}
 *      This is probably the right class to use to add fore behaviour to your app,
 *      start by extending this class instead of AppCompatActivity
 * </p>
 *
 * <p>
 * To use this class, you need to:
 * </p>
 * <ul>
 *      <li>Extend it</li>
 *      <li>Implement {@link SyncableView#syncView()} </li>
 *      <li>Implement {@link #getThingsToObserve()} by returning a {@link LifecycleSyncer.Observables}
 *      instance constructed with all the {@link Observable} models that the view is interested in</li>
 *      <li>If you override onCreate() in your own class, you must call super.onCreate()</li>
 * </ul>
 *
 */
public abstract class SyncXActivity extends AppCompatActivity implements SyncableView{


    private LifecycleSyncer lifecycleSyncer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lifecycleSyncer = new LifecycleSyncer(this, getThingsToObserve());
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

    public abstract LifecycleSyncer.Observables getThingsToObserve();

}
