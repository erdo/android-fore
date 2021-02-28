
# Views
Views are not just XML layouts, in Android the classes that form the view layer of an app are not just the classes extending View either, they include the **Activity**, **Fragment** *and* **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

> "View layers are: ephemeral; tightly coupled to the context; slow to test"


In short they are no place to put business logic or networking code, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted, such as:

- loosing data stored in memory (causing null pointers or requiring unecessary network calls)
- maintaining UI consistency
- guarding against memory leaks

It might seem obvious, but still: handling those issues accounts for a fairly large chunk of the boiler plate present in a typical android app.


## Examples

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from:

- manage their lifecycle
- route button clicks and other widget listeners to the right place (usually directly to a model class)
- correctly display the state of whatever models they are interested in.


Here are few examples:

- [Wallets View](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/ui/wallet/WalletsActivity.kt)

- [Counter View](https://github.com/erdo/android-fore/blob/master/example-jv-02threading/src/main/java/foo/bar/example/forethreading/ui/CounterActivity.java)


## Code that belongs in the view layer

Pretty much all views in **fore** do the same few things when they are created:

- get a reference to all the view components like Buttons, TextViews etc.
- get a reference to all models that the view needs to observe (using some form of DI)
- set up all the click listeners, text changed listeners etc
- *(optionally) set up any adapters*
- *(optionally) set up any SyncTriggers for things like animations*

In addition to that there will be:

- the [syncView()](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview) function which sets an affirmative state on each of the view components, in line with what the models indicate (or proxys this to an [adapter](https://erdo.github.io/android-fore/04-more-fore.html#adapter-animations) by calling adapter.notifyDataSetChangedAuto method).

Often there will also be the add / remove observers methods where the view registers with the models it is interested in - this is handled automatically in the Sync... classes, see below.


## Removing even more boiler plate

If the list of things you are observing gets a little long, you can remove some of this *add and remove* boiler plate by using an **ObservableGoup** (it's just a convenience class that maintains a list of observables internally)

### Using ObservableGroup in a ViewModel
Here's how you can use an ObservableGroup from a ViewModel:

``` kotlin

class MyViewModel(
    private val accountModel: AccountModel,
    private val networkInfo: NetworkInfo,
    private val emailInBox: EmailInBox,
    private val weatherRepository: WeatherRepository
) : ViewModel(), ObservableGroup by ObservableGroupImp(
    accountModel,
    networkInfo,
    emailInBox,
    weatherRepository) {

    private val observer = Observer { syncView() }

    init {
        addObserver(observer)
        syncView()
    }

    override fun syncView() {
       // Here you might create an immutable view state
       // to pass to your fragment (based on the state of
       // the models that you're observing).
       // You can use LiveData to make the final hop
       // to the fragment from here, or again use a fore
       // observable to make the ViewModel itself observable
        ...
    }

    override fun onCleared() {
        super.onCleared()
        removeObserver(observer)
    }
}

```

# SyncTrigger

The [SyncTrigger](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/ui/SyncTrigger.java) class lets you create a one off event (like an animation that must be fired only once) from inside the syncView() method (which is called at any time, [an arbitrary number of times](https://erdo.github.io/android-fore/05-extras.html#notification-counting)).

All "statey" view architectures have this issue (MVO, MVI, MVVM) whereas it's not an issue with MVP because that is event based to start with. Essentially we need a way to bridge the two worlds of state and events. There are a load of ways to do this, take a look [here](https://www.reddit.com/r/androiddev/comments/g6kgfn/android_databinding_with_livedata_holds_old_values/foabqm0/), [here](https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150), [here](https://gist.github.com/JoseAlcerreca/e0bba240d9b3cffa258777f12e5c0ae9), [here](https://github.com/android/architecture-samples/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SingleLiveEvent.java), and [here](https://github.com/android/architecture-components-samples/issues/63#issuecomment-310422475) for example.

Anyway this is fore's solution, but there is no need to use it if you already have a preferred way of handling this situation.

When using a SyncTrigger you need to implement the **triggered()** method which will be run when the SyncTrigger is fired (e.g. to run an animation), and also implement the **checkThreshold()** method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON). If the threshold is breached i.e. checkThreshold() returns **true**, then triggered() will be called.

For this to work you will need to call **check()** on the SyncTrigger each time the syncView() method is called by your observers. Alternatively you can call **checkLazy()** which will cause the first check result after the SyncTrigger has been constructed to be ignored. This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger. (see the SyncTrigger source for more details about this).

By default, the SyncTrigger will be reset when checkThreshold() again returns **false**. Alternatively you can construct the SyncTrigger with ResetRule.IMMEDIATELY for an immediate reset.

Please see [here](https://github.com/erdo/fore-state-tutorial/blob/master/app/src/main/java/foo/bar/example/forelife/ui/GameOfLifeActivity.kt) for example usage of the SyncTrigger.
