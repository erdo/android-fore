package co.early.fore.lifecycle;

import java.util.Arrays;
import java.util.List;

import co.early.fore.core.Affirm;
import co.early.fore.core.observer.Observable;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.ui.AutoSyncable;
import co.early.fore.core.ui.SyncableView;

public class ObservableGroup implements AutoSyncable {

    private final List<Observable> observablesList;
    private SyncableView syncableView;
    private Observer observer;

    private Observer viewUpdater = new Observer() {
        @Override
        public void somethingChanged() {
            syncableView.syncView();
        }
    };

    public ObservableGroup(Observable... observablesList) {
        this.observablesList = Arrays.asList(Affirm.notNull(observablesList));
        checkObservables();
    }

    private void checkObservables() {
        for (Observable observable : observablesList) {
            if (observable == null) {
                throw new RuntimeException("ObservableGroup has been instantiated with at least one null observable");
            }
        }
    }

    public void addObserversAndSync(SyncableView syncableView) {
        if (alreadyObserving()){
            throw new RuntimeException("you must remove previously added observers first");
        }
        this.syncableView = Affirm.notNull(syncableView);
        for (Observable observable : observablesList) {
            observable.addObserver(viewUpdater);
        }
        this.syncableView.syncView();
    }

    public void addObserversAndSync(Observer observer) {
        if (alreadyObserving()){
            throw new RuntimeException("you must remove previously added observers first");
        }
        this.observer = Affirm.notNull(observer);
        for (Observable observable : observablesList) {
            observable.addObserver(observer);
        }
        observer.somethingChanged();
    }

    public void removeObservers() {
        if (observer != null) {
            for (Observable observable : observablesList) {
                observable.removeObserver(observer);
            }
            this.observer = null;
        } else if (syncableView != null) {
            for (Observable observable : observablesList) {
                observable.removeObserver(viewUpdater);
            }
            this.syncableView = null;
        }
    }

    private boolean alreadyObserving(){
        return (observer != null || syncableView != null);
    }
}
