# <a name="somethingchanged-parameter"></a>fore API deep dive

fore's observer API is very simple, it's a single, parameter-less function: **somethingChanged()** which is called on the UI thread whenever the model in question has changed. The function doesn't include a parameter for the actual data that changed, so why not add that? This is the obvious question for anyone familiar with Reactive Stream based APIs like RxJava or Kotlin Flow, or indeed anyone who worked with the messenger bus APIs that used to be popular on Android like EventBus by greenrobot or Otto by Square. And actually, in the distant past (long before publishing) the fore Observer interface did briefly have a generic on it along the lines of Observable&lt;SomeClass&gt; which supported this behaviour. But it was removed when we realised that adding it had *significantly increased* the amount of boiler plate and view code that had to be written.

If you're sceptical about that - and why wouldn't you be? I'd encourage you to do a before and after comparison in a small sample app, there is nothing quite like seeing it for yourself. You could use one of the fore samples to get started [very simple app](https://github.com/erdo/persista/tree/main/example-app) or [clean modules app](https://github.com/erdo/clean-modules-sample) - make sure to consider what happens once you add more data sources that need to be observed.

There are a few different ways to explain why reactive stream style APIs are not a good fit here, but a good starting point would be to say that firstly, if you know what a reactive stream is, and you are certain that you **want** an app architecture based on it (for whatever reason), then I'd advise you to stay with Rx or migrate to Flow! fore observers are NOT reactive streams - quite deliberately so.

By the way, much of what we are about to discuss is specific to mobile applications. The server side is a very different beast. Of course, blindly applying best practice from one context to a totally different context would be a beginner's mistake that we are going to avoid here! [\[1\]](#1)

Anyway you might want to consider the possibility that using reactive streams to tie your architectural layers together in a mobile client application may only be providing a local maximum in terms of performance and code clarity, there is another way...

## Reactive Streams
While we're on the subject, let's briefly detour to a discussion of reactive streams. Both RxJava and Kotlin Flow are implementations of the reactive streams initiative. Reactive Streams could just as well have been called Observable Streams, and you can consider it a combination of two concepts:

