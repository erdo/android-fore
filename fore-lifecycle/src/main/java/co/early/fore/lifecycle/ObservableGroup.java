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

    private void checkObservables(){
        for (Observable observable: observablesList){
            if (observable == null){
                throw new RuntimeException("ObservableGroup has been instantiated with at least one null observable");
            }
        }
    }

    public void addObserversAndSync(SyncableView syncableView) {
        this.syncableView = Affirm.notNull(syncableView);
        for (Observable observable : observablesList){
            observable.addObserver(viewUpdater);
        }
        this.syncableView.syncView();
    }

    public void removeObservers() {
        for (Observable observable : observablesList){
            observable.removeObserver(viewUpdater);
        }
        this.syncableView = null;
    }
}
