package co.early.fore.lifecycle.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
 *      If your app architecture uses fragments, and your fragments extend
 *      {@link androidx.fragment.app.Fragment}, to add fore behaviour to your app you can keep
 *      your activity code the same but in your fragments instead of extending Fragment,
 *      extend this class instead.
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
 *      <li>If you override onCreateView() in your own class, you must call super.onCreateView()</li>
 * </ul>
 *
 */
public abstract class SyncFragmentX extends Fragment implements SyncableView{


    private LifecycleSyncer lifecycleSyncer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        lifecycleSyncer = new LifecycleSyncer(this, getThingsToObserve());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lifecycleSyncer == null){
            throw new RuntimeException("You must call super.onCreateView() from within your onCreateView() method");
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
