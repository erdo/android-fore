
# Retrofit and the CallProcessor

The **CallProcessor** ([kotlin](https://github.com/erdo/android-fore/blob/master/fore-retrofit-kt/src/main/java/co/early/fore/kt/retrofit/CallProcessor.kt) \| [java](https://github.com/erdo/android-fore/blob/master/fore-retrofit/src/main/java/co/early/fore/retrofit/CallProcessor.java)) is a wrapper for the Retrofit2 Call class. For a usage example, please see the Retrofit [Example App](https://erdo.github.io/android-fore/#fore-4-retrofit-example).

The CallProcessor allows us to abstract all the networking related work so that the models can just deal with either successful data or domain error messages depending on the result of the network call (the models don't need to know anything about HTTP codes or io exceptions etc).

The Java and Kotlin implementations have slightly different APIs, while the Java implementation takes advantage of lambda expressions, the Kotlin implementation uses suspend functions and returns an [Either](https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-core-data/src/main/kotlin/arrow/core/Either.kt) (from [arrow-kt](https://arrow-kt.io/) - we didn't want to add yet another implementation of Either).

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
callProcessor.processCall(service.getFruits("3s"), workMode,
    successResponse -> handleSuccess(successResponse),
    failureMessage -> handleFailure(failureMessage)
);
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
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


## carryOn

The kotlin CallProcessor is explained in detail [here](https://dev.to/erdo/tutorial-kotlin-coroutines-retrofit-and-fore-3874). That article also gets into how you can use the **carryOn** extension function that ships with **fore**. For a totally bonkers [9 lines of kotlin code](https://github.com/erdo/android-fore/blob/master/fore-retrofit-kt/src/main/java/co/early/fore/kt/retrofit/ResponseExt.kt), you get to chain your network calls together whilst also letting you handle **all** potential networking errors. It works with coroutines under the hood to banish nested callbacks and it'll let you write code like this:


```kotlin

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


## Custom APIs

All APIs will be slightly different regarding what global headers they require, what HTTP response code they return and under what circumstances and how these codes map to domain model states. There will be a certain amount of customisation required, see the sample retrofit app for an [example](https://github.com/erdo/android-fore/tree/master/example-kt-04retrofit/src/main/java/foo/bar/example/foreretrofitkt/api) of this customisation.

The sample apps all use JSON over HTTP, but there is no reason you can't use something like protobuf, for example.


## Testing Networking Code

Another advantage of using the CallProcessor is that it can be mocked out during tests. The fore-retrofit sample app takes two alternative approaches to testing:

- [one](https://github.com/erdo/android-fore/blob/master/example04retrofit/src/test/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcherUnitTest.java) ([kotlin](https://github.com/erdo/android-fore/blob/master/example-kt-04retrofit/src/test/java/foo/bar/example/foreretrofitkt/feature/fruit/FruitFetcherUnitTest.kt)) is to simply mock the callProcessor so that it returns successes or failures to the model
- [the other](https://github.com/erdo/android-fore/blob/master/example04retrofit/src/test/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcherIntegrationTest.java) ([kotlin](https://github.com/erdo/android-fore/blob/master/example-kt-04retrofit/src/test/java/foo/bar/example/foreretrofitkt/feature/fruit/FruitFetcherIntegrationTest.kt)) is to use canned HTTP responses (local json data, and faked HTTP codes) to drive the call processor and therefore the model.

As with testing any asynchronous code with **fore**, we use WorkMode.**SYNCHRONOUS** to cause the Call to be processed on one thread which simplifies our test code (no need for latches etc).



# Adapters notifyDataSetChangedAuto()

*For a clean implementation in a small sample app, please see the [Adapter Example App Source Code](https://erdo.github.io/android-fore/#fore-3-adapter-example)*

Ahh adapters, I miss the good old days when all you had to do was call notifyDataSetChanged(). And the best place to call it is from inside the syncView() method:

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


In this way you let your adapters piggy back on the observer which you have already setup for your view (it's the observer that calls syncView() whenever the model changes). (Your adapter will have a reference to the model which is where it will get its list data from - see the sample app linked to above).

If you're not overly concerned with list animations I would continue to call notifyDataSetChanged anyway (yes it is marked as deprecated, but the alternative methods that android is offering are so difficult to implement correctly that I strongly suspect they will never be able to remove the original adapter.notifyDataSetChanged() method from the API).

*By the way, I've noticed people bizarrely claiming that notifyDataSetChanged() is "inefficient" but then replacing it with code that calls DiffUtil. Nothing wrong with DiffUtil, but that's like choosing black tea instead of black coffee because you're on a diet, but then taking your tea and adding 5 teaspoons of sugar, whipped cream and marsh mallows on top. If you ever see a lag using notifyDataSetChanged() then you're probably doing something very wrong (like re-inflating your cells' layout when you shouldn't)*


## RecyclerView Animations

So onwards and upwards! if you want list animations on android, they make you work quite hard for it. In order to get animations, you need to tell the adapter what kind of change actually happened, what rows were added or removed etc.

Happily by using the ChangeAware\* classes found in the fore-adapters library you can get **fore** to do most of the work for you.

As the name implies, the ChangeAware\*Lists are aware of how they have been changed and they feed that information back to the ChangeAwareAdapter (for your own code, just extend ChangeAwareAdapter instead of RecyclerView.Adapter).

When you call notifyDataSetChangedAuto() on the ChangeAwareAdapter, it will take care of calling the correct Android notify\* method for you. The only thing you need to take care of is telling the list what happened when an item has *changed* (the list has no way of detecting that automatically itself). For that, you use the method **ChangeAware\*List.makeAwareOfDataChange(int startRowIndex, int rowsAffected)** whenever an item is changed (rather than added or removed).


<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
public void syncView() {

  // set enabled states and visibilities etc
  ...

  adapter.notifyDataSetChangedAuto();
}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
fun syncView() {

  // set enabled states and visibilities etc
  ...

  adapter.notifyDataSetChangedAuto()
}
 </code></pre>


See [here](https://github.com/erdo/android-fore/blob/master/example03adapters/src/main/java/foo/bar/example/foreadapters/ui/playlist/advanced/PlaylistAdapterAdvanced.java) for an example adapter, the list for which is held in [this](https://github.com/erdo/android-fore/blob/master/example03adapters/src/main/java/foo/bar/example/foreadapters/feature/playlist/PlaylistAdvancedModel.java) model, see if you can spot the occasional call to makeAwareOfDataChange() in the model code.

This radically simplifies any [view code](https://github.com/erdo/android-fore/blob/master/example03adapters/src/main/java/foo/bar/example/foreadapters/ui/playlist/PlaylistsActivity.java) that needs to use an adapter and wants recycler view animations, the only thing that it needs to do is call **notifyDataSetChangedAuto()** instead of **notifyDataSetChanged()** from the **syncView()** function.

> "just call **notifyDataSetChangedAuto()** instead of **notifyDataSetChanged()** from the **syncView()** function"

## Database driven RecyclerView Animations

For lists that are being driven by a database table, the only way we can get animated changes is by comparing the two lists (the new versus the old) and try to work out what changed. This is because the view layer will not be aware of how the list has been changed as it will often be changed in another part of the system at the database layer. Thankfully Android has a *DiffUtil* class that does that for us, however it's a more heavy weight approach and isn't really useful once your lists gets larger than about 1000 items - in any case you want to be calculating the DiffResult in a separate thread.

**fore** wraps some of these Android classes and handles threading for you, so all you need to do is extend ChangeAwareAdapter but this time with a construction parameter that implements the **Diffable** interface, rather than the **Updatable** interface.

The [**fore 6 db example**](https://erdo.github.io/android-fore/#fore-6-db-example-room) shows all the code needed for this and also how to trigger view updates from a Room database using its InvalidationTracker (which is also how LiveData is notified of changes)

## Ensuring Robustness

More specifics regarding adapters and threading are in the source of [ObservableImp](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/observer/ObservableImp.java) where it talks about the notificationMode. The subtle gotcha with android adapters is that when you update list data that is driving an adapter, **the actual change to the list MUST to be done on the UI thread** and the **notify* must be called straight after** (or at least before the thread you are on, yields). Call it at the end of the method you are in, for example.

> "the change to the list data MUST to be done on the UI thread AND notify*() MUST be called before the current thread yields"

The "fruit fetcher" screen of the [full app example](https://github.com/erdo/fore-full-example-02-kotlin) demonstrates that quite well, it's deliberately challenging to implement in a regular fashion (multiple simultaneous network calls changing the same list; user removal of list items; and screen rotation - all at any time) it's still totally robust as a result of sticking to that rule above.

This is because android will call **Adapter.count()** then **Adapter.get()** on the UI thread and *you must NOT change the adapter's size between these calls*. If after android calls Adapter.count(), you change the list but don't immediately let the adapter know that its count() call is out of date (by calling the notify... methods), when android next calls Adapter.get() you will have problems. Synchronizing any list updates is not enough. Even posting the notify... call to the end of the UI thread is not enough, it needs to be done immediately (before the UI thread yields) because once the UI thread yields it may let android in to call Adapter.get().

*Occasionally you may encounter people who believe that the key to robust adapter implementations is to have the adapter driven by an immutable list - I don't know where this advice comes from but it's nonsense unfortunately. When the list data changes, the notify... method needs to be called immediately, and both things need to happen on the UI thread, that's it. It's a shame the android docs do such a terrible job of explaining this.*


# AsyncTasks with Lambdas

(_skip down to [Kotlin Coroutines](#kotlin-coroutines) if you prefer a non thread based solution_)

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

AsyncTask suffers from a few problems - the main one being that it can't be tested and is difficult to mock because it needs to be instanciated each time it's used.

The quickest **fore** solution to all that is to use Async as an (almost) drop in solution.

[Asynchronous Example App Source Code](https://erdo.github.io/android-fore/#fore-2-asynchronous-code-example) is the simplest way to see this all in action by the way.

## Async
Async (which is basically a wrapper over AsyncTask) looks and behaves very similarly to an AsyncTask with two exceptions detailed below.

Async uses a AsyncTask.THREAD_POOL_EXECUTOR in all versions of Android. You should take a quick look at the [source code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/threading/Async.java) for Async, don't worry it's tiny.

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


### WorkMode Parameter
Async takes a constructor argument: WorkMode (in the same way that **fore** Observable does). The WorkMode parameter tells Async to operate in one of two modes (Asynchronous or Synchronous).

Passing WorkMode.ASYNCHRONOUS in the constructor makes the Async operate with the same behaviour as a normal AsyncTask.

Passing WorkMode.SYNCHRONOUS here on the other hand makes the whole Async run in one thread, blocking until it's complete. This makes testing it very easy as you remove the need to use any CountdownLatches or similar.


### ExecuteTask
The other difference with Async is that to run it, you need to call executeTask() instead of execute(). (AsyncTask.execute() is marked final).


## AsyncBuilder

For slightly more concise code, you can use AsyncBuilder. This class works in much the same way as Async, it just uses the build pattern and has a cut down API to take advantage of lambda expressions. For reference here's the [source code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/threading/AsyncBuilder.java)

One restriction with AsyncBuilder is there is no way to publish progress, so if you want to use that feature during your asynchronous operation, just stick to a plain Async.


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


## Testing Asynchronous Code
For both Async and AsyncBuilder, testing is done by passing WorkMode.SYNCHRONOUS in via the constructor.

A convenient way to make this happen is to inject the WorkMode into the enclosing class at construction time so that WorkMode.ASYNCHRONOUS can be used for deployed code and WorkMode.SYNCHRONOUS can be used for testing. This method is demonstrated in the tests for the [Threading Sample](https://github.com/erdo/android-fore/blob/master/example02threading/src/test/java/foo/bar/example/forethreading/feature/counter/CounterWithLambdasTest.java)

# Kotlin Coroutines
With coroutines, Async and AsyncBuilder aren't really required (unless you prefer them). **fore** includes some extension functions which make coroutines much more testable than they otherwise would be, you can refer [here](https://github.com/erdo/android-fore/blob/master/example-kt-02coroutine/src/main/java/foo/bar/example/forecoroutine/feature/counter/Counter.kt) for example usage.

As you'll notice in the source code [comments](https://github.com/erdo/android-fore/blob/master/fore-core-kt/src/main/java/co/early/fore/kt/core/coroutine/Ext.kt) for the extension functions, there is a good reason we use the same workMode parameter here to test this asynchronous code, as we do with all the fore components.

# SyncTrigger

The [SyncTrigger](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/ui/SyncTrigger.java) class lets you bridge the gap between syncView() (which is called at any time [an arbitrary number of times](https://erdo.github.io/android-fore/05-extras.html#notification-counting)) and an event like an animation that must be fired only once.

There is a dev.to tutorial on State and Events which might be worth a read, it discusses the syncTrigger [here](https://dev.to/erdo/tutorial-android-state-v-event-3n31#introducing-an-event).

When using a SyncTrigger you need to implement the **triggered()** method which will be run when the SyncTrigger is fired (e.g. to run an animation), and also implement the **checkThreshold()** method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON). If the threshold is breached i.e. checkThreshold() returns **true**, then triggered() will be called.

For this to work you will need to call **check()** on the SyncTrigger each time the syncView() method is called by your observers. Alternatively you can call **checkLazy()** which will cause the first check result after the SyncTrigger has been constructed to be ignored. This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger. (see the [SyncTrigger source](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/ui/SyncTrigger.java) for more details about this).


By default, the SyncTrigger will be reset when checkThreshold() again returns **false**. Alternatively you can construct the SyncTrigger with ResetRule.IMMEDIATELY for an immediate reset.

Please see [here](https://github.com/erdo/android-fore/blob/master/example05ui/src/main/java/foo/bar/example/foreui/ui/tictactoe/TicTacToeView.java) for example usage of the SyncTrigger.
