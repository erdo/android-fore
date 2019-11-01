package co.early.fore.lifecycle;

import android.view.LayoutInflater;

import java.util.Arrays;
import java.util.List;

import co.early.fore.core.Affirm;
import co.early.fore.core.observer.Observable;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.ui.SyncableView;

/**
 * <p>
 *      Holds a reference to a list of {@link Observable} instances.
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
        checkObservables();
    }

    public LifecycleSyncer(SyncableView syncableView, Observables observables) {
        this.observablesList = Affirm.notNull(observables).observablesList;
        this.syncableView = Affirm.notNull(syncableView);
        checkObservables();
    }

    private void checkObservables(){
        for (Observable observable: observablesList){
            if (observable == null){
                throw new RuntimeException("One of the observables used to instantiate LifecycleSyncer.Observables is null");
            }
        }
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
