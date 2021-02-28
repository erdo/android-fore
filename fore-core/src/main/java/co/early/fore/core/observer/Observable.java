package co.early.fore.core.observer;


public interface Observable extends ObservableGroup {
    void notifyObservers();
    boolean hasObservers();
}
