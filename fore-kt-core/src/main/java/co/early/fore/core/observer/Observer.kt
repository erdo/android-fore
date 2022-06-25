package co.early.fore.core.observer

interface Observer {
    /**
     * Called by the model on each observer whenever the model data has changed, implementing
     * classes should take this as an invitation to query the model again for any new data.
     */
    fun somethingChanged()
}