# Reactive UIs

Data Binding is the old term for this, and its basic definition is: any changes of state that happen in your underlying model, get automatically represented in your view.

> "Any changes of state in your underlying model, get automatically represented in your view."

So if your shopping basket model is empty: the checkout button on your view needs to be invisible or disabled. And as soon as your shopping basket model has something in it, your checkout button needs to reflect that by being enabled. This concept is decades old, and in UI frameworks is generally implemented with some form of Observer pattern.

Lately it's been applied to other (non UI) areas of code very successfully under the name of *reactive* programming. Back at the UI layer, you could say that the view is *reacting* to changes in the model (i.e. the view layer does not need to check the model to see if it has changed, and of course there is no polling involved).


## SyncView()

MVO uses one of the most simple (but extremely reliable) reactive implementations you can have. It really all boils down to a single **syncView()** method *(the concept is similar to MVI's render() method - compare MVO with MVI [here](https://erdo.github.io/android-fore/00-architecture.html#comparison-with-mvi))*. On the surface it looks very simple, but there are some important details to discuss that can trip you up, or otherwise result in a less than optimal implementation of this method. The basic philosophy is: If a model being observed changes **in any way**, then the **entire** view is refreshed.

That simplicity is surprisingly powerful so we're going to go into further detail about why, after I've quoted myself so that you remember it...

> "If a model being observed changes **in any way**, then the **entire** view is refreshed."

That doesn't mean that you can't subdivide your views and only refresh one of the subviews if you want by the way - as long as both (sub)views have their own syncView() method and they are observing their respective models.


### Quick Tutorial

I'm going to defer to the [dev.to spot the bug tutorial](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k) for this.


### Writing an effective syncView() method

*A lot of this advice also applies to writing MVI render() methods. MVO's reducer() function helps to maintain state consistency, but it won't matter if the render() method written in the view layer doesn't set an affirmative state for each UI element.*

As part of refreshing the entire view, the syncView() method must set an **affirmative state** for every view element property that you are interested in. What that means is that where there is an **if** there must always be an **else** for each property.

> "Where there is an if, there must always be an else"

It's not good enough to just set a button as **disabled** if a total is 0 or less. You must also set that button as **enabled** if the total is greater than 0. If you don't set an affirmative step for both the positive and negative scenarios, then you run the risk of a syncView() call not setting a state at all, which means that the result will be indeterministic (it will be whatever state it had previously). This is one of those sneaky edge case things that at first glance might look fine, but can reveal itself as a bug later.

So don't do this:



<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
if (basket.isBelowMinimum()){
    checkoutButton.setEnabled(false);
    totalPrice.setColour(red);
}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
if (basket.isBelowMinimum()){
    checkoutButton.enabled = false
    totalPrice.color = red
}
 </code></pre>


At the very least you must do this:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
if (basket.isBelowMinimum()){
    checkoutButton.setEnabled(false);
    totalPrice.setColour(red);
} else {
    checkoutButton.setEnabled(true);
    totalPrice.setColour(black);
}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
if (basket.isBelowMinimum()){
    checkoutButton.enabled = false
    totalPrice.color = red
} else {
    checkoutButton.enabled = true
    totalPrice.color = black
}
 </code></pre>

But you'll find that by focusing on the UI component first rather than the condition, you can get some extremely tight code like so:


<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
checkoutButton.setEnabled(!basket.isBelowMinimum());
totalPrice.setColour(basket.isBelowMinimum() ? red : black);
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
checkoutButton.enabled = !basket.isBelowMinimum()
totalPrice.color = if (basket.isBelowMinimum()) red else black
 </code></pre>


### Don't count notifications
Be careful not to rely on syncView() being called a certain number of times, as it results in fragile code. You can't predict when it will be called, and your syncView() code needs to be prepared for that. Make sure you understand [this](https://erdo.github.io/android-fore/05-extras.html#notification-counting) and you'll be writing solid syncView() implementations that will survive code refactors.

### Beware infinite loops
One final point to mention is about syncing your view directly from UI element "changed" listeners. It's generally fine to do that, and you should be able to call syncView() whenever you like, after all.

However, you will usually be setting a state on that UI element during your syncView(), if that UI element then calls its "changed" listener, you will end up calling syncView() again and find yourself in an infinite loop.

Of course, if you're setting a state on a UI element which is the same as the state it already had, it shouldn't be firing it's "changed" listeners anyway. But Android. And indeed Android's EditText calls afterTextChanged() even when the text is identical to what it had before. Thankfully it's not a very common issue and the [work around](https://github.com/erdo/android-architecture/blob/todo-mvo/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/ui/widget/CustomEditText.java) is easy. (Interesting that the equivalent TextInput component of ReactNative doesn't suffer from this "feature").

