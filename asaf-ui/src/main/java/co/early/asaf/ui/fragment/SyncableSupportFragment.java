package co.early.asaf.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.ui.SyncableView;
import co.early.asaf.ui.LifecycleSyncer;

/**
 * <p>
 *      Convenience class that uses a {@link co.early.asaf.ui.LifecycleSyncer} instance to ensure that
 *      {@link co.early.asaf.core.ui.SyncableView#syncView()} is called whenever the relevant Observable models change.
 *      Also uses android lifecycle hooks to tell {@link co.early.asaf.ui.LifecycleSyncer} when to add and remove
 *      observers to prevent memory leaks.</p>
 *
 * <p>
 *      If your app architecture uses fragments, and your fragments extend
 *      {@link android.support.v4.app.Fragment}, to add ASAF behaviour to your app you can keep
 *      your activity code the same but in your fragments instead of extending Fragment,
 *      extend this class instead.
 * </p>
 *
 * <p>
 * To use this class, you need to:
 * </p>
 * <ul>
 *      <li>Extend it</li>
 *      <li>Implement {@link co.early.asaf.ui.fragment.SyncableSupportFragment#getResourceIdForSyncableView()} by returning a
 *      layoutId that refers to an xml layout whose top most element
 *      is a custom view that implements {@link co.early.asaf.core.ui.SyncableView}</li>
 *      <li>Implement {@link co.early.asaf.ui.fragment.SyncableSupportFragment#getThingsToObserve()} by returning a {@link co.early.asaf.ui.LifecycleSyncer.Observables}
 *      instance constructed with all the {@link co.early.asaf.core.observer.Observable} models that the view is interested in</li>
 * </ul>
 *
 */
public abstract class SyncableSupportFragment extends Fragment {

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
