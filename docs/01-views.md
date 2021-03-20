
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

- the [syncView()](https://erdo.github.io/android-fore/01-views.html#syncview) function which sets an affirmative state on each of the view components, in line with what the models indicate (or proxys this to an [adapter](https://erdo.github.io/android-fore/04-more-fore.html#adapter-animations)).
- the add / remove observers methods where the view registers with the models it is interested in.

A few [examples](https://erdo.github.io/android-fore/01-views.html#view-examples) are listed at the bottom of this page

## SyncView()

MVO uses one of the most simple (but extremely reliable) reactive implementations you can have. It really all boils down to a single **syncView()** method *(the concept is similar to MVI's render() method - compare MVO with MVI [here](https://erdo.github.io/android-fore/00-architecture.html#comparison-with-mvi))*. On the surface it looks very simple, but there are some important details to discuss that can trip you up, or otherwise result in a less than optimal implementation of this method. The basic philosophy is: If a model being observed changes **in any way**, then the **entire** view is refreshed.

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

When writing syncView() functions, you will often come across situations where you want to set a visibility to VISIBLE / INVISIBLE or VISIBLE / GONE based on a boolean state of a model. This is a very short line to write in java, slightly less so in kotlin (as we don't have the elvis operator for ternerary operations). So for kotlin the cleanest way of writing these lines is with one of two extension functions that fore provides. So if you prefer, you can write the following:


<pre class="codesample"><code>
fun syncView() {
    homepage_busy_progbar.showOrGone(authentication.isBusy)
    homepage_memberstatus_img.showOrInvisible(user.isRegistered)
}

</code></pre>

### Don't count notifications
Be careful not to rely on syncView() being called a certain number of times, as it results in fragile code. You can't predict when it will be called, and your syncView() code needs to be prepared for that. Make sure you understand [this](https://erdo.github.io/android-fore/05-extras.html#notification-counting) and you'll be writing solid syncView() implementations that will survive code refactors. Check out [SyncTrigger](https://erdo.github.io/android-fore/01-views.html#synctrigger)  below it case it fits your situation.

### Beware infinite loops
One final point to mention is about syncing your view directly from UI element "changed" listeners. It's generally fine to do that, and you should be able to call syncView() whenever you like, after all.

However, you will usually be setting a state on that UI element during your syncView(), if that UI element then calls its "changed" listener, you will end up calling syncView() again and find yourself in an infinite loop.

Of course, if you're setting a state on a UI element which is the same as the state it already had, it shouldn't be firing its "changed" listeners anyway. But Android. And indeed Android's EditText calls afterTextChanged() even when the text is identical to what it had before. Thankfully it's not a very common issue and the [work around](https://github.com/erdo/android-architecture/blob/todo-mvo/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/ui/widget/CustomEditText.java) is easy. (Interesting that the equivalent TextInput component of ReactNative doesn't suffer from this "feature").


## SyncTrigger

The [SyncTrigger](https://github.com/erdo/android-fore/blob/master/fore-core-kt/src/main/java/co/early/fore/kt/core/ui/SyncTrigger.kt) class lets you create a one off event (like an animation that must be fired only once) from inside the syncView() method (which is called at any time, [an arbitrary number of times](https://erdo.github.io/android-fore/05-extras.html#notification-counting)).

All "statey" view architectures have this issue (MVO, MVI, MVVM) whereas it's not an issue with MVP because that is event based to start with. Essentially we need a way to bridge the two worlds of state and events. There are a load of ways to do this, take a look [here](https://www.reddit.com/r/androiddev/comments/g6kgfn/android_databinding_with_livedata_holds_old_values/foabqm0/), [here](https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150), [here](https://gist.github.com/JoseAlcerreca/e0bba240d9b3cffa258777f12e5c0ae9), [here](https://github.com/android/architecture-samples/blob/dev-todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/SingleLiveEvent.java), and [here](https://github.com/android/architecture-components-samples/issues/63#issuecomment-310422475) for example.

Anyway this is fore's solution, but there is no need to use it if you already have a preferred way of handling this situation.

When using a SyncTrigger you need to implement the **triggered()** method which will be run when the SyncTrigger is fired (e.g. to run an animation), and also implement the **checkThreshold()** method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON). If the threshold is breached i.e. checkThreshold() returns **true**, then triggered() will be called.

For this to work you will need to call **check()** on the SyncTrigger each time the syncView() method is called by your observers. Alternatively you can call **checkLazy()** which will cause the first check result after the SyncTrigger has been constructed to be ignored. This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger. (see the SyncTrigger source for more details about this).

By default, the SyncTrigger will be reset when checkThreshold() again returns **false**. Alternatively you can construct the SyncTrigger with ResetRule.IMMEDIATELY for an immediate reset.

Please see [here](https://github.com/erdo/fore-state-tutorial/blob/master/app/src/main/java/foo/bar/example/forelife/ui/GameOfLifeActivity.kt) for example usage of the SyncTrigger.


## View Examples

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from:

- manage their lifecycle
- route button clicks and other widget listeners to the right place (usually directly to a model class)
- correctly display the state of whatever models they are interested in.


Here are few examples:

- [Wallets View](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/ui/wallet/WalletsActivity.kt)

- [Counter View](https://github.com/erdo/android-fore/blob/master/example-jv-02threading/src/main/java/foo/bar/example/forethreading/ui/CounterActivity.java)
