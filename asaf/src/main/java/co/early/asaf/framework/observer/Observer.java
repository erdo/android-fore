package co.early.asaf.framework.observer;

public interface Observer {

    /**
     * Called by the model on each observer whenever the model data has changed, implementing
     * classes should take this as an invitation to query the model again for any new data.
     */
    void somethingChanged();
}