## **fore** Observables
In MVO, the models are usually Observable, and the Views are mostly doing the Observing.

By extending ObservableImp / implementing Observable in the case of java, or delegating to ObservableImp in the case of kotlin [like this](https://github.com/erdo/fore-full-example-02-kotlin/blob/master/app/src/main/java/foo/bar/example/fore/fullapp02/feature/basket/BasketModel.kt), the models gain the following characteristics:

- Any observers (usually views) can add() themselves to the model so that the **observer will be told of any changes in the model's state**
- When the model's state changes, each added observer will be told in turn by having its **somethingChanged()** method called (which in turn typically causes a call to **syncView()**)
- For this to work, all a model must do is call **notifyObservers()** whenever its own state changes (see the [Model](https://erdo.github.io/android-fore/02-models.html#shoom) section)
- When the model is constructed in **ASYNCHRONOUS** mode, these notifications will always be delivered on the UI thread so that view code need not do anything special to update the UI
- To avoid memory leaks, **views are responsible for removing their observer callback** from the observable model once they are no longer interested in receiving notifications
- Typically Views **add()** and **remove()** their observer callbacks in android lifecycle methods such as View.onAttachedToWindow() and View.onDetachedFromWindow()
- The fact that the **fore** observable contract has no parameter means that this view layer code is extremely sparse, even if a View is Observing multiple Models, only a single observable is required.

## Connecting Views and Models

So basically, somewhere in the view layer (Activity/Fragment/View) there will be a piece of code like this:


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


And in line with android lifecycle methods (of either the Activity, the Fragment or the View), this observer will be an added and removed accordingly *(in this case we are observing two models: wallet and account, and we are using View lifecycle methods to do it)*:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
@Override
protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    wallet.addObserver(observer);
    account.addObserver(observer);
    syncView(); //  <- don't forget this
}

@Override
protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    wallet.removeObserver(observer);
    account.removeObserver(observer);
}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    wallet.addObserver(observer)
    account.addObserver(observer)
    syncView() //  <- don't forget this
}

override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    wallet.removeObserver(observer)
    account.removeObserver(observer)
}
 </code></pre>

If you're still not satisfied with that, you can [remove even more boiler plate](https://erdo.github.io/android-fore/01-views.html#removing-even-more-boiler-plate).

That's everything you need to do to get bullet proof reactive UIs in your app, everything now takes care of itself, no matter what happens to the model or the rotation state of the device.

## <a name="somethingchanged-parameter"></a>Why not put a parameter in the Observer.somethingChanged() method?

If I had a dollar for everyone who asked me this question! (I would have, about $4). It is the obvious question for anyone not familiar with this style of reactive view implementation, indeed in the distant past the Observable class did have a generic on it which supported this behaviour. But it was finally removed when we realised that doing so *significantly* reduced the amount of view code that had to be written.

### Views want different things from the same model
Usually, view layer components are going to want different things from the same model. Take an example **AccountModel**, most views are going to want to know if the account is logged in or not, a settings page might want to display the last time the user logged in, an account page might want to know the status of the account such as ACTIVE, DORMANT, BANNED or whatever. Maybe a view will want to show all those things, or just two of them.

Regardless, our example model will be managing these three pieces of state:

``` kotlin
fun hasSessionToken(): Boolean
fun getLastLoggedInTimeStampMs(): Long
fun getAccountStatus(): Account.Status
```

