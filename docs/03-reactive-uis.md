# Reactive UIs

Data Binding is the old term for this, and its basic definition is: any changes of state that happen in your underlying model, get automatically represented in your view.

> "Any changes of state in your underlying model, get automatically represented in your view."

So if your shopping basket model is empty: the checkout button on your view needs to be invisible or disabled. And as soon as your shopping basket model has something in it, your checkout button needs to reflect that by being enabled. This concept is decades old, and in UI frameworks is generally implemented with some form of Observer pattern.

Lately it's been applied to other (non UI) areas of code very successfully under the name of *reactive* programming. Back at the UI layer, you could say that the view is *reacting* to changes in the model.


## SyncView()

MVO uses one of the most simple (but extremely reliable) data binding implementations you can have. It really all boils down to a single **syncView()** method *(the concept is similar to MVI's render() method - compare MVO with MVI [here](https://erdo.github.io/android-fore/00-architecture.html#comparison-with-mvi))*. On the surface it looks very simple, but there are some important details to discuss that can trip you up, or otherwise result in a less than optimal implementation of this method. The basic philosophy is: If a model being observed changes **in any way**, then the **entire** view is refreshed.

That simplicity is surprisingly powerful so we're going to go into further detail about why, after I've quoted myself so that you remember it...

> "If a model being observed changes **in any way**, then the **entire** view is refreshed."

That doesn't mean that you can't subdivide your views and only refresh one of the subviews if you want by the way - as long as both (sub)views have their own syncView() method and they are observing their respective models.


### Quick Tutorial

I'm going to refer to the [dev.to spot the bug tutorial](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k) for this.


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

Of course, if you're setting a state on a UI element which is the same as the state it already had, it shouldn't be firing it's "changed" listeners anyway. But Android. And indeed EditText calls afterTextChanged() even when the text is identical to what it had before. Thankfully it's not a very common issue and the [work around](https://github.com/erdo/android-architecture/blob/todo-mvo/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/ui/widget/CustomEditText.java) is easy.

## **fore** Observables
In MVO, the models are usually Observable, and the Views are mostly doing the Observing.

Most of the models in the sample apps become observable by extending ObservableImp (you can also implement the Observable interface and proxy the methods through to an ObservableImp instance), the [code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/observer/ObservableImp.java) is pretty light weight and you can probably work out what it's doing. By extending ObservableImp, the models gain the following characteristics:

- Any observers (usually views) can add() themselves to the model so that the **observer will be told of any changes in the model's state**
- When the model's state changes, each added observer will be told in turn by having its **somethingChanged()** method called (which in turn typically causes a call to **syncView()**)
- For this to work, all a model must do is call **notifyObservers()** whenever its own state changes (see the [Model](https://erdo.github.io/android-fore/02-models.html#shoom) section)
- When the model is constructed in **ASYNCHRONOUS** mode, these notifications will always be delivered on the UI thread so that view code need not do anything special to update the UI
- To avoid memory leaks, **views are responsible for removing their observable callback** from the observable model once they are no longer interested in receiving notifications
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
Observer observer = new Observer() {
    public void somethingChanged() {
        syncView();
    }
};
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
var observer: Observer = object : Observer() {
    fun somethingChanged() {
        syncView()
    }
}
 </code></pre>


Or a bit tighter with Java 8 or Kotlin, the rather lovely:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java 8</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
Observer observer = this::syncView;
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
internal var observer = this::syncView
 </code></pre>


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

That's everything you need to do to get bullet proof data binding in your app, everything now takes care of itself, no matter what happens to the model or the rotation state of the device.

## <a name="somethingchanged-parameter"></a>Why not put a parameter in the Observer.somethingChanged() method?

If I had a dollar for everyone who asked me this question! (I would have, about $4). There are a couple of good (but subtle) reasons that we don't have a parameter here though.

Adding a parameter would let client code use the observer like some kind of messenger thing or an event bus. That could be a perfectly valid thing to do for the specific situation you find yourself in, and sending data like that might at first seem like an easy and convenient thing to do here as well.

When it comes to binding data to an android view layer however, doing so instantly couples a particular model to a particular view.

Often with **fore**, different views will want different things from the same model and as the code evolves, that model slowly ends up having to support many different flavoured observables all with different parameter requirements.

Similarly there will often be views that are interested in more than one model, and if those models all have different observable interfaces, each of those interfaces will need to be implemented and managed by the view, rather than just using a single Observer implementation.

It balloons the amount of code that needs to be written. It also leads developers down the wrong path regarding data binding and ensuring consistency when your application is rotated etc as discussed above.

_(The fact that the observable interface is the same for all models is also what enables fore to handle the adding and removing of observers automatically for us in the Sync... classes.)_

Not having the ability to add a parameter here is one of the key reasons that fore UI code tends to be so compact compared with other architectures.

This is one case where **fore** is stopping you from making an easy but horrible architectural mistake. The library is as valuable for what you can't do with it, as it is for what you can do with it.


> "This library is as valuable for what you **can't** do with it, as it is for what you **can** do with it."


Try to get comfortable using these observers to just notify observing view code of any (unspecified) changes to the model's state (once the observing view code has been told there are changes, it will call fast returning getters on the model to find out what actually happened, redraw it's state, or whatever - if this isn't straight forward then the models you have implemented probably need to be refactored slightly, check the [observer vs callback](https://erdo.github.io/android-fore/05-extras.html#observer-listener) discussion first).

For some, this is a strange way to develop, but once you've done it a few times and you understand it, the resulting code is rock solid and very compact.
