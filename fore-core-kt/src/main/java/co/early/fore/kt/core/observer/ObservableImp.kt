package co.early.fore.kt.core.observer

import android.os.Looper
import co.early.fore.core.WorkMode
import co.early.fore.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.coroutine.launchMainImm
import java.util.ArrayList


/**
 * @param notificationMode If notifications should be run to the UI thread (appropriate for most app code) then use ASYNCHRONOUS.
 *
 *
 * If notifications should be executed immediately (more appropriate for test code) then use SYNCHRONOUS
 *
 *
 *
 * For tests, you will want to inject SYNCHRONOUS here which will force
 * all the notifications to come through on the same thread that notifyObservers()
 * is called on.
 *
 *
 * For application code, you probably want ASYNCHRONOUS - this will ensure
 * all the notifications are delivered on the UI thread (posting to the UI thread only if
 * necessary) regardless of what thread called the notifyObservers() method
 * (this is handy if you will be updating the UI from
 * somethingChanged()).
 *
 *
 *
 * NB: If there are any Android Adapters depending on your model for their list data, you will
 * want to make sure that you only update this list based data on the UI thread (i.e. for use
 * with adapters, you should only call notifyObservers() on the UI thread). This remains true
 * regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
 * WorkMode.
 * Synchronizing any list updates is not enough, Android will call Adapter.count() and
 * Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
 *
 * @param logger           If you want to be told about warnings, pass an implementation of Logger here (recommended)
 */
class ObservableImp(
        private val notificationMode: WorkMode,
        private val logger: Logger? = null
) : Observable {

    private val observerList = ArrayList<Observer>()

    /**
     * Take the observer and add it to the list of registered observers that
     * want to be notified when the model data changes.
     */
    @Synchronized
    override fun addObserver(observer: Observer) {

        observerList.add(observer)

        if (observerList.size > 2) {
            logger?.w(
                LOG_TAG, "There are now:" + observerList.size + " Observers added to this Observable, that's quite a lot.\n" +
                        "It's sometimes indicative of code which is not removing observers when it should\n" +
                        "(forgetting to remove observers in an onPause() or onDetachedFromWindow() method for example)\n" +
                        "Failing to remove observers when you no longer need them will cause memory leaks"
            )
        }
    }

    /**
     * Remove the observer from the list of registered observers, you should do this
     * from android lifecycle methods like onStop() to prevent memory leaks
     *
     * @param observer the observer that is no longer interested in receiving updates
     * from the model when its data changes
     */
    @Synchronized
    override fun removeObserver(observer: Observer) {
        observerList.remove(observer)
    }


    /**
     * Extending classes should call this method, whenever the model data is
     * updated. somethingChanged() will be called on each registered observer.
     *
     *
     * If the Observable has been constructed with the SYNCHRONOUS method parameter
     * then the notifications will be called on the same thread that this method is
     * called on.
     *
     *
     * If the Observable has been constructed with the ASYNCHRONOUS method parameter
     * then the notifications will be posted to the UI thread if necessary (if notifyObservers
     * is already on the UI thread then the observers will be called immediately with no posting done)
     *
     *
     * NB: If there are any Android Adapters depending on your model for their list data, you will
     * want to make sure that you only update this list based data on the UI thread (i.e. for use
     * with adapters, you should only call notifyObservers() on the UI thread). This remains true
     * regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
     * WorkMode.
     * Synchronizing any list updates is not enough, Android will call Adapter.count() and
     * Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
     */
    @Synchronized
    override fun notifyObservers() {

        //don't post to UI thread if we are already on it as this can cause problems with android adapters
        launchMainImm(notificationMode) {
            for (observer in observerList) {
                doNotification(observer)
            }
        }
    }

    override fun hasObservers(): Boolean {
        return observerList.size > 0
    }

    private fun doNotification(observer: Observer) {
        try {
            observer.somethingChanged()
        } catch (e: Exception) {

            var errorMessage = "One of the observers has thrown an exception during it's somethingChanged() callback\n"

            logger?.e(LOG_TAG, errorMessage + e.message)

            if (Looper.myLooper() != Looper.getMainLooper()) {
                logger?.e(
                    LOG_TAG, "NOTE: this code is NOT currently on the UI thread,\n" +
                            "if you are trying to update any part of the android UI,\n" +
                            "this needs to happen on the UI thread.\n" +
                            "You can achieve this by either a) calling notifyObservers() on the UI thread,\n" +
                            "or by b) constructing this Observable with the ASYNCHRONOUS parameter which will\n" +
                            "ensure that all notifications are run on the UI thread regardless.\n" +
                            "\n" +
                            "If you are updating list based data for an Android Adapter with these notifications,\n" +
                            "then you need to use option a).\n"
                )
            }

            throw e
        }

    }

    companion object {
        private val LOG_TAG = ObservableImp::class.java.simpleName
    }
}