All those states may change (potentially as a result of a network request completing in the background, or a notification arriving on the device etc). And if they change, the views need to update themselves immediately without us needing to do it (that's the whole point of reactive UIs after all!).

If we make all these things individually observable, we might choose something like RxJava observables, or LiveData and the views will need to observe each piece of state individually. Taking LiveData as an example, the view layer will have to contain something like this:


``` kotlin
accountModel.sessionTokenLiveData.observer(this, Observer { hasToken ->
  //update the view based on the hasToken Boolean
})

accountModel.lastLoggedInTimeStampLiveData.observer(this, Observer { timeStamp ->
  //update the view based on the timeStamp Long
})

accountModel.accountStateLiveData.observer(this, Observer { status ->
  //update the view based on the status class
})
```

We already learnt about how updating views in this way introduces very [hard to spot bugs](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k). But for the moment let's focus on the view layer boiler plate that needs to be written. If you've worked with MVVM and LiveData, you probably recognise this as fairly typical boiler plate. The same would be the case if we had a parameter in the somethingChanged() method. None of the observables can be reused because they all have different parameter requirements, so they all have to be specified invidivually.

(We can improve this situation by using LiveData to observe a single immutable state class which contains all the states - but you have to enforce that yourself, it doesn't come automatically as a result of the framework design). It also won't help if a view is observing more than one model...


### Views want things from more than one model
Any non-trivial reactive UI is going to be interested in data from more than one source (all of which could change with no direct user input and need to be immediately reflected on the UI). It's easy to imagine a view that shows the number of unread emails, the user's current account status, and a little weather icon in a corner. Something like MVVM or MVP would have you write a Presenter or a ViewModel that would aggregate that data for you, but as we discovered: 1) it's often [not necessary](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) and 2) the problem is still there, it just got moved to the Presenter or the ViewModel.

Each model or repo class is going to have different types of state available to observe, so the view layer is going to need to manage even more observer implementations, we'll stick with LiveData examples for brevity but the same issue presents itself with an API like RxJava's:


``` kotlin
emailInbox.unreadCountLiveData.observer(this, Observer { unread ->
  //update the view based on the unread Int
})

accountModel.sessionTokenLiveData.observer(this, Observer { hasToken ->
  //update the view based on the hasToken Boolean
})

accountModel.lastLoggedInTimeStampLiveData.observer(this, Observer { timeStamp ->
  //update the view based on the timeStamp Long
})

accountModel.accountStateLiveData.observer(this, Observer { status ->
  //update the view based on the status enum
})

weatherModel.weatherForecastLiveData.observer(this, Observer { forecast ->
  //update the view based on the forecast String
})

weatherModel.temperatureLiveData.observer(this, Observer { temperature ->
  //update the view based on the temperature int
})

weatherModel.windSpeedLiveData.observer(this, Observer { windSpeed ->
  //update the view based on the windSpeed int
})

```

Doing away with a parameter in somethingChanged() is the key innovation in **fore** that enables **any view to observe any model** or multiple models, with almost no boiler plate. It's also what powers the robustness you get from using [syncView()](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview), and it's what lets us write:

``` kotlin
//single observer reference
private var observer = Observer { syncView() }


fun syncView() {
    homepage_unreademails.text = "${emailInbox.getUnreadCount()}"
    homepage_loggedin.text = if (accountModel.hasSessionToken()) "IN" else "OUT"
    homepage_lastloggedin.text = LAST_LOGGED_IN_FORMATTER.format(accountModel.getLastLoggedInTimeStampMs())
    homepage_accountstatus.text = accountModel.getStatus().name
    homepage_weatherforecast.text = weatherModel.getForecast()
    homepage_temperature.text = "${weatherModel.getTemperature()}"
    homepage_windspeed.text = "${weatherModel.getWindSpeed()}"
}


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

```

With the Observble API cut down to that extent, we can actually take it further and remove even more boiler plate with the self-syncing [SyncXXX](https://erdo.github.io/android-fore/01-views.html#removing-even-more-boiler-plate) classes:

``` kotlin

override fun getThingsToObserve(): LifecycleSyncer.Observables {
  return LifecycleSyncer.Observables(
      emailInbox,
      accountModel,
      weatherModel
  )
}

fun syncView() {
    homepage_unreademails.text = "${emailInbox.getUnreadCount()}"
    homepage_loggedin.text = if (accountModel.hasSessionToken()) "IN" else "OUT"
    homepage_lastloggedin.text = LAST_LOGGED_IN_FORMATTER.format(accountModel.getLastLoggedInTimeStampMs())
    homepage_accountstatus.text = accountModel.getStatus().name
    homepage_weatherforecast.text = weatherModel.getForecast()
    homepage_temperature.text = "${weatherModel.getTemperature()}"
    homepage_windspeed.text = "${weatherModel.getWindSpeed()}"
}

```

[This section](https://dev.to/erdo/tutorial-android-fore-basics-1155#now-for-the-really-cool-stuff) of the dev.to tutorial on fore basics is worth a read, but the upshot is that adding a parameter to the somethingChanged() function would balloon the amount of code that gets written in the view layer.

It would also lead developers down the wrong path regarding how to move data about in the UI layer whilst ensuring consistency when the application is rotated etc. It sounds a little strange, but part of the benefit of using **fore** in a team is that it automatically discourages developers from making that mistake. It's almost like an automatic, invisible code review. Not having the ability to send data via the somethingChanged() function is one of the key reasons that fore UI code tends to be so compact compared with other architectures.


> "adding a parameter to the somethingChanged() function would balloon the amount of code that gets written in the view layer"


Try to get comfortable using these observers to just notify observing view code of any (unspecified) changes to the model's state (once the observing view code has been told there are changes, it will call fast returning getters on the model to find out what actually happened, redraw it's state, or whatever - if this isn't straight forward then the models you have implemented probably need to be refactored slightly, check the [observer vs callback](https://erdo.github.io/android-fore/05-extras.html#observer-listener) discussion first).

For some, this is a strange way to develop, but once you've done it a few times and you understand it, the resulting code is rock solid and very compact.
