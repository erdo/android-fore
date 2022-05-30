package co.early.fore.core.observer;


public interface ObservableGroup {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}
