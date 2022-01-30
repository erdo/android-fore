# Reactive UIs

Data Binding is the old term for this, and its basic definition is: any changes of state that happen in your underlying model, get automatically represented in your view.

> "Any changes of state in your underlying model, get automatically represented in your view."

So if your shopping basket model is empty: the checkout button on your view needs to be invisible or disabled. And as soon as your shopping basket model has something in it, your checkout button needs to reflect that by being enabled. This concept is decades old, and in UI frameworks is generally implemented with some form of Observer pattern.

Lately it's been applied to other (non UI) areas of code very successfully under the name of *reactive* programming. Back at the UI layer, you could say that the view is *reacting* to changes in the model (i.e. the view layer does not need to explicitly check the model to see if it has changed, and of course there is no polling involved).


## **fore** Observables
In MVO, the models are usually Observable, and the Views are mostly doing the Observing.

By extending ObservableImp / implementing Observable in the case of java, or delegating to ObservableImp in the case of kotlin [like this](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/feature/wallet/Wallet.kt), the models gain the following characteristics:

- Any observers (usually views) can add() themselves to the model so that the **observer will be told of any changes in the model's state**
- When the model's state changes, each added observer will be told in turn by having its **somethingChanged()** method called (which in turn typically causes a call to **syncView()**)
- For this to work, all a model must do is call **notifyObservers()** whenever its own state changes (see the [Model](https://erdo.github.io/android-fore/02-models.html#shoom) section)
- To avoid memory leaks, **views are responsible for removing their observer callback** from the observable model once they are no longer interested in receiving notifications. That's typically a one liner: ```lifecycle.addObserver(LifecycleObserver(this, wallet))``` (or for compose: ```val walletState by wallet.observeAsState { wallet.state }```)
- The fact that the **fore** observable contract has no parameter means that this view layer code is extremely sparse, even if a View is Observing multiple Models, only a single observer is required.

## Connecting Compose Views and Models

For Compose UIs, use fore's observerAsState() extension function

<pre class="codesample"><code>val walletState by wallet.observeAsState { wallet.state }
</code></pre>

## Connecting Non-Compose Views and Models

For Non-Compose UIs, use fore's lifecycleObserver

<pre class="codesample"><code>lifecycle.addObserver(LifecycleObserver(this, wallet))
</code></pre>

This will also enable you to observer mutliple models if required

<pre class="codesample"><code>lifecycle.addObserver(LifecycleObserver(this, wallet, account, inbox))
</code></pre>

### How the Non-Compose UI works (long version)

Somewhere in the view layer (Activity/Fragment/View) there will be a piece of code like this:


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


**Be careful with Kotlin btw**

- val observer = this::syncView will **NOT** work
- val observer =  { syncView() } will **NOT** work

Both will compile and run though and the observer will be triggered successfully **but you will end up with a memory leak when you come to remove this observer as the reference will have changed.**


And in line with android lifecycle methods (of either the Activity, the Fragment or the View), this observer will be added and removed accordingly *(in this case we are observing two models: wallet and account, and we are using Fragment lifecycle methods to do it)*:

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


## <a name="boiler-plate"></a>Removing even more boiler plate

If the list of things you are observing gets a little long, you can remove some of this *add and remove* boiler plate by using an **ObservableGoup** (it's just a convenience class that maintains a list of observables internally)

### <a name="observablegroup"></a>Using ObservableGroup in a ViewModel

Here's how you can use an ObservableGroup with a ViewModel that needs to react to state changes in any/all of these observable classes (AccountModel, NetworkInfo, EmailInbox & WeatherRepository). The ViewModel is itself observable, so the reactive fragment code is similarly terse - that's what fore means by "thinner android view layers". We're using this technique in the [clean architecture modules sample app](https://github.com/erdo/clean-modules-sample/blob/main/app/ui/src/main/java/foo/bar/clean/ui/dashboard/DashboardViewModel.kt).

<pre class="codesample"><code>

class MyViewModel(
    private val accountModel: AccountModel,
    private val networkInfo: NetworkInfo,
    private val emailInBox: EmailInBox,
    private val weatherRepository: WeatherRepository
) : BaseViewModel(
    accountModel, networkInfo, emailInBox, weatherRepository
), Observable by ObservableImp() {

    var viewState = MyViewState()
        private set

    init {
        syncView()
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

For completeness, here is an example BaseViewModel

<pre class="codesample"><code>

abstract class BaseViewModel(
    vararg observablesList: Observable
): ViewModel(), SyncableView {

    private val observableGroup: ObservableGroup
    private val observer = Observer { syncView() }

    init {
        observableGroup = ObservableGroupImp(*observablesList)
        observableGroup.addObserver(observer)
    }

    override fun onCleared() {
        super.onCleared()
        observableGroup.removeObserver(observer)
    }
}
 </code></pre>

As you can see in the example BaseViewModel above, the observers will exist throught the life time of your viewmodel (which may or may not be what you want). You can add / remove the observers in line with  onStart / onStop by adding the onStart() and onStop() functions to the viewModel yourself and calling them from the host fragment or activity (the androidx viewModel doesn't support support for onStart onStop by itself).

You can alternatively use fore's **ViewModelObservability** to add this behaviour to your ViewModel as follows:

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

### <a name="forelifecycleobserver"></a><a name="lifecycleobserver"></a>fore LifecycleObserver

If you want to remove _even more_ boiler plate then you can use the fore LifecycleObserver from an Activity or Fragment which will handle the adding and removing for you (it hooks on to onStart() and onStop() internally):

 <pre class="codesample"><code>
class MyActivity : FragmentActivity(R.layout.activity_my), SyncableView {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycle.addObserver(
        LifecycleObserver(
            this,
            viewModel
        )
    )

  }

  override fun syncView() {
     ...
  }
}

 </code></pre>

You can see this technique in the [sample app for the persista library](https://github.com/erdo/persista/blob/main/example-app/src/main/java/foo/bar/example/ui/wallet/WalletsActivity.kt). It's also used to observe a view model in the [clean architecture sample](https://github.com/erdo/clean-modules-sample/blob/main/app/ui/src/main/java/foo/bar/clean/ui/dashboard/DashboardActivity.kt). The point of all these techniques is to reduce view layer code to its absolute fundamentals: what things look like.


## <a name="somethingchanged-parameter"></a>Why not put a parameter in the Observer.somethingChanged() function?

This is the obvious question for anyone familiar with Reactive Stream based APIs like Rx, or indeed anyone who worked with the messenger bus APIs that used to be popular on Android like EventBus by greenrobot or Otto by Square. And actually, in the distant past (long before publishing) the fore Observer interface did briefly have a generic on it along the lines of Observable&lt;SomeClass&gt; which supported this behaviour. But it was removed when we realised that adding it had *significantly increased* the amount of boiler plate and view code that had to be written.

If you're sceptical about that - and why wouldn't you be? I'd encourage you to do a before and after comparison in a small sample app, there is nothing quite like seeing it for yourself. You could use one of the fore samples to get started [very simple app](https://github.com/erdo/persista/tree/main/example-app), [clean modules app](https://github.com/erdo/clean-modules-sample) - make sure to consider what happens once you add more data sources that need to be observed

There are a few different ways to explain why reactive stream style APIs are not a good fit here, but a good starting point would be to say that firstly, if you know what a reactive stream is, and you are certain that you **want** an app architecture based on it (for whatever reason), then I'd advise you to stay with Rx or migrate to Flow! fore observers are NOT reactive streams - quite deliberately so.

### Reactive Streams
While we're on the subject, let's briefly detour to a discussion of reactive streams. Reactive Streams could just as well have been called Observable Streams, and you can consider it a combination of two concepts:

- Observers (tell me whenever you've changed)
- Streams (data and operators like .map .filter etc)

For some specific use cases: handling streams of changing data, which you want to process on various threads (like the raw output of an IoT device for example) reactive streams is a natural fit. The needs of most android app architectures however tend to be a little more along the lines of:

 - connect to a network to download discreet pieces of data (_always_ on an IO thread)
 - update a UI, based on some change of state (_always_ on the UI thread)

You certainly _can_ treat everything as a reactive stream if you wish, and if parts of your app actually aren't a great match for reactive streams, you can (and sometimes must) have your functions return Single&lt;Whatever&gt;s. Unfortunately regular code that touches reactive streams often gets _reactive-streamified_ like this, giving it unasked-for complexity (even code that isn't, and has no need to be, reactive in the first place).

This is how reactive streams can unintentionally spread complexity throughout a code base. When this tendency-to-spread is combined with a large non-obvious API, and a focus on asynchronicity even when none is required it can quite easily swamp otherwise fairly trivial app projects. That risk is increased on larger projects employing developers with a mixture of skill levels, especially where there is a reasonably high turn over of developers. Keeping control of ever ballooning complexity in these situations can be a significant challenge.

This is somewhat related to the famous [what color is your function](http://journal.stuffwithstuff.com/2015/02/01/what-color-is-your-function/) blog post - although that post is dealing with asynchronous code in general, which kotlin's **suspend** [handles pretty well](https://elizarov.medium.com/how-do-you-color-your-functions-a6bb423d936d). There is a parallel here though where blue is regular code, and red is reactive-streams code.

*Aside: the justification for using reactive-streams in android used to be to prevent "callback-hell", there are some pretty decent ways of [handling that](https://dev.to/erdo/tutorial-kotlin-coroutines-retrofit-and-fore-3874#quick-refresher-on-callback-hell) in kotlin nowadays regardless.*

Anyway, it's entirely possible to treat these two concepts separately. We can consider a piece of code's _observable nature_ separate to the _data that actually changed_ (that's what **fore** does - it deals exclusively with the first, letting you handle the second however you wish). This means your function signatures don't have to change, you can continue returning a Boolean if that's what you need to do, and Observable&lt;Somethings&gt; or Flow&lt;Somethings&gt; won't slowly spread throughout your code base.

It turns out that this separation **also** has some pretty stunning advantages in terms of boiler plate written in the view layer as we'll see...

### 1) Views want different things from the same model
Usually, view layer components are going to want different things from the same model.

(If you've just joined us here by the way, we are using the term model as it's defined by [wikipedia](https://en.wikipedia.org/wiki/Domain_model), a software representation of a real life thing, the model can have state and/or logic. And for all this to work, it just has to be observable i.e. if its state changes, it needs to tell all its observers that its state changed. The following example models expose their state via getters, but you can also expose a kotlin data class which encapsulates all the public state in an immutable object like we do in the clean architecture sample - it makes no difference to the pattern or how fore works.)

Take an example **AccountModel**, most views are going to want to know if the account is logged in or not, a settings page might want to display the last time the user logged in, an account page might want to know the status of the account such as ACTIVE, DORMANT, BANNED or whatever. Maybe a view will want to show all those things, or just two of them.

Regardless, our example model will be managing these three pieces of state (via a single immutable data class if that's how you like to write your code):

<pre class="codesample"><code>
fun hasSessionToken(): Boolean
fun getLastLoggedInTimeStampMs(): Long
fun getAccountStatus(): Account.Status

</code></pre>

All those states may change (potentially as a result of a network request completing in the background, or a notification arriving on the device etc). And if they change, the views need to update themselves immediately without us needing to do it (that's the whole point of reactive UIs after all!).

If we make all these things individually observable, we might choose something like RxJava observables, or LiveData, and the views will need to observe each piece of state individually. Taking LiveData as an example, the view layer will have to contain something like this:


<pre class="codesample"><code>
accountModel.sessionTokenLiveData.observer(this, Observer { hasToken ->
  //update the view based on the hasToken Boolean
})

accountModel.lastLoggedInTimeStampLiveData.observer(this, Observer { timeStamp ->
  //update the view based on the timeStamp Long
})

accountModel.accountStateLiveData.observer(this, Observer { status ->
  //update the view based on the status class
})

</code></pre>

We already learnt about how updating views in this way introduces very [hard to spot bugs](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k). But for the moment let's focus on the view layer boiler-plate that needs to be written. If you've worked with MVVM and LiveData, you probably recognise this as fairly typical. None of the observables can be reused because they all have different parameter requirements, so they all have to be specified invidivually. This is what **fore** code would also look like if we had a parameter in the somethingChanged() method, luckily there is no parameter. All fore observables have exactly the same code signature, no matter what type of data is involved. This is the fore equivalent of the code above (the data is accessed directly from the models from within syncview() - the reason this is totally robust and safe by the way is that fore observers are fired on the UI thread by default).

<pre class="codesample"><code>
lifecycle.addObserver(
  LifecycleObserver(this, accountModel)
)

</code></pre>

*(NB: If you've used MVI before, you'll immediately spot that we can improve this situation by observing a single immutable viewState - but you have to enforce that yourself, it doesn't come automatically as a result of the api design. It also won't help if your view layer is observing more than one model...)*


### 2) Views want things from more than one model
Any non-trivial reactive UI is going to be interested in data from more than one source (all of which could change with no direct user input and need to be immediately reflected in the UI). It's easy to imagine a view that shows the number of unread emails, the user's current account status, and a little weather icon in a corner. Something like **MVP / MVVM / MVI** would have you write a **Presenter / ViewModel / Interactor** respectively that would aggregate that data for you, but as we discovered: 1) it's often [not necessary](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) and 2) the problem is still there, it just gets moved to the Presenter, the ViewModel or the Interactor.

Each model or repo class is going to have different types of state available to observe, so the view layer is going to need to manage even more observer implementations, (we'll stick with LiveData examples for brevity but the same issue presents itself with an API like RxJava's - of course Rx will have operators that help, but your developers have to know about them & use them properly, it's not something that comes for free due to the API design):


<pre class="codesample"><code>
emailInbox.unreadCountLiveData.observer(this, Observer { unread ->
  //update the [view / presenter / viewmodel / viewState] based on the unread Int
})

accountModel.sessionTokenLiveData.observer(this, Observer { hasToken ->
  //update the [view / presenter / viewmodel / viewState] based on the hasToken Boolean
})

accountModel.lastLoggedInTimeStampLiveData.observer(this, Observer { timeStamp ->
  //update the [view / presenter / viewmodel / viewState] based on the timeStamp Long
})

accountModel.accountStateLiveData.observer(this, Observer { status ->
  //update the [view / presenter / viewmodel / viewState] based on the status enum
})

weatherModel.weatherForecastLiveData.observer(this, Observer { forecast ->
  //update the [view / presenter / viewmodel / viewState] based on the forecast String
})

weatherModel.temperatureLiveData.observer(this, Observer { temperature ->
  //update the [view / presenter / viewmodel / viewState] based on the temperature int
})

weatherModel.windSpeedLiveData.observer(this, Observer { windSpeed ->
  //update the [view / presenter / viewmodel / viewState] based on the windSpeed int
})

</code></pre>

Here's the fore equivalent to that code

<pre class="codesample"><code>
lifecycle.addObserver(
  LifecycleObserver(this, emailInbox, accountModel, weatherModel)
)

</code></pre>

Or you could use a BaseViewModel to do this (see above for the BaseViewModel code)

<pre class="codesample"><code>
class MyViewModel(
    private val emailInBox: EmailInBox,
    private val accountModel: AccountModel,
    private val weatherModel: WeatherModel
) : BaseViewModel(emailInBox, accountModel, weatherModel) {
  ...
}

</code></pre>

This kind of code is only possible because fore separates a model's _observable nature_ from the _data that actually changed_

### Remove the parameter, remove the boilerplate

Doing away with a parameter in somethingChanged() is the key innovation in **fore** that enables **any view to observe any model** or multiple models, with almost no boiler plate. It's also what powers the robustness you get from using [syncView()](https://erdo.github.io/android-fore/01-views.html#syncview), and it's what lets us write simple code like this:

<pre class="codesample"><code>
//single observer reference
private var observer = Observer { syncView() }

override fun onStart() {
    super.onStart()
    emailInbox.addObserver(observer)
    accountModel.addObserver(observer)
    weatherModel.addObserver(observer)
    syncView() //<-- don't forget this
}

override fun onStop() {
    super.onStop()
    emailInbox.removeObserver(observer)
    accountModel.removeObserver(observer)
    weatherModel.removeObserver(observer)
}

</code></pre>

Or take it further as we did with the examples above and remove almost all the boiler plate with an [ObservableGroup](https://erdo.github.io/android-fore/03-reactive-uis.html#observablegroup), or the fore [LifecycleObserver](https://erdo.github.io/android-fore/03-reactive-uis.html#lifecycleobserver).

> "reduce view layer code to its absolute fundamentals: what things look like"

This lets you reduce view layer code to its absolute fundamentals: what things look like. Imagine a fairly complex reactive UI that displays if the user is logged in or not, shows the last time the user logged in, the number of unread emails, what the account status is, a weather forecast, and the current wind speed and temperature. With appropriately written, observable models, the syncView implementation for that screen would be:

<pre class="codesample"><code>
fun syncView() {
    homepage_unreademails.text = "${emailInbox.getUnreadCount()}"
    homepage_loggedin.text = if (accountModel.hasSessionToken()) "IN" else "OUT"
    homepage_lastloggedin.text = LAST_LOGGED_IN_FORMATTER.format(accountModel.getLastLoggedInTimeStampMs())
    homepage_accountstatus.text = accountModel.getStatus().name
    homepage_weatherforecast.text = weatherModel.getForecast()
    homepage_temperature.text = "${weatherModel.getTemperature()}"
    homepage_windspeed.text = "${weatherModel.getWindSpeed()}"
}

</code></pre>

If we used a ViewModel to constuct a ViewState (placed in between the Activity/Fragment code and the model code), the syncView() function can directly reference this single ViewState, it would have the same number of lines but it would make the code even clearer.

<pre class="codesample"><code>
fun syncView() {
    homepage_unreademails.text = viewState.unreadCountText
    homepage_loggedin.text = viewState.loggedInStatusText
    homepage_lastloggedin.text = viewState.lastLoggedInText
    homepage_accountstatus.text = viewState.accountStatusText
    homepage_weatherforecast.text = viewState.forecastText
    homepage_temperature.text = viewState.temperatureText
    homepage_windspeed.text = viewState.windspeedText
}

</code></pre>

[This section](https://dev.to/erdo/tutorial-android-fore-basics-1155#now-for-the-really-cool-stuff) of the dev.to tutorial on fore basics is worth a read, but the upshot is that adding a parameter to the somethingChanged() function would balloon the amount of code that gets written in the view layer.

It would also lead developers down the wrong path regarding how to move data about in the UI layer whilst ensuring consistency when the application is rotated etc. It sounds a little strange, but part of the benefit of using **fore** in a team is that it automatically discourages developers from making that mistake. It's almost like an automatic, invisible code review.

Not having the ability to send data via the somethingChanged() function is one of the key reasons that fore UI code tends to be so compact compared with other architectures - and also why fore naturally lends itself to supporting rotation on android.


> "adding a parameter to the somethingChanged() function would balloon the amount of code that gets written in the view layer"


Try to get comfortable using these observers to just notify observing view code of any (unspecified) changes to the model's state (once the observing view code has been told there are changes, it will call fast returning getters on the model to find out what actually happened, redraw it's state, or whatever - if this isn't straight forward then the models you have implemented probably need to be refactored slightly, check the [observer vs callback](https://erdo.github.io/android-fore/05-extras.html#observer-listener) discussion first).

For some, this is a strange way to develop, but once you've done it a few times and you understand it, the resulting code is rock solid and very compact.
