
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

Maybe you want to swap in a mock NetworkAccess for a test so that it doesn't actually connnect to the network when you run the test. If NetworkAccess is an interface, dependency injection would let you replace the entire implementation with another one without having to alter the rest of your code.

A quick way to check how your code is doing on this front is to look for the keyword ***new***. If it's there, then that is a depenency that won't be able to be swapped or mocked out at a later date (which may be fine, as long as you are aware of it).

*Incidentally don't let anyone tell you that you must use a dependency injection framework in your android app. In the ASAF sample apps, all the dependencies are managed in the ObjectGraph class and managing even 100 dependencies in there is no big deal (and if you have a app with more than 100 global scope dependencies then you're probably doing something wrong, or maybe you're using a framework like MVP which could seriously increase the number of dependencies you need to keep track of... or maybe you're injecting all your networking dependencies directly into your Fragments instead of wrapping them up in model classes :/ ) Anyway, if you and your team dig dagger, then use it. But if you spent a few days stabbing yourself in the eye with it instead - feel free to manage those dependencies yourself.*


### Inversion of Control

This term caused me a lot of confusion in past so here's how I look at it.

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


## Testing
For both AsafTask and AsafTaskBuilder, testing is done by passing WorkMode.SYNCHRONOUS in via the constructor.

A conveient way to make this happen is to inject the WorkMode into the enclosing class at construciton time so that it WorkMode.ASYNCHRONOUS can be used for deployed code and WorkMode.SYNCHRONOUS can be used for testing. This method is demonstrated in the tests for the [Threading Sample](https://github.com/erdo/asaf-project/blob/master/examplethreading/src/test/java/foo/bar/example/asafthreading/feature/counter/CounterWithLambdasTest.java)


# Adapters

//TODO

[Adapter Example App Source Code](/asaf-project/#asaf-3-adapter-code-example)


# Retrofit and the CallProcessor

//TODO

[Retrofit Example App Source Code](/asaf-project/#asaf-4-retrofit-code-example)


# UI Widgets and Helpers

//TODO
