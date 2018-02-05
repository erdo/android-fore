
# Dependency Injection

Dependency Injection is pretty important, without it I'm not sure how you could write a properly tested Android app. But it's not actually that complicated.

All it really means is instead of instantiating the things that you need to use (dependencies) inside of the class that you're currently in, pass them **to** your class instead (either via the constructor or some other method if the constructor is not available such as with the Android UI classes).


Don't do this:

```
public MessageSender() {
    networkAccess = new NetworkAccess();
}
```

Do this instead

```
public MessageSender(NetworkAccess networkAccess) {
    this.networkAccess = networkAccess;
}
```

If you don't have access to the constructor, you can do this (like we do in a lot of the ASAF sample apps):

```
MessageSender messageSender;

protected void onFinishInflate() {
    super.onFinishInflate();

    messageSender = CustomApp.get(MessageSender);
}
```

In a commercial app, the number of dependencies you need to keep track can sometimes of can get pretty large, so some people use a library like Dagger2 to manage this:

```
@Inject MessageSender messageSender;

protected void onFinishInflate() {
    super.onFinishInflate();

    DaggerComponent.inject(this);
}
```

The main reason for all of this is that dependency injection enables you to swap out that NetworkAccess dependency (or swap out MessageSender) in different situations.

Maybe you want to swap in a mock NetworkAccess for a test so that it doesn't actually connect to the network when you run the test. If NetworkAccess is an interface, dependency injection would let you replace the entire implementation with another one without having to alter the rest of your code.

A quick way to check how your code is doing on this front is to look for the keyword ***new***. If it's there, then that is a depenency that won't be able to be swapped or mocked out at a later date (which may be fine, as long as you are aware of it).

