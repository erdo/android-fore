package co.early.fore.core.observer

import co.early.fore.core.WorkMode
import co.early.fore.core.logging.getTagInferer
import co.early.fore.core.logging.Logger
import co.early.fore.core.coroutine.launchCustom
import co.early.fore.core.coroutine.launchMain
import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.MultiplatformLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * @param notificationMode If notifications should be run on the UI thread (appropriate for most
 * app code) then use ASYNCHRONOUS. For tests, you will want to inject SYNCHRONOUS here which will
 * force all the notifications to come through on the same thread that notifyObservers()
 * is called on.
 *
 * @param logger If you want to be told about warnings, pass an implementation of Logger
 * here (recommended)
 *
 * @param dispatcher You will only need to specify this if you are running in a non-android module,
 * we'd recommend passing in Dispatchers.Main.immediate here in that case, otherwise leave this as
 * null
 *
 * If you don't specify any construction parameters, they will be taken from ForeDelegateHolder
 *
 * NB: If there are any Android Adapters depending on your model for their list data, you will
 * want to make sure that you only update this list based data on the UI thread (i.e. for use
 * with adapters, you should only call notifyObservers() on the UI thread). This remains true
 * regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
 * WorkMode.
 * Synchronizing any list updates is not enough, Android will call Adapter.count() and
 * Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
 *
 */
class ObservableImp(
    private val notificationMode: WorkMode? = null,
    private val logger: Logger? = null,
    private val dispatcher: CoroutineDispatcher? = null
) : Observable {

    // this is for iOS target benefit which doesn't like default parameters in constructors
    constructor() : this(null, null, null)

    private val observerList = mutableListOf<Observer>()
    private val addRemoveMutex = Mutex()
    private val inferredTag: String = getTagInferer().inferTag()

    /**
     * Take the observer and add it to the list of registered observers that
     * want to be notified when the model data changes. Usually you will do this
     * from android lifecycle methods like onStart() (and remove the observer in onStop())
     */
    override fun addObserver(observer: Observer) {
        launchMain {
            addRemoveMutex.withLock {

                if (observerList.contains(observer)) {
                    Fore.getLogger(logger).w(inferredTag,
                        "You are about to add the same observer twice to [$inferredTag]. This is almost certainly an error and indicates code that " +
                                "could cause a memory leak. Usually an observer is added and removed in line with _mirrored_ lifecycle methods " +
                                "(for example onStart()/onStop() or onAttachedToWindow()/onDetachedFromWindow()) thread:${threadName()}"
                    )
                }

                observerList.add(observer)

                Fore.getLogger(logger).i(inferredTag, "Observer added to....... [${inferredTag}]: $observer")

                if (observerList.size > 4) {
                    Fore.getLogger(logger).w(inferredTag,
                        "There are now:" + observerList.size + " Observers added to the Observable [$inferredTag], that's quite a lot.\n" +
                                "It's sometimes indicative of code which is not removing observers when it should\n" +
                                "(forgetting to remove observers in an onStop(), onClear() or onDetachedFromWindow() method for example)\n" +
                                "Failing to remove observers when you no longer need them will cause memory leaks,\n" +
                                "you might want to look in to the ForeLifecycleObserver (legacy Android) or\n" +
                                "Fore's observeAsState() extension function (Compose UI) which handles this for you.\n" +
                                "(If the number of observers steadily increases as you use the app, that's probably what you have,\n" +
                                "if the number remains constant or goes down, then you're probably ok :) ) Thread:${threadName()}"
                    )
                }
            }
        }
    }

    /**
     * Remove the observer from the list of registered observers, you should do this
     * from android lifecycle methods like onStop() to prevent memory leaks.
     *
     * @param observer the observer that is no longer interested in receiving updates
     * from the model when its data changes
     */
    override fun removeObserver(observer: Observer) {
        launchMain {
            addRemoveMutex.withLock {

                val beforeSize = observerList.size
                observerList.remove(observer)

                Fore.getLogger(logger).i(inferredTag, "Observer removed from... [$inferredTag]: $observer")

                if (observerList.size == beforeSize) {
                    Fore.getLogger(logger).w(inferredTag,
                        "You have tried to remove an observer from [$inferredTag] that wasn't added in the first place. This is almost certainly an error and\n" +
                                "will cause a memory leak. Usually an observer is added and removed in line with _mirrored_ lifecycle methods\n" +
                                "(for example onStart()/onStop() or onAttachedToWindow()/onDetachedFromWindow()) thread:${threadName()}"
                    )
                }
            }
        }
    }


    /**
     * Extending classes should call this method, whenever the model data is
     * updated. somethingChanged() will be called on each registered observer.
     *
     * If the Observable has been constructed with the SYNCHRONOUS method parameter
     * then the notifications will be called on the same thread that this method is
     * called on.
     *
     * If the Observable has been constructed with the ASYNCHRONOUS method parameter
     * then the notifications will be posted to the UI thread if necessary (if notifyObservers
     * is already on the UI thread then the observers will be called immediately with no posting done)
     *
     * NB: If there are any Android Adapters depending on your model for their list data, you will
     * want to make sure that you only update this list based data on the UI thread (i.e. for use
     * with adapters, you should only call notifyObservers() on the UI thread). This remains true
     * regardless of whether this Observable has been created with ASYNCHRONOUS or SYNCHRONOUS
     * WorkMode.
     * Synchronizing any list updates is not enough, Android will call Adapter.count() and
     * Adapter.get() on the UI thread and you cannot change the adapter's size between these calls.
     */
    override fun notifyObservers() {

        var dispatch = dispatcher

        if (dispatch == null) {
            try {
                dispatch = Dispatchers.Main.immediate
            } catch (uoe: UnsupportedOperationException){
                val errorMessage = "\nIt looks like you are running in a module that doesn't support \n" +
                        "Dispatchers.Main.immediate\n" +
                        "If this is intentional, you will need to specify a dispatcher in the\n" +
                        "constructor, we'd recommend Dispatchers.Main in that case\n" +
                        "If this is NOT intentional, move your code to a module that supports\n" +
                        "Dispatchers.Main.immediate (e.g. an android module)"
                Fore.getLogger(logger).e(inferredTag, errorMessage)
                throw IllegalArgumentException(errorMessage)
            }
        }

        launchCustom(dispatch, Fore.getWorkMode(notificationMode)) {
            addRemoveMutex.withLock {
                for (observer in observerList) {
                    doNotification(observer)
                }
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

            val dispatcherMsg = if (dispatcher!=null) {
                        " and the dispatcher used was: $dispatcher \n" +
                        "If you are trying to update any part of the android UI directly from the somethingChanged() callback,\n" +
                        "then ObservableImp needs to be constructed with Dispatchers.Main.immediate" } else ""

            val errorMessage = "\nOne of the observers of [$inferredTag] has thrown an exception during it's somethingChanged() callback\n" +
                    "The currentThread id is: ${threadName()} $dispatcherMsg\n" +
                    "If you are updating an android adapter directly, you will want to make sure that notifyObservers()\n" +
                    "is being called from the UI thread. Having said that, it's quite possible you just have a crash somewhere\n" +
                    "in your UI code which has bubbled its way up to here. See stack trace for further info, Error Message: "

            Fore.getLogger(logger).e(inferredTag, errorMessage + e.message)
            throw e
        }
    }
}
