<a name="fore-network"></a>
# Retrofit2 and Apollo

**fore** handles both Retrofit2 and Apollo networking calls in a similar way, by wrapping them with a **CallProcessor** class ([CallProcessorRetrofit2](https://github.com/erdo/android-fore/blob/master/fore-network-kt/src/main/java/co/early/fore/kt/net/retrofit2/CallProcessorRetrofit2.kt) \| [CallProcessorApollo](https://github.com/erdo/android-fore/blob/master/fore-network-kt/src/main/java/co/early/fore/kt/net/apollo/CallProcessorApollo.kt)). For usage examples, please see the appropriate example apps in the [repo](https://github.com/erdo/android-fore/).

The CallProcessor allows us to abstract all the networking related work so that the models can just deal with either successful data or domain error messages depending on the result of the network call (the models don't need to know anything about HTTP codes or io exceptions etc).

The Java and Kotlin implementations have slightly different APIs, while the Java implementation takes advantage of lambda expressions, the Kotlin implementation uses suspend functions and returns an [Either](https://github.com/erdo/android-fore/blob/master/fore-core-kt/src/main/java/co/early/fore/kt/core/Either.kt).

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
//Retrofit2 example

callProcessor.processCall(service.getFruits("3s"), workMode,
    successResponse -> handleSuccess(successResponse),
    failureMessage -> handleFailure(failureMessage)
);
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
//Retrofit2 example

launchMain(workMode) {

    val result = callProcessor.processCallAwait {
        service.getFruits("3s")
    }

    when (result) {
        is Left -> handleFailure(failureWithPayload, result.a)
        is Right -> handleSuccess(success, result.b)
    }
}
 </code></pre>

## Either either

I didn't really want to add yet another implementation of Either, but in the end it was preferrable to forcing people to use Arrow's Either (the feeling on Reddit semed to be that it was a bit rude to force it on to clients). Arrow's Either comes as part of the arrow-core-data package and that adds around 700KB to the overall apk size of a client app. That might not sound like a huge amount, but the whole point of fore is to be tiny / simple and 700KB is an order of magnitude larger than fore itself! For just one class it wasn't worth it.

So fore 1.2.1 is the last version that uses Arrow's Either by default. If you still want to use Arrow after that, it's no problem, simply add an extension function like this somewhere in your app code:

```kotlin

fun <L, R> Either<L, R>.toArrow(): arrow.core.Either<L, R> {
    return when(this){
        is Either.Left ->  arrow.core.Either.left(this.a)
        is Either.Right -> arrow.core.Either.right(this.b)
    }
}
```

And then you can convert any CallProcessor results from Fore Eithers to Arrow Eithers by doing: `result.toArrow()`. (You can of course use the same technique to convert Fore Eithers to whatever flavour of Either you prefer).

## carryOn

The kotlin CallProcessor is explained in detail [here](https://dev.to/erdo/tutorial-kotlin-coroutines-retrofit-and-fore-3874). That article also gets into how you can use the **carryOn** extension function that ships with **fore**. For a totally bonkers [9 lines of kotlin code](https://github.com/erdo/android-fore/blob/master/fore-network-kt/src/main/java/co/early/fore/kt/net/retrofit2/Retrofit2ResponseExt.kt), you get to chain your network calls together whilst also letting you handle **all** potential networking errors. It works with coroutines under the hood to banish nested callbacks and it'll let you write code like this:


```kotlin

//Retrofit2 example

callProcessor.processCallAsync {

    var ticketRef = ""
    ticketSvc.createUser() //Response<UserPojo>
    .carryOn {
      ticketSvc.createTicket(it.userId) //Response<TicketPojo>
    }
    .carryOn {
      ticketRef = it.ticketRef
      ticketSvc.getEstWaitingTime(it.ticketRef) //Response<TimePojo>
    }
    .carryOn {
      if (it.minutesWait > 10) {
        ticketSvc.cancelTicket(ticketRef) //Response<ResultPojo>
      } else {
        ticketSvc.confirmTicket(ticketRef) //Response<ResultPojo>
      }
   }
}

```

You can see it live in the kotlin version of [sample app 4](https://erdo.github.io/android-fore/#fore-4-retrofit-example)

*Because of small differences in Apollo's API design, it's not quite as convenient to chain calls together when using Apollo. There is however an extension function on fore's Either implementation which let's you achieve something [very similar](https://github.com/erdo/android-fore/blob/d859bfe40ffdf2d253fbed6df4bf9105633ab258/example-kt-07apollo/src/main/java/foo/bar/example/foreapollokt/feature/launch/LaunchesModel.kt#L104).*

## Custom APIs

All APIs will be slightly different regarding what global headers they require, what HTTP response codes they return and under what circumstances and how these codes map to domain model states. There will be a certain amount of customisation required, see the sample retrofit app for an [example](https://github.com/erdo/android-fore/tree/master/example-kt-04retrofit/src/main/java/foo/bar/example/foreretrofitkt/api) of this customisation.

The sample apps all use JSON over HTTP, but there is no reason you can't use something like protobuf, for example.


## Testing Networking Code

Another advantage of using the CallProcessor is that it can be mocked out during tests. The fore-retrofit sample app takes two alternative approaches to testing:

- [one](https://github.com/erdo/android-fore/blob/master/example-jv-04retrofit/src/test/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcherUnitTest.java) ([kotlin](https://github.com/erdo/android-fore/blob/master/example-kt-04retrofit/src/test/java/foo/bar/example/foreretrofitkt/feature/fruit/FruitFetcherUnitTest.kt)) is to simply mock the callProcessor so that it returns successes or failures to the model
- [the other](https://github.com/erdo/android-fore/blob/master/example-jv-04retrofit/src/test/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcherIntegrationTest.java) ([kotlin](https://github.com/erdo/android-fore/blob/master/example-kt-04retrofit/src/test/java/foo/bar/example/foreretrofitkt/feature/fruit/FruitFetcherIntegrationTest.kt)) is to use canned HTTP responses (local json data, and faked HTTP codes) to drive the call processor and therefore the model.

As with testing any asynchronous code with **fore**, we use WorkMode.**SYNCHRONOUS** to cause the Call to be processed on one thread which simplifies our test code (no need for latches etc).



# Adapter animations

*For some robust and testable implementations, please see the [Adapter Example App](https://erdo.github.io/android-fore/#fore-3-adapter-example)*

Firstly, if you _don't_ care about adapter animations, just call notifyDataSetChanged() from inside the syncView() function:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
public void syncView() {

  // set enabled states and visibilities etc
  ...

  adapter.notifyDataSetChanged();
}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
fun syncView() {

  // set enabled states and visibilities etc
  ...

  adapter.notifyDataSetChanged()
}
 </code></pre>


In this way you let your adapters piggy back on the observer which you have already setup for your view (it's the observer that calls syncView() whenever the model changes). Your adapter needs to be setup as in the examples, i.e. most of the logic will be moved to the model, and the adapter will have a reference to the model which is where it will get its list data from - see the sample app linked to above.


## Animations

Adapter animations are probably one of the most complicated parts of android to get completely right as subtle timing and threading differences will make them break in edge case situations. To be fair, getting adapters animations to work "99% of the time" is not too difficult (especially if your list changes are small and infrequent). But if you're chasing 100% robustness and you'd rather not depend on luck and timing for your apps performance (because your app has a lot of users, for instance) it can require some very carefully written code.

As most android developers know, in order to get animations you need to tell the adapter what kind of change actually happened i.e. what rows were added or changed etc. There are two ways to do this on android:

- Tell the adapter by calling the appropriate notifyItem... methods. This is how fore's **Updatable** classes work under the hood, in order to use these you need to be in a position to know what changes were made to the list, of course. For example if you are maintaining the list inside a model, and a public `addItem(newItem: Item)` function is called, the model knows that the list has had an item added. Similarly if a model's public `changeTextForItem(index:Int, text:String)` function, is called, then the model knows that the item at that index has been changed. Mostly fore works this out for you, with the exception of when an item is changed (in which case you need to manually call `list.makeAwareOfDataChange(index)`) You'll find example code for this here: [view](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/ui/playlist/updatable/UpdatableListView.kt), [adapter](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/ui/playlist/updatable/UpdatablePlaylistModelAdapter.kt), [model](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/feature/playlist/updatable/UpdatablePlaylistModel.kt)

- Tell the adapter by using android's DiffUtil. This is how fore's **Diffable** classes work under the hood. This method is ideal if you aren't in a position to know ahead of time what changes have been made to the list, for example if your list changes come via an API (i.e. you only get the new list, you don't get the new list and information about what changed since the old list). DiffUtil is a little more resource intensive (so fore's Diffable classes run DiffUtil using coroutines), you'll find example code for this here: [view](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/ui/playlist/diffable/DiffableListView.kt), [adapter](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/ui/playlist/diffable/DiffablePlaylistModelAdapter.kt), [model](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/feature/playlist/diffable/DiffablePlaylistModel.kt)

Android also provides us with **AsyncListDiffer / ListAdapter** (which also use DiffUtil under the hood). Depending on your situtation you might find these useful (one disadvantage is that it moves management of your list out of a model class and into an adapter, short-cutting some of the benefits of MVO). You'll find example code for this in the kotlin sample for comparison anyway, please see the guidance in the view source code: [view](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/ui/playlist/listdiffer/ListDifferListView.kt), [apapter](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters/src/main/java/foo/bar/example/foreadapterskt/ui/playlist/listdiffer/ListDifferPlaylistAdapter.kt), (there is no model).

Once things have been setup correctly with fore, the only thing you'll need to do in the view layer is call notifyDataSetChangedAuto() instead of notifyDataSetChanged() from the syncView() function.

> "just call **notifyDataSetChangedAuto()** instead of **notifyDataSetChanged()** from the **syncView()** function"

## Database driven RecyclerView Animations

The [**fore 6 db example**](https://erdo.github.io/android-fore/#fore-6-db-example-room) (which uses fore's Diffable classes) shows all the code needed for this, and also how to trigger view updates from a Room database using its InvalidationTracker (which is also how LiveData is notified of changes)

## Ensuring Robustness

More specifics regarding adapters and threading are in the source of [ObservableImp](https://github.com/erdo/android-fore/blob/master/fore-core-kt/src/main/java/co/early/fore/kt/core/observer/ObservableImp.kt) where it talks about the notificationMode. One of the subtle gotchas with android adapters is that when you update list data that is driving an adapter, **the actual change to the list must be done on the UI thread** and the **adapter must be told straight after** (or at least before the thread you are on, yields). Call it at the end of the method you are in, for example.

> "the change to the list data MUST be done on the UI thread AND the adapter MUST be told before the current thread yields"

The "fruit fetcher" screen of the [full app example](https://github.com/erdo/fore-full-example-02-kotlin) demonstrates that quite well, it's deliberately challenging to implement in a regular fashion (multiple simultaneous network calls changing the same list; user removal of list items; and screen rotation - all at any time) it's still totally robust as a result of sticking to that rule above.

This is because android will call **Adapter.count()** then **Adapter.get()** on the UI thread and *you must NOT change the adapter's size between these calls*. If after android calls Adapter.count(), you change the list but don't immediately let the adapter know that its count() call is out of date (by calling the notify... methods for example), when android next calls Adapter.get() you will have problems. Synchronizing any list updates is not enough. Even posting the notify... call to the end of the UI thread is not enough, it needs to be done immediately (before the UI thread yields) because once the UI thread yields it may let android in to call Adapter.get().

*Occasionally you may encounter people who believe that the key to robust adapter implementations is to have the adapter driven by an immutable list - I don't know where this advice comes from but it's nonsense unfortunately. When the list data changes, the adapter needs to be notified immediately, and both things need to happen on the UI thread, that's it. It's a shame the android docs do such a terrible job of explaining this.*

<a name="default-params"></a>
# Default parameters for WorkMode, Logger and SystemTimeWrapper

A lot of **fore** classes take parameters for WorkMode, Logger and SystemTimeWrapper in their constructor. That's done to make it very clear what needs to be swapped out when you want to inject different dehaviour (e.g. pass in a mock SystemTimeWrapper rather than a real one, when you want to test various time based behaviour). It's simple and clear but potentially annoying to see these parameters crop up all the time.

From **1.2.0** the kotlin APIs will set default values for these parameters if you don't specify them. If you chose to do that, you'll probably want to use `ForeDelegateHolder.setDelegate()` to [setup](https://github.com/erdo/android-fore/blob/d859bfe40ffdf2d253fbed6df4bf9105633ab258/example-kt-01reactiveui/src/test/java/foo/bar/example/forereactiveuikt/feature/wallet/WalletTest.kt#L22) your tests with. You will usually want `TestDelegateDefault()` for that, but you can create your own if you have some specific mocking requirements.

By default, a SilentLogger will be used so if you do nothing, your release build will have nothing logged by fore. During development you may wish to turn on fore logs by calling: `ForeDelegateHolder.setDelegate(DebugDelegateDefault("mytagprefix_"))`

All the defaults used are specified [here](https://github.com/erdo/android-fore/blob/d859bfe40ffdf2d253fbed6df4bf9105633ab258/fore-core-kt/src/main/java/co/early/fore/kt/core/delegate/Delegates.kt#L11).


# AsyncTasks with Lambdas

(_skip down to [Kotlin Coroutines](#kotlin-coroutines) if you prefer a coroutine based solution_)

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
new AsyncBuilder<Void, Integer>(workMode)
    .doInBackground(MyModel.this.doStuffInBackground())
    .onPostExecute(result -> MyModel.this.doThingsWithTheResult(result))
    .execute((Void) null);
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
AsyncBuilder<Unit, Int>(workMode)
    .doInBackground { this@MyModel.doStuffInBackground() }
    .onPostExecute { result -> this@MyModel.doThingsWithTheResult(result) }
    .execute()
 </code></pre>

We don't really want to be putting asynchronous code in the View layer unless we're very careful about it. So in this section we are mostly talking about Model code which often needs to do asynchronous operations, and also needs to be easily testable.

Android's AsyncTask suffers from a few problems - the main one being that it can't be tested and is difficult to mock because it needs to be instanciated each time it's used.

The quickest **fore** solution to all that is to use AsyncBuilder

[Asynchronous Example App Source Code](https://erdo.github.io/android-fore/#fore-2-asynchronous-code-example) is the simplest way to see this all in action by the way.

## AsyncBuilder

(_skip down to [Kotlin Coroutines](#kotlin-coroutines) if you prefer a non thread based solution_)

This class uses the builder pattern and has a cut down API to take advantage of lambda expressions. For reference here's the [source code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/threading/AsyncBuilder.java)

One restriction with AsyncBuilder is there is no way to publish progress as you can with android's AsyncTask. If you want to use that feature during your asynchronous operation, see the Async class below.


<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
new AsyncBuilder<String, Integer>(workMode)
    .doInBackground(input -> MyModel.this.doStuffInBackground(input))
    .onPostExecute(result -> MyModel.this.doThingsWithTheResult(result))
    .execute("input string");
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
AsyncBuilder<String, Int>(workMode)
    .doInBackground { input -> this@MyModel.doLongRunningStuff(input) }
    .onPostExecute { result -> this@MyModel.doThingsWithTheResult(result) }
    .execute("input string")
 </code></pre>

**AsyncBuilder (and Async) use a AsyncTask.THREAD_POOL_EXECUTOR in all versions of Android.**

### WorkMode Parameter
AsyncBuilder takes a constructor argument: WorkMode (in the same way that **fore** Observable does). The WorkMode parameter tells AsyncBuilder to operate in one of two modes (Asynchronous or Synchronous).

Passing WorkMode.ASYNCHRONOUS in the constructor makes the AsyncBuilder operate with the same behaviour as a normal AsyncTask.

Passing WorkMode.SYNCHRONOUS here on the other hand makes the whole AsyncBuilder run in one thread, blocking until it's complete. This makes testing it very easy as you remove the need to use any CountdownLatches or similar.

## Async
Async (which is basically a wrapper over AsyncTask that makes it testable) looks and behaves very similarly to android's AsyncTask and is an (almost) drop in replacement for it.

You should take a quick look at the [source code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/threading/Async.java) for Async, don't worry it's tiny.

Here's how you use Async:


<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
new Async<Void, Integer, Integer>(workMode) {

    @Override
    protected Integer doInBackground(Void... voids) {

        //do some stuff in the background
        ...

        //publish progress if you want
        publishProgressTask(progress);

        //return results
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progress = values[0];
        //do something with the progress
        ...
    }

    @Override
    protected void onPostExecute(Integer result) {
        //do something with the results once back on the UI thread
        ...
    }

}.executeTask((Void) null);
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
object : Async<Unit, Int, Int>(workMode) {
    override fun doInBackground(vararg empty: Unit?): Int {

      //do some stuff in the background
      ...

      //publish progress if you want
      publishProgressTask(progress);

      //return results
      return result;
    }

    override fun onProgressUpdate(vararg values: Int?) {

      progress = values[0]!!;

      //do something with the progress
      ...
    }

    override fun onPostExecute(result: Int?) {
      //do something with the results once back on the UI thread
      ...
    }

}.executeTask()
 </code></pre>


### ExecuteTask
**One difference with Async is that to run it, you need to call executeTask() instead of execute(). (AsyncTask.execute() is marked final).**


## Testing Asynchronous Code
For both Async and AsyncBuilder, testing is done by passing WorkMode.SYNCHRONOUS in via the constructor.

A convenient way to make this happen is to inject the WorkMode into the enclosing class at construction time so that WorkMode.ASYNCHRONOUS can be used for deployed code and WorkMode.SYNCHRONOUS can be used for testing. This method is demonstrated in the tests for the [Threading Sample](https://github.com/erdo/android-fore/blob/master/example-jv-02threading/src/test/java/foo/bar/example/forethreading/feature/counter/CounterWithLambdasTest.java)

# Kotlin Coroutines
With coroutines, Async and AsyncBuilder aren't really required (unless you prefer them). **fore** includes some extension functions which make coroutines much more testable than they otherwise would be, you can refer [here](https://github.com/erdo/android-fore/blob/master/example-kt-02coroutine/src/main/java/foo/bar/example/forecoroutine/feature/counter/Counter.kt) for example usage.

When using the **fore** coroutine classes, specifying the WorkMode parameter is now optional, see [here](https://erdo.github.io/android-fore/04-more-fore.html#default-params) for more

# SyncTrigger

The [SyncTrigger](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/ui/SyncTrigger.java) class lets you bridge the gap between syncView() (which is called at any time [an arbitrary number of times](https://erdo.github.io/android-fore/05-extras.html#notification-counting)) and an event like an animation that must be fired only once.

There is a dev.to tutorial on State and Events which might be worth a read, it discusses the syncTrigger [here](https://dev.to/erdo/tutorial-android-state-v-event-3n31#introducing-an-event).

When using a SyncTrigger you need to implement the **triggered()** method which will be run when the SyncTrigger is fired (e.g. to run an animation), and also implement the **checkThreshold()** method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON). If the threshold is breached i.e. checkThreshold() returns **true**, then triggered() will be called.

For this to work you will need to call **check()** on the SyncTrigger each time the syncView() method is called by your observers. Alternatively you can call **checkLazy()** which will cause the first check result after the SyncTrigger has been constructed to be ignored. This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger. (see the SyncTrigger source for more details about this).


By default, the SyncTrigger will be reset when checkThreshold() again returns **false**. Alternatively you can construct the SyncTrigger with ResetRule.IMMEDIATELY for an immediate reset.

Please see [here](https://github.com/erdo/android-fore/blob/master/example-jv-05ui/src/main/java/foo/bar/example/foreui/ui/tictactoe/TicTacToeView.java) for example usage of the SyncTrigger.
