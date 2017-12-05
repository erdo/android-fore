package foo.bar.example.asafui.ui.wallet;

import co.early.asaf.core.WorkMode;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.ui.LifecycleSyncer;
import co.early.asaf.ui.fragment.SyncableSupportFragment;

/**
 *
 */
public class ExampleFragment extends SyncableSupportFragment {

    public static ExampleFragment newInstance() {
        ExampleFragment fragment = new ExampleFragment();
        return fragment;
    }


    @Override
    public int getResourceIdForSyncableView() {
        return co.early.asaf.ui.R.layout.fragment_basket;
    }

    @Override
    public LifecycleSyncer.Observables getThingsToObserve() {
        return new LifecycleSyncer.Observables(
                new ObservableImp(WorkMode.SYNCHRONOUS),
                new ObservableImp(WorkMode.SYNCHRONOUS));
//        return new LifecycleSyncer.Observables();
    }

}