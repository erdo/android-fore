package co.early.asaf.ui;

import android.view.LayoutInflater;

import java.util.Arrays;
import java.util.List;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.observer.Observable;
import co.early.asaf.core.observer.Observer;
import co.early.asaf.core.ui.SyncableView;

/**
 * <p>
 *      Class used by
 *      {@link co.early.asaf.ui.activity.SyncableActivity},
 *      {@link co.early.asaf.ui.activity.SyncableAppCompatActivity},
 *      {@link co.early.asaf.ui.fragment.SyncableFragment},
 *      {@link co.early.asaf.ui.fragment.SyncableSupportFragment}
 *      to hold a reference to a list of {@link Observable} instances.
 *      A single {@link Observer} which calls {@link SyncableView#syncView()} when notified
 *      is added and removed in line with android lifecycle methods to prevent memory leaks
 *      and ensure UI consistency.
 * </p>
 */
public class LifecycleSyncer {

    private final List<Observable> observablesList;
    public final SyncableView syncableView;

    private Observer viewUpdater = new Observer() {
        @Override
        public void somethingChanged() {
            syncableView.syncView();
        }
    };

    public LifecycleSyncer(LayoutInflater layoutInflater, int layoutResourceId, Observables observables) {
        Affirm.notNull(layoutInflater);
        this.observablesList = Affirm.notNull(observables).observablesList;
        this.syncableView = Affirm.notNull((SyncableView) layoutInflater.inflate(layoutResourceId, null));
    }

    public LifecycleSyncer(SyncableView syncableView, Observables observables) {
        this.observablesList = Affirm.notNull(observables).observablesList;
        this.syncableView = Affirm.notNull(syncableView);
    }

    public void addObserversAndSync() {
        for (Observable observable : observablesList){
            observable.addObserver(viewUpdater);
        }
        syncableView.syncView();
    }

    public void removeObservers() {
        for (Observable observable : observablesList){
            observable.removeObserver(viewUpdater);
        }
    }


    public static class Observables{

        private final List<Observable> observablesList;

        public Observables(Observable... observablesList) {
            this.observablesList = Arrays.asList(Affirm.notNull(observablesList));
        }
    }

}
