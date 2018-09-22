
# Retrofit and the CallProcessor

The [CallProcessor](https://github.com/erdo/android-fore/blob/master/fore-retrofit/src/main/java/co/early/fore/retrofit/CallProcessor.java) is a wrapper for the Retrofit2 Call class. For a usage example, please see the [Retrofit Example App Source Code](/android-fore/#fore-4-retrofit-example). The CallProcessor allows us to abstract all the networking related work so that the models can just deal with either successful data or domain model error messages depending on the result of the network call (the models don't need to know anything about HTTP codes or io exceptions etc).

When taking advantage of lambda expressions, the code can become very tight indeed:

```
    callProcessor.processCall(service.getFruits("3s"), workMode,
        successResponse -> handleSuccess(successResponse),
        failureMessage -> handleFailure(failureMessage)
    );
```

## Custom APIs

All APIs will be slightly different regarding what global headers they require, what HTTP response code they return and under what circumstances and how these codes map to domain model states. There will be a certain amount of customisation required, see the sample retrofit app for an [example](https://github.com/erdo/android-fore/tree/master/example04retrofit/src/main/java/foo/bar/example/foreretrofit/api) of this customisation.

The sample apps all use JSON over HTTP, but there is no reason you can't use something like protobuf, for example.


## Testing Networking Code

Another advantage of using the CallProcessor is that it can be mocked out during tests. The fore-retrofit sample app takes two alternative approaches to testing:

- [one](https://github.com/erdo/android-fore/blob/master/example04retrofit/src/test/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcherUnitTest.java) is to simply mock the callProcessor so that it returns successes or failures to the model
- [the other](https://github.com/erdo/android-fore/blob/master/example04retrofit/src/test/java/foo/bar/example/foreretrofit/feature/fruit/FruitFetcherIntegrationTest.java) is to use canned HTTP responses (local json data, and faked HTTP codes) to drive the call processor and therefore the model.

As with testing any asynchronous code with **fore**, we use WorkMode.SYNCHRONOUS to cause the Call to be processed on one thread which simplifies our test code (no need for latches etc).



# Adapters notifyDataSetChangedAuto()

*For a clean implementation in a small sample app, please see the [Adapter Example App Source Code](/android-fore/#fore-3-adapter-example)*

Ahh adapters, I miss the good old days when all you had to do was call notifyDataSetChanged(). And the best place to call it is from inside the syncView() method:

```
public void syncView() {

  // set enabled states and visibilities etc
  ...

  adapter.notifyDataSetChanged();
}
```

In this way you let your adapters piggy back on the observer which you have already setup for your view (it's the observer that calls syncView() whenever the model changes).

You could also add your adapter as an observer on the model directly, but doing it like that usually causes problems because you will also need to find a way to remove it correctly (see these items in the code review check list [here](https://erdo.github.io/android-fore/05-code-review-checklist.html#non-lifecycle-observers) and [here](https://erdo.github.io/android-fore/05-code-review-checklist.html#add-remove)).

If you're not overly concerned with list animations I would continue to call notifyDataSetChanged anyway (yes it is marked as deprecated, but the alternative methods that android is offering are so difficult to implement correctly that I strongly suspect they will never be able to remove the original adapter.notifyDataSetChanged() method from the API)


## RecyclerView Animations

So onwards and upwards! if you want list animations on android, they make you work quite hard for it. In order to get animations, you need to tell the adapter what kind of change actually happened, what rows were added or removed etc. This is one case in particular that it was so tempting to just add a parameter to the **fore** observable. [It still wasn't worth it though](https://erdo.github.io/android-fore/05-more.html#somethingchanged-parameter).

Happily by using the ChangeAware\* classes found in the fore-adapters library you can get **fore** to do most of the work for you.

As the name implies, the ChangeAware\*Lists are aware of how they have been changed and they feed that information back to the ChangeAwareAdapter (for your own code, just extend ChangeAwareAdapter instead of RecyclerView.Adapter).

When you call notifyDataSetChangedAuto() on the ChangeAwareAdapter, it will take care of calling the correct Android notify* method for you. The only thing you need to take care of is telling the list what happened when an item has *changed* (the list has no way of detecting that automatically itself). For that, you use the method **ChangeAware\*List.makeAwareOfDataChange(int index)** whenever an item is changed (rather than added or removed).


```
public void syncView() {

  // set enabled states and visibilities etc
  ...

  adapter.notifyDataSetChangedAuto();
}
```


See [here](https://github.com/erdo/android-fore/blob/master/example03adapters/src/main/java/foo/bar/example/foreadapters/ui/playlist/advanced/PlaylistAdapterAdvanced.java) for an example adapter, the list for which is held in [this](https://github.com/erdo/android-fore/blob/master/example03adapters/src/main/java/foo/bar/example/foreadapters/feature/playlist/PlaylistAdvancedModel.java) model, see if you can spot the occasional call to makeAwareOfDataChange() in the model code. This radically simplifies any [view code](https://github.com/erdo/android-fore/blob/master/example03adapters/src/main/java/foo/bar/example/foreadapters/ui/playlist/PlaylistsView.java) that needs to use an adapter and wants recycler view animations, the only thing that it needs to do is call **notifyDataSetChangedAuto()** from the **syncView()** method.

## Database driven RecyclerView Animations

For lists that are being driven by a database table, the only way we can get animated changes is by comparing the two lists (the new versus the old) and try to work out what changed. This is because the view layer will not be aware of how the list has been changed as it will often be changed in another part of the system at the database layer. Thankfully Android has a *DiffResult* class that does that for us, however it's a more heavy weight approach and isn't really useful once your lists gets larger than about 1000 items.

**fore** wraps some of these Android classes and handles threading for you, so all you need to do is extend ChangeAwareAdapter but this time with a construction parameter that implements the Diffable interface, rather than the Updatable interface.

The [**fore** 6 db example**](https://erdo.github.io/android-fore/#fore-6-db-example-room) shows all the code needed for this and also how to trigger view updates from a Room database using its InvalidationTracker (which is also how LiveData is notified of changes)

## Ensuring Robustness

More specifics regarding adapters and threading are in the source of [ObservableImp](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/observer/ObservableImp.java) where it talks about the notificationMode. The subtle gotcha with android adapters is that when you update list data that is driving an adapter, **the actual change to the list MUST to be done on the UI thread** and the **notify* must be called straight after** (or at least before the thread you are on, yields). Call it at the end of the method you are in, for example.

> "the change to the list data MUST to be done on the UI thread AND notify*() MUST be called before the current thread yields"

The "fruit fetcher" screen of the [full app example](https://github.com/erdo/asaf-full-app-example) demonstrates that quite well, it's deliberately challenging to implement in a regular fashion (multiple simultaneous network calls changing the same list; user removal of list items; and screen rotation - all at any time) it's still totally robust as a result of sticking to that rule above.





# AsyncTasks with Lambdas

```
new AsyncBuilder<Void, Integer>(workMode)
    .doInBackground(input -> MyModel.this.doStuffInBackground(input))
    .onPostExecute(result -> MyModel.this.doThingsWithTheResult(result))
    .execute((Void) null);
```


We don't really want to be putting asynchronous code in the View layer unless we're very careful about it. So in this section we are mostly talking about Model code which often needs to do asynchronous operations, and also needs to be easily testable.

AsyncTask suffers from a few problems - the main one being that it can't be tested and is difficult to mock because of the "new" keyword.

The quickest **fore** solution to all that is to use Async as an (almost) drop in solution.

[Asynchronous Example App Source Code](/android-fore/#fore-2-asynchronous-code-example) is the simplest way to see this all in action by the way.

## Async
Async (which is basically a wrapper over AsyncTask) looks and behaves very similarly to an AsyncTask* with two exceptions detailed below.

*Async uses a AsyncTask.THREAD_POOL_EXECUTOR in all versions of Android. You should take a quick look at the [source code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/threading/Async.java) for Async, don't worry it's tiny.

Here's how you use Async:


```
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

}.executeTask((Void)null);
```


### WorkMode Parameter
Async takes a constructor argument: WorkMode (in the same way that **fore** Observable does). The WorkMode parameter tells Async to operate in one of two modes (Asynchronous or Synchronous).

Passing WorkMode.ASYNCHRONOUS in the constructor makes the Async operate with the same behaviour as a normal AsyncTask.

Passing WorkMode.SYNCHRONOUS here on the other hand makes the whole Async run in one thread, blocking until it's complete. This makes testing it very easy as you remove the need to use any CountdownLatches or similar.


### ExecuteTask
The other difference with Async is that to run it, you need to call executeTask() instead of execute(). (AsyncTask.execute() is marked final).


## AsyncBuilder

For slightly more concise code, you can use AsyncBuilder. This class works in much the same way as Async, it just uses the build pattern and has a cut down API to take advantage of lambda expressions. For reference here's the [source code](https://github.com/erdo/android-fore/blob/master/fore-core/src/main/java/co/early/fore/core/threading/AsyncBuilder.java)

One restriction with AsyncBuilder is there is no way to publish progress, so if you want to use that feature during your asynchronous operation, just stick to a plain Async.


```
new AsyncBuilder<Void, Integer>(workMode)
    .doInBackground(new DoInBackgroundCallback<Void, Integer>() {
        @Override
        public Integer doThisAndReturn(Void... input) {
            return MyModel.this.doLongRunningStuff(input);
        }
    })
    .onPostExecute(new DoThisWithPayloadCallback<Integer>() {
        @Override
        public void doThis(Integer result) {
            MyModel.this.doThingsWithTheResult(result);
        }
    })
    .execute((Void) null);

```

That might not look particularly clean, but it gets a lot cleaner once you are using lambda expressions.

```
new AsyncBuilder<Void, Integer>(workMode)
    .doInBackground(input -> MyModel.this.doStuffInBackground(input))
    .onPostExecute(result -> MyModel.this.doThingsWithTheResult(result))
    .execute((Void) null);
```


## Testing Asynchronous Code
For both Async and AsyncBuilder, testing is done by passing WorkMode.SYNCHRONOUS in via the constructor.

A convenient way to make this happen is to inject the WorkMode into the enclosing class at construction time so that WorkMode.ASYNCHRONOUS can be used for deployed code and WorkMode.SYNCHRONOUS can be used for testing. This method is demonstrated in the tests for the [Threading Sample](https://github.com/erdo/android-fore/blob/master/example02threading/src/test/java/foo/bar/example/forethreading/feature/counter/CounterWithLambdasTest.java)





# Lifecycle Components

Totally optional, but you can use them to remove the databinding boiler plate from your views completely. For example usage please refer to the [UI Helpers Example App Source Code](/android-fore/#fore-5-ui-helpers-example-tic-tac-toe).

## SyncableXXX Convenience Classes

**fore** includes various SyncableXXX classes which will do the work of adding and removing observers inline with lifecycle methods and calling syncView() when required. They operate at the Activity or Fragment level and are completely optional, but to use these classes you will have to extend from them rather than extending from the usual Activity / Fragment classes.

- [SyncableAppCompatActivity](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/activity/SyncableAppCompatActivity.java)
- [SyncableActivity](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/activity/SyncableActivity.java)
- [SyncableSupportFragment](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/fragment/SyncableSupportFragment.java)
- [SyncableFragment](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/fragment/SyncableFragment.java)




# SyncTrigger

The [SyncTrigger](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/SyncTrigger.java) class lets you bridge the gap between syncView() (which is called at any time [an arbitrary number of times](https://erdo.github.io/android-fore/05-more.html#notification-counting)) and an event like an animation that must be fired only once.

When using a SyncTrigger you need to implement the **triggered()** method which will be run when the SyncTrigger is fired (e.g. to run an animation), and also implement the **checkThreshold()** method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON). If the threshold is breached i.e. checkThreshold() returns **true**, then triggered() will be called.

For this to work you will need to call **check()** on the SyncTrigger each time the syncView() method is called by your observers. Alternatively you can call **checkLazy()** which will cause the first check result after the SyncTrigger has been constructed to be ignored. This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger. (see the [SyncTrigger source](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/SyncTrigger.java) for more details about this).


By default, the SyncTrigger will be reset when checkThreshold() again returns **false**. Alternatively you can construct the SyncTrigger with ResetRule.IMMEDIATELY for an immediate reset.

Please see [here](https://github.com/erdo/android-fore/blob/master/example05ui/src/main/java/foo/bar/example/foreui/ui/tictactoe/TicTacToeView.java) for example usage of the SyncTrigger.