- Observers (tell me whenever you've changed)
- Streams (data and operators like .map .filter etc)

For some specific use cases: handling streams of changing data which you want to process on various threads (like the raw output of an IoT device for example) reactive streams is a natural fit. That's especially true when it comes to **back pressure**.

### Back pressure

Back pressure refers the problem of data being *produced*, faster than it is able to be *consumed*. Handling back pressure in streams of data is basically what [reactive streams](http://www.reactive-streams.org/) lives for. The needs of most android app architectures however tend to be a little more along the lines of:

 - connect to a network to download discreet pieces of data (_always_ on an IO thread, takes _seconds_)
 - update a UI, based on some change of state (_always_ on the UI thread, takes _milliseconds_)

The timescales that these UI state changes happen in, are orders of magnitude slower than the timescales that would require back pressure management.

To put that another way: the **production** of data in an app (a user logs in, and a session token is fetched from the network) tends to happen in the order of **seconds**. The **consumption** of that data (the waiting spinner on the ui is changed from visible to invisible) is often a **sub-millisecond** affair.

This is very obviously not a situation that reactive streams was designed to help with (unlike processing streaming video for example). You might wonder why on earth RxJava featured so heavily in android architectures for half a decade or so, and why Flow (another implementation of reactive streams) is now such a popular replacement 🤷 [\[2\]](#2)

### What if we just pretend?

You certainly _can_ treat everything as a reactive stream if you wish, and if parts of your app actually aren't a great match for reactive streams, you can (and sometimes must) have your functions return Single&lt;Whatever&gt;s. Unfortunately regular code that touches reactive streams often gets _reactive-streamified_ like this, giving it unasked-for complexity (even code that isn't, and has no need to be asynchronous or reactive, let alone reactive streams, in the first place).[\[3\]](#3)

## The fore approach

Those two concepts we mentioned above (Observers / Streams) can be treated separately. We can consider a piece of code's _observable nature_ separate to the _data that actually changed_ . And that's what fore does - it deals exclusively with the first, letting you handle the second however you wish. This means your function signatures don't have to change, you can continue returning a Boolean if that's what you need to do, and Observable&lt;Something&gt;s or Flow&lt;Something&gt;s won't slowly spread throughout your code base unnecessarily.

One of the benefits of this is that it lets you isolate asynchronous code styles, to code that actually needs to be asynchronous (db access, network connections, calculation work etc). The rest of the code remains explicitly synchronous, on the UI thread, and highly predictable / testable because of it.

### The UI thread

The funny thing is... if you're writing an app that has a UI, much of the code that you write will be on the UI thread _anyway_.

Take a very common pattern with reactive streams based android apps: an app **collecting a Flow in a ViewModel to update its UI**. That code _still_ runs on the UI thread, even though it's written with all the trappings of asynchronous reactive streams 🧐 Android prevents you from shooting yourself in the foot here - for a change ;) and viewModelScope is bound to the UI thread - this is why you can update the UI from inside it without needing to switch to the UI thread first. (It's the same reason that fore's syncView() is always called from the UI thread).

Apart from removing this pretend asynchronous code from the view layer, there are other boiler plate advantages to fore style observers...

## 1) Views want different things from the same model
Usually, view layer components are going to want different things from the same model.

(If you've just joined us here by the way, we are using the term model as it's defined by [wikipedia](https://en.wikipedia.org/wiki/Domain_model), a software representation of a real life thing, the model can have state and/or logic. And for all this to work, it just has to be observable i.e. if its state changes, it needs to tell all its observers that its state changed. The following example models expose their state via getters, but you can make your own choices here - it makes no difference to the pattern or how fore works.)

Take an example **AccountModel**, most views are going to want to know if the account is logged in or not, a settings page might want to display the last time the user logged in, an account page might want to know the status of the account such as ACTIVE, DORMANT, BANNED or whatever. Maybe a view will want to show all those things, or just two of them.

Regardless, our example model will be managing these three pieces of state

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

We already learnt about how updating views in this way introduces very [hard to spot bugs](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k). But for the moment let's focus on the view layer boiler-plate that needs to be written. If you've worked with MVVM and LiveData, you probably recognise this as fairly typical.

None of the observables can be reused because they all have different parameter requirements, so they all have to be specified invidivually. This is what **fore** code would also look like if we had a parameter in the somethingChanged() method, luckily there is no parameter. All fore observables have exactly the same code signature, no matter what type of data is involved.

This is the fore equivalent of the code above (the data is accessed directly from the models from within the syncView() function)

<pre class="codesample"><code>
lifecycle.addObserver(
  LifecycleObserver(this, accountModel)
)

</code></pre>

Now, hopefully you'll have spotted a way around this problem even without using fore (especially if you've worked with something like MVI before), and that's to have all your individual states wrapped up in a single immutable state like this:

<pre class="codesample"><code>
data class AccountState (
    val hasSessionToken: Boolean,
    val lastLoggedInTimeStampMs: Long,
    val status: Account.Status,
)

...

var currentState = AccountState()
    private set

</code></pre>

But you have to enforce that yourself, it doesn't come automatically as a result of the api design. It also won't help if you are observing more than one model, which is the next problem...


## 2) Views want things from more than one model
Any non-trivial reactive UI is going to be interested in data from more than one source (all of which could change with no direct user input and this needs to be immediately reflected in the UI). It's easy to imagine a view that shows the number of unread emails, the user's current account status, and a little weather icon in a corner.

Probably best not to write that management code in an Activity or a Fragment, and typically you would write a Presenter, ViewModel or Interactor to help you manage all that according to your preferences. But the problem of observing multiple things at once is still there, it just gets moved to a different class.

Each model is going to have different types of state available to observe, so the view layer is going to need to manage even more observer implementations, (we'll stick with LiveData examples for brevity but the same issue presents itself with an API like RxJava's)

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

fore's API enables the observing of multiple **things** by simply specifying a list of those things: you can observe as many things as you care to list.

Both Rx and Flow have operators that can **combine** observable things, so that would be your option with a reactive streams solution. You and your team do have to know about those functions & use them properly though, it's not something that comes for free due to the API design.

But both Flow and RxJava need to have different dedicated function implementations for combining: **two things**, **three things** or **four things** (Flow's combine functions go up to 5 **things**, after that you need to write your own function).

*If you think this is a little extreme, at the time of writing I am part of the dev team of a fairly popular app (500k+ users), whose main UI reactively updates itself based on 7 different observable data sources*

When you look into the details of the reactive streams solution to this very common problem of reactive mobile UIs, it can start to feel a little hacky - (or maybe pragmatic if what you are actually doing is processing streams of real time data, of course).

Here's the fore equivalent to the code above

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

## Remove the parameter, remove the boilerplate

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

Or take it further as we did with the examples above and remove almost all the boiler plate with an ObservableGroup or the fore [LifecycleObserver](https://erdo.github.io/android-fore/03-reactive-uis.html#lifecycleobserver).

<pre class="codesample"><code>
lifecycle.addObserver(LifecycleObserver(this, emailInbox, accountModel, weatherModel))

</code></pre>

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


Try to get comfortable using these observers to just notify observing view code of any (unspecified) changes to the model's state (once the observing view code has been told there are changes, it will call fast returning getters on the model to find out what actually happened, redraw it's state, or whatever - if this isn't straight forward then the models you have implemented probably need to be refactored slightly.

For some, this is a strange way to develop, but once you've done it a few times and you understand it, the resulting code is rock solid and very compact.

#### [1]
Most of developer internet is talking about the server side. Somewhat amusingly, this advice is sometimes absorbed and repeated unthinkingly for a mobile context, without the new author realising that the prior _assumptions_ on which the advice was built are no longer relevant in their new context.

**Stateless vs Stateful** on the server side is a great example of this. Stateless microservices have a lot going for them. While potentially less intuitive and sometimes less performant, stateless is easier to scale using cloud services, stateless can be made more robust (any managed state can be hard to recover after a system crash, potentially affecting millions of users at once), and if the service can be designed to not even need a database, it will be significantly less expensive to maintain and meet SLAs with that service.

None of these considerations are applicable to a mobile client application of course.

Mobile clients have a UI thread, view layers that come into and out of existence from something like a device rotation, the devices have low processor speeds, but they have high performance requirements such that 10ms vs 100ms significantly affects a user's perception of speed. For instance, a network connection made each time a view is re-navigated to, can feel very sluggish (just from the json parsing alone i.e. even if that network call is cached locally somewhere in the data layer).

#### [2]
There were a few reasons that RxJava exploded in popularity when it arrived on the android scene. Firstly: every one hated AsyncTask (although you could always wrap it, and once you were able to [give it a lamda interface](https://erdo.github.io/android-fore/04-more-fore.html#asynctasks-with-lambdas) it was actually pretty ok - but not many people were aware you could do that). The second often stated reason was that it could help prevent "callback-hell", there are some pretty decent ways of [handling that](https://dev.to/erdo/intro-to-eithers-in-android-2om9#so-you-said-eithers-were-good) in kotlin nowadays regardless.

And as for Kotlin Flow? Flow is a much better RxJava in Android, so if you are already heavily invested in a reactive streams architecture, there is a clear mental migration path from RxJava to Flow

#### [3]
This is how reactive streams can unintentionally spread complexity throughout a code base. When this **tendency-to-spread** is combined with a large non-obvious API, and a focus on asynchronicity even when none is required it can quite easily swamp otherwise fairly trivial app projects. That risk is increased on larger projects employing developers with a mixture of skill levels, especially where there is a reasonably high turn over of developers. Keeping control of ever ballooning complexity in these situations can be a significant challenge.

This is somewhat related to the famous [what color is your function](http://journal.stuffwithstuff.com/2015/02/01/what-color-is-your-function/) blog post - although that post is dealing with asynchronous code in general, which kotlin's **suspend** [handles pretty well](https://elizarov.medium.com/how-do-you-color-your-functions-a6bb423d936d). There is a parallel here though where blue is regular code, and red is reactive streams code (again though, Kotlin Flow beats RxJava hands down here. But even Flow reactive streams can be viewed as an unnecessary complication when applied to android architectural layers).

