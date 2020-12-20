

# MVO Architecture

This little library helps you implement an architecture we call **MVO (Model View Observer)**. (If you know your MV*s then you'll notice MVO has some similarity with both MVI and MVVM).

![mvo anotated](img/arch_mvo_anotated.png)

That block diagram above is what MVO looks like (it's simplified of course, further details below).

By [**Model**](https://erdo.github.io/android-fore/02-models.html#shoom) we mean the standard definition of a software model, there are no particular restrictions we will put on this model other than it needs to be somehow observable (when it changes, it needs to tell everyone observing it, that it changed) and it needs to expose its state via quick returning getter methods. The model can have application level scope, or it can be a View-Model - it makes no difference from an MVO perspective. (But mostly with MVO we are talking about application level things like AccountModel, MessageInboxRepository, TodoListManager, Favourites etc).

By [**Observer**](https://en.wikipedia.org/wiki/Observer_pattern) we mean the standard definition of the Observable pattern. In MVO, the Views observe the Models for any changes. (This has nothing to do with Rx by the way, though you could implement an MVO architecture using Rx if you wanted to).

By [**View**](https://erdo.github.io/android-fore/01-views.html#shoom) we mean the thinest possible UI layer that holds buttons, text fields, list adapters etc and whose main job is to observe one or more observable models and sync its UI with whatever state the models hold. If you're going to implement MVO on android you might choose to use an Activity or Fragment class for this purpose, or a custom View class.

We mentioned **State**. The **fore** philosophy is to take state away from the UI layer, leaving the UI layer as thin as possible. **fore** puts state in the models where it can be comprehensively unit tested. For example, if you want to display a game's score, the place to manage that state is in a GameModel. The view simply represents whatever state the GameModel has, and is synchronised everytime the (observable) GameModel changes:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>
<pre class="tabcontent tabbed java"><code>
public void syncView() {
  pointsView.text = gameModel.getScore();
}

</code></pre>
<pre class="tabcontent tabbed kotlin"><code>
fun syncView() {
  pointsView.text = gameModel.score
}

</code></pre>

Notice the syncView() method does not take a parameter. It gets all it needs from the models that the view is observing. This style of view state binding is deceptively simple, and is _one_ of the reasons that fore is so tiny and the library so easy to understand.

If you like using an immutable ViewState to drive your UI, the best place to put that for MVO would be within a ViewModel class, which you would access from inside your syncView function (e.g. myAccountViewModel.getViewState()).

*For the avoidance of doubt, most non-trivial apps will of course have more layers behind the model layer, typically you'll have some kind of repository, a networking abstraction etc. You can use UseCases as normal, although for reasons we'll get to, they tend to be of the fire-and-forget variety. There are two slightly larger, more commercial style app examples to check out: one in [Kotlin](https://github.com/erdo/fore-full-example-02-kotlin) and another in [Java](https://github.com/erdo/android-architecture) (which has a [tutorial](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) to go along with it).*

In a nutshell this is what we have with MVO:

> "Observable **Models**; **Views** doing the observing; and some **Reactive UI** tricks to tie it all together"

Here's an example app structure showing some models being observed by fragments in typical MVO style. As discussed below, the dependency arrows point towards the models (i.e. the view layer is aware of the models, the models are not aware of the view layer).

<a name="bad-diagram"></a>

![data binding](img/app_arch_0.png)

A lot of the time, things happen in an app that do not originate directly from user interaction: incoming notifications, network connectivity changes, bluetooth LE connections etc. Often, we have to propagate those events to the view layer somehow, often by locating the current foreground activity / fragment (using ActivityLifecycleCallbacks for instance) and then pushing the information to the view component directly: this is the <strong>opposite</strong> of a reactive UI.

MVO's observable models provide an easy and much less boiler-plate intensive solution: when the models' state changes, they notify their observers (they don't need to involve themselves in any view layer considerations at all).

It's the view layer components job to observe whichever models they are interested in, and synchronize their UI when those models change.

This flow is so lightweight and easy to implement, it's actually how MVO handles <strong>all</strong> state changes, including those that are a result of network responses, or actions triggered directly by the user. For this reason it's common to see Actions or UseCases that are fire-and-forget from the view layer into the app layer - the return path comes via the observable model state changes.

<a name="bad-diagram-w-usecase"></a>

![data binding](img/app_arch_1_usecase.png)

If you decide your code would be improved by the addition of a ViewModel, here is where that would fit in our example app:

<a name="bad-diagram-w-viewmodel"></a>

![data binding](img/app_arch_2_viewmodel.png)

The Mobile Wallet Model / Wallet Fragment section of that diagram matches what is happening in [**sample app 1**](https://erdo.github.io/android-fore/#fore-1-reactive-ui-example). Here are the relevant bits of code: the [**observable model code**](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/feature/wallet/Wallet.kt) and the [**view code**](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/ui/wallet/WalletsActivity.kt) that does the observing.

One great thing about MVO is that the view layer and the rest of the app are so loosely coupled, that supporting rotation already works out of the box. In the code examples above, the code just works if you rotate the screen simply because of how it's structured - you don't need to do a single thing.

> "the code works if you rotate the screen - without you needing to do a single thing"

The code looks extremely simple and it is, but surprisingly the technique works the same if you're using [**adapters**](https://github.com/erdo/android-fore/blob/master/example-jv-03adapters/src/main/java/foo/bar/example/foreadapters/ui/playlist/PlaylistsActivity.java) [\[screen shot\]](https://raw.githubusercontent.com/erdo/android-fore/master/example-jv-03adapters/screenshot.png), or if you're doing [**asynchronous work in your model**](https://github.com/erdo/android-fore/blob/master/example-jv-02threading/src/main/java/foo/bar/example/forethreading/ui/CounterActivity.java), or fetching data [**from a network**](https://github.com/erdo/android-fore/blob/master/example-kt-04retrofit/src/main/java/foo/bar/example/foreretrofitkt/ui/fruit/FruitActivity.kt). It even works when you have a heavily animated view like we do in [**sample app 5**](https://erdo.github.io/android-fore/#fore-5-ui-helpers-example-tic-tac-toe).


## Handling State
In MVO, the state is kept inside in the models, typically accessible via getter methods or properties. You'll notice that's not particularly functional in style, but it's one of the reasons that MVO has such shockingly low boiler plate compared with other ui data-binding techniques. And this shouldn't worry you by the way (dependency injection is not a functional pattern either - as developers we simply always look for the best tool for the job). Whatever drives the state of your models and the rest of your app can be as functional as you want of course, MVO just tends to keep the functional code out of ephemeral view layers. This means that you can have a Redux style reducer, and immutable state for your models internally  - as long as that state is accessed by the view layer using getters / properties, you'll still be able to take full advantage of MVO architecture.

There is further discussion of state versus events [**here**](https://erdo.github.io/android-fore/05-extras.html#state-versus-events)

<a name="comparisons-with-mv"></a>
## From MV* to MVO

MVO is like a radically reduced version of MVVM, with the addition of a render() style function similar to the one you will find in MVI.

Discussions of **MVC**, **MVP**, **MVVM** and **MVI** can get quite abstract, and specific implementations often differ considerably. (If you want to continue learning about **MVO**, you might want to head over to the discussion on [**Views**](https://erdo.github.io/android-fore/01-views.html#shoom) at this point).

Anyway for the purposes of our MV* discussion, the following flow diagrams will do:

![simple basket](img/arch_mvc.png)

This is quite a common representation of **MVC**, however I don't think it's a particularly useful diagram - it depends entirely on the specifics of your controller which often isn't mentioned at all. If you are considering your Android Activity class to be the controller, then implementing something like this on Android can get a little messy. *(In the decade or so I've been a contract Android developer, I've learnt the hard way that it's usually best to remove as much code from activity/fragment classes as possible)*. If you are considering your controllers to be your click listeners then it's basically a nothing diagram that shows a View interacting with a Model. (See below for a discussion of [Controllers](#btw-whats-a-controller)).

There is one important thing to note about about this diagram however. If we focus on the Model, all the arrows (dependencies) point towards the Model. This tells us that while the View and Controller know about each other and the Model, the Model knows nothing about the View or the Controller. That's exactly the way we want it. This way a Model can be tested independently, and needs to know nothing about the view layer. It can support any number of different Views which can come and go as they please (when an Android device is rotated for example, and the Model is not affected - or even aware of it).

_(It's worth mentioning that many early Android apps had no discernible domain model at all, some still don't, essentially writing the entire app in the UI layer - so if you can't find it in the app you are working on, it might not exist)._

Anyway I did say that I thought the typical MVC diagram is not particularly useful, I think its main purpose is just to be shown before the MVP diagram is - so that we can see a particular difference. So here is a typical MVP diagram:

![simple basket](img/arch_mvp.png)

It's basically the same thing except here the View doesn't know about the Model. All interactions with the Model go via a Presenter class. The Presenter class usually does two main things: it sets UI states on the View (so it needs to know about the View) and it forwards commands from click listeners and the like, to the underlying Model / Models (so it needs to know about those Models too).

In a typical MVP Android app, quite a bit of boiler plate is required to let the Presenter do its job, typical implementations also create the Presenter from scratch each time the view is constructed, and that can make handling rotations difficult.

Note that as with MVC, the Model is not aware of the higher level View related classes - which is a good thing. Moving code from the View to a Presenter class also means that we can now unit test it, which is great. (The Presenter is aware of the View but this is usually via an injected interface, so for a unit test you don't need to set up an actual View, just its interface)

The main issue with both of these approaches on Android though, is the arrow pointing to the View

![simple basket](img/arch_mvpx.png)

Android has a particular problem with this as the Views are destroyed and created even due to a simple screen rotation. Each time that happens all the references need to be recreated and if you don't do that correctly, it's a very easy way of getting a memory leak.

Here's the MVVM equivalent diagram:

![simple basket](img/arch_mvvm.png)

Again there are different ways of doing MVVM, even on Android, but the main difference here is that the View-Model is not aware of the View like the Presenter is. All the arrows go from the edge of the system where the UI is, towards the centre where things like business logic reside, down in the model layer.

In MVVM you typically have a View-Model for each View, so even though there are no dependencies on the View from the View-Model (no arrow pointing from View-Model to View), it's still a specific implementation for that View, it's a little more difficult to use one View-Model for different Views. A slightly more realistic situation for a whole app with different views looks like this:

![simple basket](img/arch_mvvm_reality.png)

By the way, you can make these Views reactive by making the [ViewModels observable](https://github.com/erdo/fore-full-example-02-kotlin/blob/master/app/src/main/java/foo/bar/example/fore/fullapp02/feature/basket/BasketModel.kt) using *fore* (we did mention fore can make anything observable). But if you are following Google's typical ViewModel implementation, you would use LiveData for this purpose. The lack of a [syncView](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview) convention (or render() in MVI), does result in increasingly complex view code once you start tackling non trivial UIs with lots of bits of state though - so even here, adding a *fore* Observable would be a quick win over using LiveData.

Importantly, all the arrows are pointing the right way! (which, no surprise, happens to match the direction of the arrows in [clean architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html))

### Finally MVO

As we mentioned, here is what MVO looks like in a real app:

![simple basket](img/arch_mvo.png)

Well how does that work? you can't just remove boxes and call it better! (I hear you say).


> "Observable **Models**; **Views** doing the observing; and some **Reactive UI** tricks to tie it all together"


As with all the architectures discussed so far, here the Model knows nothing about the View. In MVO, when the view is destroyed and recreated, the view re-attaches itself to the model using the observer pattern. Any click listeners or method calls as a result of user interaction are sent directly to the relevant model (no benefit here in sending them via a Presenter). With this architecture you remove a lot of problems around lifecycle management and handling rotations, it also turns out that the code to implement this is a lot less verbose **(and it's also very testable and scalable)**.

Sometimes you really will want to scope a model to just a single activity (although you might be surprised at how rarely this is genuinely useful - look at how much code we removed using the MVO approach on the [android architecture blueprints](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) for example). Anyway, if you decide that's what your app needs at that moment, use a ViewModel (Google has a nice implementation) and make it observable using the *fore* Observable as you would with any other Model. If you are injecting your ViewModels in to the view layer and using fore observables to synchronize your UI, the view layer will not even be aware of what type of model it is anyway.

**There are a few important things in MVO that allow you an architecture this simple:**

* The first is a very robust but simple [**Observer API**](https://erdo.github.io/android-fore/03-reactive-uis.html#fore-observables) that lets views attach themselves to any model (or multiple models) they are interested in
* The second is the [**syncView()**](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview) convention
* The third is writing [**models**](https://erdo.github.io/android-fore/02-models.html#shoom) at an appropriate level of abstraction, something which comes with a little practice
* The fourth is making appropriate use of [**DI**](https://erdo.github.io/android-fore/05-extras.html#dependency-injection-basics)

 If you totally grok those 4 things, that's pretty much all you need to use MVO successfully, the [**code review guide**](https://erdo.github.io/android-fore/05-extras.html#troubleshooting--how-to-smash-code-reviews) should also come in handy as you get up to speed, or you bring your team up to speed.

### Comparison with MVI
 The two architectures are very similar in that they both have a single method that updates the UI according to state.

 **MVO has syncView()** which takes no parameters. The method sets the UI according to whatever models it has, eg:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
loggedInStatus.setText(accountModel.isLoggedIn() ? "IN" : "OUT")
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
loggedInStatus.text = if (accountModel.isLoggedIn) "IN" else "OUT"
 </code></pre>


 **MVI has render()** which takes a ViewState parameter containing all the required state for the UI, eg:


 <!-- Tabbed code sample -->
  <div class="tab">
    <button class="tablinks java" onclick="openLanguage('java')">Java</button>
    <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
  </div>

 <pre class="tabcontent tabbed java"><code>
loggedInStatus.setText(viewState.isLoggedIn ? "IN" : "OUT")
 </code></pre>

 <pre class="tabcontent tabbed kotlin"><code>
loggedInStatus.text = if (viewState.isLoggedIn) "IN" else "OUT"
 </code></pre>


 Most testing takes place just below the UI layer for both architectures:

 ![testing with MVI and MVO](img/test_mvo_mvi.png)

 In **MVI** a typical test would be to make sure that an **Intention/Intent** made by a user results in the correct **ViewState** being returned to the UI layer. For example, test that the LOGIN_INTENTION is processed correctly (i.e. gets converted to a LOGIN_ACTION, is processed via an interactor to create a LOGIN_RESULT, which is then *reduced* and combined with previous view states to produce a ViewState object (including a field like ViewState.isLoggedIn = true), for passing back to the UI). The reason for the complication with MVI is that the whole thing is functionally written so that the resulting ViewState returned via the render() method is *immutable*. Luckily the tests don't need to know much about this and mostly just compare the INTENTION with an expected STATE.

 **MVO** simply tests that when you call accountModel.login(): **a)** if you are observing that model and it changes, you receive a notification, and **b)** the accountModel.isLoggedIn() method subsequently returns the expected value.

 *Both architectures mock out dependencies and have strategies for dealing with asynchronous code which makes the tests small.*

 There is a thin part of the app that can only be tested with the help of android itself (and is therefore sometimes skipped). For **MVI**: testing that when you click on the login button it actually emits a LOGIN_INTENTION for processing. For **MVO**: testing that when you click on the login button, it actually calls accountModel.login().

 On the return trip to the UI: For **MVI**: testing that when render() is called with the appropriate ViewState, the login text does actually read "Logged in". For **MVO**: testing that when syncView() is called with an appropriately mocked accountModel object, the login text does actually read "Logged in".

 *Both architectures support rotation on Android although it's not quite so trivial in MVI, mostly due to its functional/immutable nature.*

 It goes without saying that the amount of code that needs to be written to implement MVI is considerably more than with MVO (this is the price you pay for writing UI data-binding code in a functional style). The difference becomes more significant with views that depend on a number of different data sources, each of which may need reacting to (such as an AccountModel, EmailInbox and NetworkStatusRepository). Most of this additional code will be written in the Interactor class, so at least it remains testable - but sheer amount of (often complex RxJava) code can become a significant break on development speed and robustness, especially when code needs to be changed.


### BTW, What's a Controller
It helps to remember that MVC is at least 3 decades old, I think it was Microsoft who invented it [I saw a Microsoft white paper written about it once, but I can't find it anywhere now]. A controller means different things on different platforms.

Originally a controller might have been a class that accepts mouse clicks at specific pixel co-ordinate, did some collision detection to find out which UI component was clicked, then sent that information on to the appropriate UI classes for further processing. (A controller in a web app however, might be a main entry point URL that forwards on requests to different parts of the system.)

In modern app frameworks most of the controller work is implemented for you by the UI framework itself - these are the button click listeners that simply catch user input and send it on to the right place. As we need to worry less about controllers now a days, we talk more about more "modern" things like MVVM - which is only about **10(!)** years old.

(Android also lets you use Activities as kind of "Controllers" by letting you specify callback methods right in the XML for buttons which will end up getting called on whatever activity is hosting that particular view. The idea is to not have to write click listeners - unfortunately this encourages (forces) you to get the activity involved in something that it doesn't need to be involved in, which usually doesn't end well.)
