
# Models
There are lots of definitions of the word Model. We will follow this description: [domain model](https://en.wikipedia.org/wiki/Domain_model).

In practice, model classes in MVO will mostly contain data and/or logic, they will very likely pass off tasks like network or database access to sub layers, making good use of composition. They typically employ dependency injection techniques to do that, preferably via their constructors. In MVO, the state is located in these models and accessible via quick returning getter methods or properties (usually called by thin views), it's the state of these models that is the main focus for tests.

You'll notice that keeping state in models is not a particularly functional pattern, and you'd be right (also neither is dependency injection for what it's worth). But *not* writing your UI code in a functional style, actually provides some stunning wins when it comes to [**Reactive UIs**](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom). MVO says nothing about the code behind the getter methods of the models of course, it can be as functional as you like.

An important thing about these models is that none of the code should know anything about [View](https://erdo.github.io/android-fore/01-views.html#shoom) layer classes. The models are concerned with their data, their logic and their state, and that is all. They don't know or care what interrogates their state via their getter methods - and this makes our Models extremely easy to test.

> "The models are concerned with their data, their logic and their state, and that is all"

In the sample apps, the models are all found in the **feature** package.

Here's the code for a very simple model which represents a user's [Wallet](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/feature/wallet/Wallet.kt)


## Writing a Basic Model

If you write a good model, using it in the rest of your app should be a piece of cake.

You'll see that in all the sample apps, the models have been written with the assumption that all the methods are being accessed on a single thread (which for a live app would be the UI thread). Not having to worry about thread safety here is a *very* big win in terms of code complexity. The models can use threads and coroutines internally of course.

If you need to hop onto another thread for IO or any heavy processing, do it explicitly with something like an [**AsyncBuilder**](https://erdo.github.io/android-fore/04-more-fore.html#asyncbuilder) or launch a [**coroutine**](https://erdo.github.io/android-fore/04-more-fore.html#kotlin-coroutines) for example, and then pop back on to the UI thread when you are ready to update your state.

> pop back on to the UI thread when you are ready to update your state

Check out a [[few]](https://github.com/erdo/android-fore/blob/master/example-jv-04retrofit/src/main/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcher.java) [[examples]](https://github.com/erdo/android-fore/blob/master/example-kt-02coroutine/src/main/java/foo/bar/example/forecoroutine/feature/counter/Counter.kt) from the sample apps, or if you're already comfortable writing model code _(most of this advice applies to writing ViewModels too, so this is all fairly obvious if you are coming from MVVM)_, feel free to skim over the checklist below for a refresher and you should be good to go.

## Model Checklist

For reference here's a check list of recommendations for the model classes, as used in **fore**. Once you've had a go at writing one you can come back here to double check you have everything down:

- The model classes should **know nothing about android lifecycle methods**
- **Avoid referencing Contexts** from your model if you can, although sometimes the design of Android makes this [awkward](https://erdo.github.io/android-fore/05-extras.html#androids-original-mistake)
- **Prefer referencing Application over Context or Activity** if you have a choice, as that reduces the chance of a memory leak
- The model **shouldn't know anything about View classes**, Fragments or specific Activities.
- In fact **the less the models knows about Android the better**
- Any **callback/listener** references passed to the model via methods need to be used and then cleared as quickly as possible within the model itself (callbacks may contain references to contexts and leak memory so you don't want to keep them around).
- Where it's reasonable, prefer the [observer](https://erdo.github.io/android-fore/03-reactive-uis.html#fore-observables) pattern over callbacks / listeners
- In MVO, the model's current **state at any point in time is typically exposed by getters or properties**. These are used by the View classes to ensure they are displaying the correct data, and by the test classes to ensure the model is calculating its state correctly.
- You'll **save yourself the need to write a lot of tricky thread safety code** by keeping access to your models on the UI thread (or test thread), user interaction like clicks will all come through on the UI thread anyway. Where you need asynchronous operation for db or network access etc, make it explicit and obvious, then return to the UI thread once you are done.
- The **getters must return quickly**. Don't do any complicated processing here, just return data that the model should already have. i.e. front load the processing and do work in setter type methods if necessary, not the getters
- When any data in your model changes, inside the model code call **notifyObservers()** after the state has changed.
- The models should make good use of [dependency injection](https://erdo.github.io/android-fore/05-extras.html#dependency-injection-basics) (via constructor arguments ideally). Any dependency that is not being injected will be difficult to mock for a test.
- Written in this way, the models will already be testable but it's worth highlighting **testability** as a specific goal. The ability to thoroughly test model logic is a key part of reducing unecessary app bugs.
- Do check out [When should I use an Observer, when should I use a callback listener?](https://erdo.github.io/android-fore/05-extras.html#observer-listener) in the FAQs to double check you're making the right choice for your model.
- (For models written in Java specifically), it's very useful to immediately **crash in your model constructor if any caller tries to send you null objects**. Your constructor is your public interface and could be used by anyone. You can help other developers out by immediately crashing here rather than sometime later, when the cause might not be so obvious. In the sample Java apps, this is done with the **Affirm.notNull()** call.