*Incidentally don't let anyone tell you that you must use a dependency injection framework in your android app. In the ASAF sample apps, all the dependencies are managed in the ObjectGraph class and managing even 100 dependencies in there is no big deal (and if you have an app with more than 100 global scope dependencies then you're probably doing something wrong) Anyway, if you and your team dig dagger, then use it. But if you spent a few days stabbing yourself in the eye with it instead - feel free to manage those dependencies yourself. See [here](http://blog.ploeh.dk/2014/06/10/pure-di/) for more on this*


### Inversion of Control

This term really confused me when I first heard it years ago, so here's my take in case it's helpful for you.

Imagine if we have a company. It has a CEO at the top. Underneath the CEO are departments like Marketing, HR, Finance. Those departments all print documents using a printer.

The CEO is in control of the whole lot, whatever she says goes. But if you took that to extremes it would be ridiculous. The CEO would tell the departments exactly what documents to print, but also with what paper, and what printer ink. When paper tray 3 ran out, the CEO would be the one to decide to switch to using paper tray 2 instead, or to display an error on the printer display. After 5 minutes of no printing, the CEO would decide to put the printer into power saving mode. You get the idea. Don't write software like that.

Inversion of control means turning that control on its head and giving it to the lower parts of the system. Who decides when to enter power saving mode on the printer? the printer does, it has control. And the printer wasn't manufactured in the office, it was made elsewhere and "injected" into the office by being delivered. Sometimes it gets swapped out for a newer model that prints more reliably. Write software like that.



# Asynchronous Code

We don't really want to be putting asynchronous code in the View layer unless we're very careful about it. So in this section we are mostly talking about Model code which often needs to do asynchronous operations, and also needs to be easily testable.

AsyncTask suffers from a few problems - the main one being that it can't be tested and is difficult to mock because of the "new" keyword.

The quickest ASAF solution to all that is to use AsafTask as an (almost) drop in solution.

[Asynchronous Example App Source Code](/asaf-project/#asaf-2-asynchronous-code-example) is the simplest way to see this all in action by the way.

## AsafTask
AsafTask (which is basically a wrapper over AsyncTask) looks and behaves very similarly to an AsyncTask* with two exceptions detailed below.

*AsafTask uses a AsyncTask.THREAD_POOL_EXECUTOR in all versions of Android. You should take a quick look at the [source code](https://github.com/erdo/asaf-project/blob/master/asaf-core/src/main/java/co/early/asaf/core/threading/AsafTask.java) for AsafTask, don't worry it's tiny.

Here's how you use AsafTask:


```
new AsafTask<Void, Integer, Integer>(workMode) {

    @Override
    protected Integer doInBackground(Void... voids) {

        //do some stuff in the backgroud
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
AsafTask takes a constructor argument: WorkMode (in the same way that ASAF Observable does). The WorkMode parameter tells AsafTask to operate in one of two modes (Asynchronous or Synchronous).

Passing WorkMode.ASYNCHRONOUS in the contructor makes the AsafTask operate with the same behaviour as a normal AsyncTask.

Passing WorkMode.SYNCHRONOUS here on the other hand makes the whole AsafTask run in one thread, blocking until it's complete. This makes testing it very easy as you remove the need to use any CountdownLatches or similar.


### ExecuteTask
The other difference with AsafTask is that to run it, you need to call executeTask() instead of execute(). (AsyncTask.execute() is marked final).


## AsafTaskBuilder

For slightly more concise code, you can use AsafTaskBuilder. This class works in much the same way as AsafTask, it just uses the build pattern and has a cut down API to take advantage of lambda expressions. For reference here's the [source code](https://github.com/erdo/asaf-project/blob/master/asaf-core/src/main/java/co/early/asaf/core/threading/AsafTaskBuilder.java)

One restriction with AsafTaskBuilder is there is no way to pulish progress, so if you want to use that feature during your asynchronous operation, just stick to a plain AsafTask.


```
new AsafTaskBuilder<Void, Integer>(workMode)
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
new AsafTaskBuilder<Void, Integer>(workMode)
    .doInBackground(input -> MyModel.this.doStuffInBackground(input))
    .onPostExecute(result -> MyModel.this.doThingsWithTheResult(result))
    .execute((Void) null);
```


## Testing Asynchronous Code
For both AsafTask and AsafTaskBuilder, testing is done by passing WorkMode.SYNCHRONOUS in via the constructor.

A conveient way to make this happen is to inject the WorkMode into the enclosing class at construciton time so that WorkMode.ASYNCHRONOUS can be used for deployed code and WorkMode.SYNCHRONOUS can be used for testing. This method is demonstrated in the tests for the [Threading Sample](https://github.com/erdo/asaf-project/blob/master/example02threading/src/test/java/foo/bar/example/asafthreading/feature/counter/CounterWithLambdasTest.java)


# Adapters

*For a clean implementation in a small sample app, please see the [Adapter Example App Source Code](/asaf-project/#asaf-3-adapter-example)*

Ahh adapters, I miss the good old days when all you had to do was call notifyDataSetChanged(). And the best place to call it is from inside the syncView() method:

    public void syncView() {

		// set enabled states and visibilities etc
		...
		
        adapter.notifyDataSetChanged();
    }

In this way you let your adapters piggy back on the observer which you have already setup for your view (it's the observer that calls syncView() whenever the model changes).

You could also add your adapter as an observer on the model directly, but doing it like that usually causes problems because you will also need to find a way to remove it correctly (see these items in the code review check list [here](https://erdo.github.io/asaf-project/05-code-review-checklist.html#non-lifecycle-observers) and [here](https://erdo.github.io/asaf-project/05-code-review-checklist.html#add-remove)).

If you're not overly concerned with list animations I would continue to call notifyDataSetChanged anyway (yes it is marked as deprectated, but the alternative methods that android is offering are so difficult to implement correctly that I strongly suspect they will never be able to remove the original adapter.notifyDataSetChanged() method from the API)


## RecyclerView Animations

So onwards and upwards! if you want list animations on android, they make you work quite hard for it. In order to get animations, you need to tell the adapter what kind of change actually happened, what rows were added or removed etc. This is one case in particular that it was so tempting to just add a parameter to the ASAF observable. [It still wasn't worth it though](https://erdo.github.io/asaf-project/06-faq.html#somethingchanged-parameter).

Happily by using the ChangeAware\*Lists found in the asaf-adapters library you can get ASAF to do most of the work for you.

As the name implies, the ChangeAware\*Lists are aware of how they have been changed and they feed that information back to the ChangeAwareAdapter (for your own code, just extend ChangeAwareAdapter instead of RecyclerView.Adapter).

When you call notifyDataSetChangedAuto() on the ChangeAwareAdapter, it will take care of calling the correct Android notify* method for you. The only thing you need to take care of is telling the list what happened when an item has *changed* (the list has no way of detecting that automatically itself). For that, you use the method **ChangeAware\*List.makeAwareOfDataChange(int index)** whenever an item is changed (rather than added or removed).

See [here](https://github.com/erdo/asaf-project/blob/master/example03adapters/src/main/java/foo/bar/example/asafadapters/ui/playlist/advanced/PlaylistAdapterAdvanced.java) for an example adapter, the list for which is held in [this](https://github.com/erdo/asaf-project/blob/master/example03adapters/src/main/java/foo/bar/example/asafadapters/feature/playlist/PlaylistAdvancedModel.java) model, see if you can spot the occasional call to makeAwareOfDataChange() in the model code. This radically simplifies any [view code](https://github.com/erdo/asaf-project/blob/master/example03adapters/src/main/java/foo/bar/example/asafadapters/ui/playlist/PlaylistsView.java) that needs to use an adapter and wants recycler view animations, the only thing that it needs to do is call **notifyDataSethangedAuto()** from the **syncView()** method.


## Ensuring Robustness

More specifics regarding adapters and threading are in the source of [ObservableImp](https://github.com/erdo/asaf-project/blob/master/asaf-core/src/main/java/co/early/asaf/core/observer/ObservableImp.java) where it talks about the notificationMode. The subtle gotcha with android adapters is that when you update list data that is driving an adapter, **the actual change to the list MUST to be done on the UI thread** and the **notify* must be called straight after** (or at least before the thread you are on, yields). Call it at the end of the method you are in, for example.

> "the change to the list data MUST to be done on the UI thread AND notify*() MUST be called before the current thread yields"

The "fruit fetcher" screen of the [full app example](https://github.com/erdo/asaf-full-app-example) demonstrates that quite well, it's deliberately challenging to implement in a regular fasion (multiple simultaneous network calls changing the same list; user removal of list items; and screen rotation - all at any time) it's still totally robust as a result of sticking to that rule above.


# Retrofit and the CallProcessor

The [CallProcessor](https://github.com/erdo/asaf-project/blob/master/asaf-retrofit/src/main/java/co/early/asaf/retrofit/CallProcessor.java) is a wrapper for the Retrofit2 Call class. For a useage example, please see the [Retrofit Example App Source Code](/asaf-project/#asaf-4-retrofit-example). The CallProcessor allows us to abstract all the networking related work so that the models can just deal with either successful data or domain model error messages depending on the result of the network call (the models don't need to know anything about HTTP codes or io exceptions etc).

When taking advantage of lamda expressions, the code can become very tight indeed:

```
    callProcessor.processCall(fruitService.getFruits("3s"), workMode,
        successResponse -> handleSuccess(successResponse),
        failureMessage -> handleFailure(failureMessage)
    );
```

## Custom APIs

All APIs will be slightly different regarding what global headers they require, what HTTP response code they return and under what circumstances and how these codes map to domain model states. There will be a certain amount of customisation required, see the sample retrofit app for an [example](https://github.com/erdo/asaf-project/tree/master/example04retrofit/src/main/java/foo/bar/example/asafretrofit/api) of this customisation.

The sample apps all use JSON over HTTP, but there is no reason you can't use something like protobuf, for example.


## Testing Networking Code

Another advantage of using the CallProcessor is that it can be mocked out during tests. The asaf-retrofit sample app takes two alternative approaches to testing:

- [one](https://github.com/erdo/asaf-project/blob/master/example04retrofit/src/test/java/foo/bar/example/asafretrofit/feature/fruit/FruitFetcherUnitTest.java) is to simply mock the callProcessor so that it returns successes or failures to the model
- [the other](https://github.com/erdo/asaf-project/blob/master/example04retrofit/src/test/java/foo/bar/example/asafretrofit/feature/fruit/FruitFetcherIntegrationTest.java) is to use canned HTTP responses (local json data, and faked HTTP codes) to drive the call processor and therefore the model.

As with testing any asynchronous code with ASAF, we use WorkMode.SYNCHRONOUS to cause the Call to be processed on one thread which simplifies our test code (no need for latches etc).



# UI Widgets and Helpers

For example useage please refer to the [UI Helpers Example App Source Code](/asaf-project/#asaf-5-ui-helpers-example-tic-tac-toe).

## SyncableXXX Convenience Classes

ASAF includes various SyncableXXX classes which will reduce some of the boiler plate code in your views related to adding and removing observers inline with lifecycle methods and calling syncView() when required. They operate at the Activity or Fragment level and are completely optional, but to use these classes you will have to extend from them rather than extending from the usual Activty / Fragment classes.

- [SyncableAppCompatActivity](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/activity/SyncableAppCompatActivity.java)
- [SyncableActivity](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/activity/SyncableActivity.java)
- [SyncableSupportFragment](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/fragment/SyncableSupportFragment.java)
- [SyncableFragment](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/fragment/SyncableFragment.java)



## SyncTrigger

The [SyncTrigger](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/SyncTrigger.java) class lets you bridge the gap between syncView() (which is called at any time [an arbitrary number of times](https://erdo.github.io/asaf-project/05-code-review-checklist.html#notification-counting)) and an event like an animation that must be fired only once.

When using a SyncTrigger you need to implement the **triggered()** method which will be run when the SyncTrigger is fired (e.g. to run an animation), and also implement the **checkThreshold()** method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON). If the threshold is breached i.e. checkThreshold() returns **true**, then triggered() will be called.

For this to work you will need to call **check()** on the SyncTrigger each time the syncView() method is called by your observers. Alernatively you can call **checkLazy()** which will cause the first check result after the SyncTrigger has been constructed to be ignored. This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger. (see the [SyncTrigger source](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/SyncTrigger.java) for more details about this).


By default, the SyncTrigger will be reset when checkThreshold() again returns **false**. Alternatively you can construct the SyncTrigger with ResetRule.IMMEDIATELY for an immediate reset.

Please see [here](https://github.com/erdo/asaf-project/blob/master/example05ui/src/main/java/foo/bar/example/asafui/ui/tictactoe/TicTacToeView.java) for example useage of the SyncTrigger.

