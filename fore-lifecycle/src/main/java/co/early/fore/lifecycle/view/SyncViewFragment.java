package co.early.fore.lifecycle.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.early.fore.core.Affirm;
import co.early.fore.core.ui.SyncableView;
import co.early.fore.lifecycle.LifecycleSyncer;

/**
 * @deprecated this is too complicated, use one of the Sync[ViewGroup] classes instead.
 */
public abstract class SyncViewFragment extends Fragment {

    private LifecycleSyncer lifecycleSyncer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setLifecycleSyncer(new LifecycleSyncer(inflater, getResourceIdForSyncableView(),
                getThingsToObserve()));
    }

    private View setLifecycleSyncer(LifecycleSyncer lifecycleSyncer){
        this.lifecycleSyncer = Affirm.notNull(lifecycleSyncer);
        return (View)lifecycleSyncer.syncableView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleSyncer == null){
            throw new RuntimeException("You must call super.onCreateView() from within your onCreateView() method");
        }
        // add our observer to any models we want to observe
        lifecycleSyncer.addObserversAndSync();
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove our observer from any models we are observing
        lifecycleSyncer.removeObservers();
    }

    public SyncableView getSyncableView(){
        return lifecycleSyncer.syncableView;
    }

    public abstract int getResourceIdForSyncableView();

    public abstract LifecycleSyncer.Observables getThingsToObserve();

}
