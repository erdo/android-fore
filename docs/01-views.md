
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


## Code that belongs in the view layer

Pretty much all views in **fore** do the same few things when they are created:

- get a reference to all the view components like Buttons, TextViews etc.
- get a reference to all models that the view needs to observe (using some form of DI)
- set up all the click listeners, text changed listeners etc
- *(optionally) set up any adapters*
- *(optionally) set up any SyncTriggers for things like animations*

In addition to that there will be:

- the [syncView()](https://erdo.github.io/android-fore/01-views.html#syncview) function which sets an affirmative state on each of the view components, in line with what the models indicate.
- the add / remove observers methods where the view registers with the models it is interested in.

This leaves almost all everything else to be handled in other modules or layers in the form of plain, unit testable code. A few view [examples](https://erdo.github.io/android-fore/01-views.html#view-examples) are listed at the bottom of this page

## SyncView()

MVO uses one of the most simple (but extremely reliable) reactive implementations you can have. It really all boils down to a single **syncView()** method *(the concept is similar to MVI's render() method)*. On the surface it looks very simple, but there are some important details to discuss that can trip you up, or otherwise result in a less than optimal implementation of this method. The basic philosophy is: If a model being observed changes **in any way**, then the **entire** view is refreshed.

That simplicity is surprisingly powerful so we're going to go into further detail about why, after I've quoted myself so that you remember it...

> "If a model being observed changes **in any way**, then the **entire** view is refreshed."

That doesn't mean that you can't subdivide your views and only refresh one of the subviews if you want by the way - as long as both (sub)views have their own syncView() method and they are observing their respective models.


### I need convincing

I'm going to defer to the [dev.to spot the bug tutorial](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k) for this.


### Writing an effective syncView() method

*A lot of this advice also applies to writing MVI render() methods. MVO's reducer() function helps to maintain state consistency, but it won't matter if the render() method written in the view layer doesn't set an affirmative state for each UI element.*

As part of refreshing the entire view, the syncView() method must set an **affirmative state** for every view element property that you are interested in. What that means is that where there is an **if** there must always be an **else** for each property.

> "Where there is an if, there must always be an else"

It's not good enough to just set a button as **disabled** if a total is 0 or less. You must also set that button as **enabled** if the total is greater than 0. If you don't set an affirmative step for both the positive and negative scenarios, then you run the risk of a syncView() call not setting a state at all, which means that the result will be indeterministic (it will be whatever state it had previously). This is one of those sneaky edge case things that at first glance might look fine, but can reveal itself as a bug later.

So don't do this inside your syncView() function:


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

But you'll find that by **focusing on the UI component** first rather than the condition, you can get some extremely tight code like so:


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

### showOrGone and showOrInvisible

When writing syncView() functions, you will often come across situations where you want to set a visibility to VISIBLE / INVISIBLE or VISIBLE / GONE based on a boolean state of a model or viewState data class. This is a very short line to write in java, slightly less so in kotlin (as we don't have the elvis operator for ternerary operations). So for kotlin the fore offers one of two extension functions. So if you prefer, you can write the following:


<pre class="codesample"><code>
fun syncView() {
    homepage_busy.showOrGone(authentication.isBusy)
    homepage_memberstatus.showOrInvisible(user.isRegistered)
}

</code></pre>

_(You can now also use the extension functions from androidx such as **isVisible** and **isGone**, although they are unfortunately a bit less explicit than they could be. The trouble is visibilty has 3 states: VISIBLE, INVISIBLE, and GONE. The androidx extension functions only mention one state, the negative case is left for you to remember. For instance does isVisible=false mean INVISIBLE? nope, it means GONE 🤷)_

### Don't count notifications
Be careful not to rely on syncView() being called a certain number of times, as it results in fragile code. You can't predict when it will be called, and your syncView() code needs to be prepared for that. Make sure you understand [this](https://erdo.github.io/android-fore/05-extras.html#notification-counting) and you'll be writing solid syncView() implementations that will survive code refactors. Check out [SyncTrigger](https://erdo.github.io/android-fore/01-views.html#synctrigger)  below it case it fits your situation.

### Beware infinite loops
One final point to mention is about syncing your view directly from UI element "changed" listeners. It's generally fine to do that, and you should be able to call syncView() whenever you like, after all.

However, you will usually be setting a state on that UI element during your syncView(), if that UI element then calls its "changed" listener, you will end up calling syncView() again and find yourself in an infinite loop.

Of course, if you're setting a state on a UI element which is the same as the state it already had, it shouldn't be firing its "changed" listeners anyway. But Android. And indeed Android's EditText calls afterTextChanged() even when the text is identical to what it had before. Thankfully it's not a very common issue and the [work around](https://github.com/erdo/android-architecture/blob/todo-mvo/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/ui/widget/CustomEditText.java) is easy. (Interesting that the equivalent TextInput component of ReactNative doesn't suffer from this "feature").


## <a name="synctrigger"></a>Triggers

A Trigger is fore's way of bridging the **world of state** (which is what drives a UI in architectures like MVO) and the **world of events** (which tend to happen on changes of state). There is a presentation about State vs Events [here](https://erdo.github.io/android-fore/05-extras.html#presentations).

All "statey" view architectures have this issue (MVO, MVI, MVVM) and there are a load of ways to handle this, take a look [here](https://www.reddit.com/r/androiddev/comments/g6kgfn/android_databinding_with_livedata_holds_old_values/foabqm0/), [here](https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150), [here](https://gist.github.com/JoseAlcerreca/e0bba240d9b3cffa258777f12e5c0ae9), [here](https://github.com/android/architecture-samples/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SingleLiveEvent.java), and [here](https://github.com/android/architecture-components-samples/issues/63#issuecomment-310422475) for example.

Anyway this is fore's solution, but there is no need to use it if you already have a preferred way.

### TriggerWhen
kotlin source is [here](https://github.com/erdo/android-fore/tree/master/fore-kt-core/src/main/java/co/early/fore/kt/core/ui/trigger/TiggerWhen.kt)

A **TriggerWhen** fires a predefined action (an event), when a certain threshold is met (based on some state). For example, this trigger fires the "show toast warning event" when it detects that a user's ballance is low:

<pre class="codesample"><code>
ballanceWarnTrigger = TriggerWhen(
  triggeredWhen = { account.ballance < 5 },
  doThisWhenTriggered = { showToast("bank ballance is low!") }
)

</code></pre>

Which can be written as:

<pre class="codesample"><code>
ballanceWarnTrigger = TriggerWhen({ account.ballance < 5 }) {
  showToast("bank ballance is low!")
}

</code></pre>

The triggers are typically checked (see below) during the syncView() function which already gets called whenever the state changes. The reason these events are not continually fired each time syncView() is called is the **ResetRule** of the Trigger. By default this is set to ResetRule.ONLY_AFTER_REVERSION, which means in the case above, the trigger will not be ready to fire again until the account balance is back to 5 or above. Other values are ResetRule.IMMEDIATELY and ResetRule.NEVER. Here's how you would apply a reset rule:

<pre class="codesample"><code>
ballanceWarnTrigger = TriggerWhen({ account.ballance < 5 }) {
  showToast("bank ballance is low!")
}.resetRule(ResetRule.IMMEDIATELY)

</code></pre>

### TriggerOnChange
kotlin source is [here](https://github.com/erdo/android-fore/tree/master/fore-kt-core/src/main/java/co/early/fore/kt/core/ui/trigger/TriggerOnChange.kt)

A **TriggerOnChange** fires a predefined action (an event), when a certain state changes. For example, this trigger fires the "animate event" when it detects that the pollenLevel has changed:

<pre class="codesample"><code>
fadePollenTrigger = TriggerOnChange(
  currentState = { viewModel.viewState.weather.pollenLevel },
  doThisWhenTriggered = { animations.animatePollenChange() }
)

</code></pre>

Which can be written as:

<pre class="codesample"><code>
fadePollenTrigger = TriggerOnChange({ viewModel.viewState.weather.pollenLevel }) {
  animations.animatePollenChange()
}

</code></pre>

This trigger has no ResetRule, each time it is checked it will verify the latest state by running the currentState() function, if the state is not equal to the state it had previously, the trigger will fire.

You can acces the previous and current state if required, for example:

<pre class="codesample"><code>
fadePollenTrigger = TriggerOnChange({ viewModel.viewState.weather.pollenLevel }) { state ->
  animations.animatePollenChange(from = state.pre, to = state.now)
}

</code></pre>

### Careful with scope functions
Don't be tempted to do something like this by the way. Here we are using **apply** but the pollenLevel changes won't be visible to the Trigger, the pollenLevel will be stuck at whatever it was the first time this is run.

<pre class="codesample"><code>
viewModel.viewState.weather.apply {
  fadePollenTrigger = TriggerOnChange({ pollenLevel }) {
    animations.animatePollenChange()
  }
}

</code></pre>

### check() vs checkLazy()
Triggers tend to live in the UI layer of the app, and can therefore be destroyed and recreated on device rotation. If we take this error trigger as an example:

<pre class="codesample"><code>
showErrorTrigger = TriggerWhen({ viewModel.viewState.error != null }) {
  showToast("viewModel.viewState.error")
}

</code></pre>
When the error state is non null, syncView() is run and the trigger fires, showing the error toast. If we then rotate the device, a new trigger is constructed, syncView() is run and another error toast will be shown (the domain error state is still non null).

<pre class="codesample"><code>
fun syncView() {

  ...

  showErrorTrigger.check()
}

</code></pre>

If this is not what you want (usually it isn't), we can use checkLazy() instead of check(). checkLazy() swallows the first trigger event if it occours the first time a check is run after construction - so you won't get a fresh error toast displaying each time you rotate the device.

<pre class="codesample"><code>
fun syncView() {

  ...

  showErrorTrigger.checkLazy()
}

</code></pre>

Please see [here](https://github.com/erdo/fore-state-tutorial/blob/master/app/src/main/java/foo/bar/example/forelife/ui/GameOfLifeActivity.kt) and [here](https://github.com/erdo/clean-modules-sample/blob/main/app/ui/src/main/java/foo/bar/clean/ui/dashboard/DashboardActivity.kt) for some example usages of Triggers.

As with most fore components, the robustness comes from the fact the public functions are being called on the UI thread (which is where you will usualy already be if you are in the view layer). The exception to this is during a test where the main thread becomes the test thread.

## View Examples

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from:

- manage their lifecycle
- route button clicks and other widget listeners to the right place (usually directly to a model class)
- correctly display the state of whatever models they are interested in.


Here are few examples:

- [Wallets View](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/ui/wallet/WalletsActivity.kt)

- [Counter View](https://github.com/erdo/android-fore/blob/master/example-jv-02threading/src/main/java/foo/bar/example/forethreading/ui/CounterActivity.java)
