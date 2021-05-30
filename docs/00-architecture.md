

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

If you prefer using immutable state, just have your model expose that state via a property or a getter: (gameModel.currentState.score).

*For the avoidance of doubt, most non-trivial apps will of course have more layers behind the model layer, typically you'll have some kind of repository, a networking abstraction etc. There are two slightly larger, more commercial style app examples to check out, though they are starting to get a little out of date: one in [Kotlin](https://github.com/erdo/fore-full-example-02-kotlin) and another in [Java](https://github.com/erdo/android-architecture) (which has a [tutorial](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) to go along with it).*

In a nutshell this is what we have with MVO:

> "Observable **Models**; **Views** doing the observing; and some **Reactive UI** tricks to tie it all together"

Here's an example app structure showing some models being observed by fragments in typical MVO style. As discussed below, the dependency arrows point towards the models (i.e. the view layer is aware of the models, the models are not aware of the view layer).

<a name="bad-diagram"></a>

![data binding](img/app_arch_0.png)

A lot of the time, things happen in an app that do not originate directly from user interaction: incoming notifications, network connectivity changes, Bluetooth LE connections etc. We have to propagate those events to the view layer somehow. Sometimes that is done by locating the current foreground activity / fragment (using ActivityLifecycleCallbacks for example) and then pushing the information to the view layer directly: this is the <strong>opposite</strong> of a reactive UI. Implementing this with reactive-streams is doable but requires very careful handling of caching and memory references.

MVO's observable models provide an easy and much less boiler-plate intensive solution: when the models' state changes, they notify their observers (they don't need to involve themselves in any view layer considerations at all).

It's the view layer components job to observe whichever models they are interested in, and synchronize their UI when those models change.

This flow is so lightweight and easy to implement, it's actually how MVO handles <strong>all</strong> state changes, including those that are a result of network responses, or actions triggered directly by the user.

If your team is getting on well with MVVM, here is where the ViewModel would fit in our example app:

<a name="bad-diagram-w-viewmodel"></a>

![data binding](img/app_arch_2_viewmodel.png)

The Mobile Wallet Model / Wallet Fragment section of that diagram matches what is happening in [**sample app 1**](https://erdo.github.io/android-fore/#fore-1-reactive-ui-example). Here are the relevant bits of code: the [**observable model code**](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/feature/wallet/Wallet.kt) and the [**view code**](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/ui/wallet/WalletsActivity.kt) that does the observing. (That's the expressive style of doing things so that you can easily see what is happening, even that boilerplate is [optional](https://erdo.github.io/android-fore/03-reactive-uis.html#boiler-plate) though)

One great thing about MVO is that the view layer and the rest of the app are so loosely coupled, that supporting rotation already works out of the box. In the code examples above, the code just works if you rotate the screen simply because of how it's structured - you don't need to do a single thing.

> "the code works if you rotate the screen - without you needing to do a single thing"

The code looks extremely simple and it is, but surprisingly the technique works the same if you're using android adapters, or if you're doing asynchronous work in your model, or fetching data from a network.

## Handling State
In MVO, the state is kept inside the models, typically accessible via getter methods or properties. If you prefer exposing your model's state using an immutable data class for example, you can of course do that - but it must still be accessible via a property or a getter. That's how the clean architecture sample is written incidentally.

## Dependency arrows

As with all M* architectures, with MVO the Model knows nothing about the View. When the view is destroyed and recreated, the view re-attaches itself to the model using the observer pattern. Any click listeners or method calls as a result of user interaction are sent directly to the relevant model or an intemediary viewModel (from the UI thread - asynchronous code is managed in the models, not at the UI layer). With this architecture you remove a lot of problems around lifecycle management and handling rotations, it also turns out that the code to implement this is a lot less verbose **(and it's also very testable and scalable)**.

Sometimes you really will want to scope a model to just a single activity (although you might be surprised at how rarely this is genuinely useful - look at how much code we removed using the MVO approach on the [android architecture blueprints](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) for example). Anyway, if you decide that's what your app needs at that moment, use a ViewModel and make it observable using the *fore* Observable as you would with any other Model. If you are injecting your ViewModels in to the view layer and using fore observables to synchronize your UI, the view layer will not even be aware of what type of model it is anyway.

**There are a few important things in MVO that allow you an architecture this simple:**

* The first is a very robust but simple [**Observer API**](https://erdo.github.io/android-fore/03-reactive-uis.html#somethingchanged-parameter) that lets views attach themselves to any model (or multiple models) they are interested in
* The second is the [**syncView()**](https://erdo.github.io/android-fore/01-views.html#syncview) convention
* The third is writing [**models**](https://erdo.github.io/android-fore/02-models.html#shoom) at an appropriate level of abstraction, something which comes with a little practice
* The fourth is making appropriate use of [**DI**](https://erdo.github.io/android-fore/05-extras.html#dependency-injection-basics)

 If you totally grok those 4 things, that's pretty much all you need to use MVO successfully, the [**code review guide**](https://erdo.github.io/android-fore/05-extras.html#troubleshooting--how-to-smash-code-reviews) should also come in handy as you get up to speed, or you bring your team up to speed.
