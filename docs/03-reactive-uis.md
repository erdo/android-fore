# Reactive UIs

Essentially this means your UI responds immediately to any change to the system state, and it does so automatically.

It doesn't require the **user** to manually refresh the screen... it doesn't even require the **developer** to "manually" refresh the screen (using techniques like polling, or refreshing things from the onResume() callback for example).

When setup correctly, the UI layer can become extremely simple, all it needs to do is synchronize it's UI with whatever state the system has. In a reactive UI, it does that in milliseconds, whenever it's told that something has changed

> "Any changes of state in your underlying model, get automatically represented in your view."

So if your shopping basket model is empty: the checkout button on your view needs to be invisible or disabled. And as soon as your shopping basket model has something in it, your checkout button needs to reflect that by being enabled. This concept is decades old, and in UI frameworks is generally implemented with some form of Observer pattern.

Lately it's been applied to other (non UI) areas of code very successfully under the name of *reactive* programming. Back at the UI layer, you could say that the view is *reacting* to changes in the model (i.e. the view layer does not need to explicitly check the model to see if it has changed).


## **fore** Observables
To get the best out of fore, the models are usually Observable, and the Views are mostly doing the Observing.

By extending ObservableImp / implementing Observable in the case of java, or delegating to ObservableImp in the case of kotlin [like this](https://github.com/erdo/android-fore/blob/master/app-examples/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/feature/wallet/Wallet.kt), the models gain the following characteristics:

- Any observers (usually views) can add() themselves to the model so that the **observer will be told of any changes in the model's state**
- When the model's state changes, each added observer will be told in turn by having its **somethingChanged()** method called (which in turn typically causes a call to **syncView()** or a recompose in the case of Compose UI)
- For this to work, all a model must do is call **notifyObservers()** whenever its own state changes (see the [Model](https://erdo.github.io/android-fore/02-models.html#shoom) section)
- To avoid memory leaks, **views are responsible for removing their observer callback** from the observable model once they are no longer interested in receiving notifications. That's typically a one liner: ```lifecycle.addObserver(LifecycleObserver(this, wallet))``` (or for compose: ```val walletState by wallet.observeAsState { wallet.state }```)
- The fact that the **fore** observable contract has no parameter means that this view layer code is extremely sparse in non compose code, even if a View is Observing multiple Models, only a single observer is required.

## Connecting Views and Models

The easiest way is to use fore's lifecycleObserver

<pre class="codesample"><code>lifecycle.addObserver(LifecycleObserver(this, wallet))
</code></pre>

This will also enable you to observe multiple models if required

<pre class="codesample"><code>lifecycle.addObserver(LifecycleObserver(this, wallet, account, inbox))
</code></pre>

### Connecting Views and Models in Compose

For Compose UIs, simply use fore's observerAsState() extension function

<pre class="codesample"><code>val walletState by wallet.observeAsState { wallet.state }
</code></pre>

### How connecting views and models works

For a non compose UI, even if you don't use the lifecycle observer, it's still quite easy to setup the observers manually. Somewhere in the view layer (Activity/Fragment/View) you need a piece of code like this:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
Observer observer = this::syncView;
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
val observer = Observer { syncView() }
 </code></pre>

And in line with android lifecycle methods (of either the Activity, the Fragment or the View), this observer needs to be added and removed accordingly *(in this case we are observing two models: wallet and account, and we are using Fragment lifecycle methods to do it)*:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
@Override
protected void onStart() {
    super.onStart();
    wallet.addObserver(observer);
    account.addObserver(observer);
    syncView(); //  <- don't forget this
}

@Override
protected void onStop() {
    super.onStop();
    wallet.removeObserver(observer);
    account.removeObserver(observer);
}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
override fun onStart() {
    super.onStart()
    wallet.addObserver(observer)
    account.addObserver(observer)
    syncView() //  <- don't forget this
}

override fun onStop() {
    super.onStop()
    wallet.removeObserver(observer)
    account.removeObserver(observer)
}
 </code></pre>

That's everything you need to do to get bullet proof reactive UIs in your app, everything now takes care of itself, no matter what happens to the state of the model or the rotation of the device.

> "The point of all these techniques is to reduce view layer code to its absolute fundamentals: what things look like"

### <a name="observablegroup"></a>Integrating a ViewModel

A common set up is a Fragment or Activity class observing the ViewModel state, and the ViewModel in turn, observing whatever Domain models it needs. Typically, you'll add those observers in the onStart and remove them in the onStop.

The easiest way to do that is to use fore's **ViewModelObservability** to add this behaviour to your ViewModel as follows:

<pre class="codesample"><code>

class MyViewModel(
    private val accountModel: AccountModel,
    private val networkInfo: NetworkInfo,
    private val emailInBox: EmailInBox,
    private val weatherRepository: WeatherRepository
) : ViewModel(), SyncableView, ViewModelObservability by ViewModelObservabilityImp(
    accountModel, networkInfo, emailInBox, weatherRepository
) {

    var viewState = MyViewState()
        private set

    init {
        initSyncableView(this)
    }

    // this gets called whenever our domain models' state changes
    override fun syncView() {
        // Here you might create an immutable view state
        // to pass to your fragment (based on the state of
        // the various models that you're observing)
        viewState = MyViewState(
            ...
        )
        notifyObservers()
    }
}
 </code></pre>

Take a look at the [clean architecture](https://github.com/erdo/clean-modules-sample/blob/main/app/ui/src/main/java/foo/bar/clean/ui/dashboard/DashboardViewModel.kt) app for example use