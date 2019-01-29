package co.early.fore.core.observer;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;

public class ObservableImp implements Observable{

    private static String TAG = ObservableImp.class.getSimpleName();

    private final WorkMode notificationMode;
    private final Logger logger;
    private final Handler handler;


    /**
     * @param notificationMode If notifications should be posted to the UI thread (appropriate for most app code) then use ASYNCHRONOUS.<p>
     *                         If notifications should be executed directly (more appropriate for test code) then use SYNCHRONOUS<p>
     *
     *                         For tests, you will want to inject SYNCHRONOUS here which will force
     *                         all the notifications to come through on the same thread that notifyObservers()
     *                         is called on.<p>
     *                         For application code, you probably want ASYNCHRONOUS - this will ensure
     *                         all the notifications are delivered on the UI thread (posting to the UI thread only if
     *                         necessary) regardless of what thread called the notifyObservers() method
     *                         (this is handy if you will be updating the UI from
     *                         somethingChanged()).<p>
     *
     *                         NB: If there are any Android Adapters depending on your model for their list data, you will
     *                         want to make sure that you only update this list based data on the UI thread (i.e. for use
     *                         with adapters, you should only call notifyObservers() on the UI thread). This remains true
     *                         regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
     *                         WorkMode.
     *                         Synchronizing any list updates is not enough, Android will call Adapter.count() and
     *                         Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
     */
    public ObservableImp(WorkMode notificationMode) {
        this.notificationMode = Affirm.notNull(notificationMode);
        this.logger = null;
        this.handler = handler(notificationMode);
    }


    /**
     * @param notificationMode If notifications should be posted to the UI thread (appropriate for most app code) then use ASYNCHRONOUS.<p>
     *                         If notifications should be executed directly (more appropriate for test code) then use SYNCHRONOUS<p>
     *
     *                         For tests, you will want to inject SYNCHRONOUS here which will force
     *                         all the notifications to come through on the same thread that notifyObservers()
     *                         is called on.<p>
     *                         For application code, you probably want ASYNCHRONOUS - this will ensure
     *                         all the notifications are delivered on the UI thread (posting to the UI thread only if
     *                         necessary) regardless of what thread called the notifyObservers() method
     *                         (this is handy if you will be updating the UI from
     *                         somethingChanged()).<p>
     *
     *                         NB: If there are any Android Adapters depending on your model for their list data, you will
     *                         want to make sure that you only update this list based data on the UI thread (i.e. for use
     *                         with adapters, you should only call notifyObservers() on the UI thread). This remains true
     *                         regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
     *                         WorkMode.
     *                         Synchronizing any list updates is not enough, Android will call Adapter.count() and
     *                         Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
     *
     * @param logger           If you want to be told about warnings, pass an implementation of Logger here (recommended)
     */
    public ObservableImp(WorkMode notificationMode, Logger logger) {
        this.notificationMode = Affirm.notNull(notificationMode);
        this.logger = Affirm.notNull(logger);
        this.handler = handler(notificationMode);
    }


    private final ArrayList<Observer> observerList = new ArrayList<Observer>();

    /**
     * Take the observer and add it to the list of registered observers that
     * want to be notified when the model data changes.
     */
    public synchronized void addObserver(Observer observer) {

        observerList.add(Affirm.notNull(observer));

        if (observerList.size() > 2 && logger != null) {
            logger.w(TAG, "There are now:" + observerList.size() + " Observers added to this Observable, that's quite a lot.\n" +
                    "It's sometimes indicative of code which is not removing observers when it should\n" +
                    "(forgetting to remove observers in an onPause() or onDetachedFromWindow() method for example)\n" +
                    "Failing to remove observers when you no longer need them will cause memory leaks");
        }

    }

    /**
     * Remove the observer from the list of registered observers, you should do this
     * from android lifecycle methods like onPause() to prevent memory leaks
     *
     * @param observer the observer that is no longer interested in receiving updates
     *                 from the model when its data changes
     */
    public synchronized void removeObserver(Observer observer) {
        observerList.remove(Affirm.notNull(observer));
    }


    /**
     * Extending classes should call this method, whenever the model data is
     * updated. somethingChanged() will be called on each registered observer.
     * <p>
     * If the Observable has been constructed with the SYNCHRONOUS method parameter
     * then the notifications will be called on the same thread that this method is
     * called on.<p>
     * If the Observable has been constructed with the ASYNCHRONOUS method parameter
     * then the notifications will be posted to the UI thread if necessary (if notifyObservers
     * is already on the UI thread then the observers will be called inline with no posting done)
     * <p>
     * NB: If there are any Android Adapters depending on your model for their list data, you will
     * want to make sure that you only update this list based data on the UI thread (i.e. for use
     * with adapters, you should only call notifyObservers() on the UI thread). This remains true
     * regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
     * WorkMode.
     * Synchronizing any list updates is not enough, Android will call Adapter.count() and
     * Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
     */
    public synchronized void notifyObservers() {

        for (final Observer observer : observerList) {

            //don't post to UI thread if we are already on it as this can cause problems with android adapters
            if (notificationMode == WorkMode.SYNCHRONOUS || Looper.myLooper() == Looper.getMainLooper()) {
                doNotification(observer);
            } else {
                // post notifications to UI thread (so that no special work needs to be done by observers to update UI)
                handler.post(() -> doNotification(observer));
            }
        }
    }

    public boolean hasObservers(){
        return (observerList.size()>0);
    }

    private void doNotification(Observer observer) {
        try {
            observer.somethingChanged();
        } catch (Exception e) {

            String errorMessage = "One of the observers has thrown an exception during it's somethingChanged() callback\n";

            if (Looper.myLooper() != Looper.getMainLooper()) {
                errorMessage = errorMessage + "NOTE: this code is NOT currently on the UI thread,\n" +
                        "if you are trying to update any part of the android UI,\n" +
                        "this needs to happen on the UI thread.\n" +
                        "You can achieve this by either a) calling notifyObservers() on the UI thread,\n" +
                        "or by b) constructing this Observable with the ASYNCHRONOUS parameter which will\n" +
                        "ensure that all notifications are run on the UI thread regardless.\n" +
                        "\n" +
                        "If you are updating list based data for an Android Adapter with these notifications,\n" +
                        "then you need to use option a).\n";
            }

            if (logger != null) {
                logger.e(TAG, errorMessage + e.getMessage());
            }

            throw e;
        }
    }

    private Handler handler(WorkMode workMode){
        return (workMode == WorkMode.ASYNCHRONOUS) ? new Handler(Looper.getMainLooper()) : null;
    }

}
