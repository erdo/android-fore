package co.early.asaf.core.observer;


public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
    boolean hasObservers();
}