package co.early.asaf.core.observer;

public interface Observer {

    /**
     * Called by the model on each observer whenever the model data has changed, implementing
     * classes should take this as an invitation to query the model again for any new data.
     */
    void somethingChanged();
}
