package co.early.fore.lifecycle.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import co.early.fore.core.observer.Observable;
import co.early.fore.core.ui.SyncableView;
import co.early.fore.lifecycle.LifecycleSyncer;


/**
 * @deprecated use {@link SyncActivityX} instead.
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
