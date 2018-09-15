package co.early.fore.core.observer;


public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
    boolean hasObservers();
}
