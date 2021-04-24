package co.early.fore.core.observer;

import java.util.Arrays;
import java.util.List;

import co.early.fore.core.Affirm;

public class ObservableGroupImp implements ObservableGroup {

    private final List<Observable> observablesList;

    public ObservableGroupImp(Observable... observablesList) {
        this.observablesList = Arrays.asList(Affirm.notNull(observablesList));
        checkObservables();
    }

    @Override
    public void addObserver(Observer observer) {
        for (Observable observable : observablesList) {
            observable.addObserver(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        for (Observable observable : observablesList) {
            observable.removeObserver(observer);
        }
    }

    private void checkObservables() {
        for (Observable observable : observablesList) {
            if (observable == null) {
                throw new RuntimeException("ObservableGroup has been instantiated with at least one null observable");
            }
        }
    }
}
