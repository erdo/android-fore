

WIP...

# Dependency Injection

//TODO

# Adapters

//TODO


# Asynchronous Code

We don't really want to be putting asynchronous code in the View layer unless we're very careful about it. So in this section we are mostly talking about Model code which often needs to do asynchronous operations, and also needs to be easily testable.

AsyncTask suffers from a few problems - the main one being that it can't be tested and is difficult to mock because of the "new" keyword.

The quickest ASAF solution to all that is to use AsafTask as an (almost) drop in solution.

**Example 3 in the project is the simplest way to see this all in action by the way.**

## AsafTask
AsafTask (which is basically a wrapper over AsyncTask) looks and behaves very similarly to an AsyncTask* with two exceptions detailed below.

*AsafTask uses a AsyncTask.THREAD_POOL_EXECUTOR in all versions of Android. You should take a quick look at the source code for AsafTask, don't worry it's tiny.


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

For slightly more concise code, you can use AsafTaskBuilder. This class works in much the sameway as AsafTask, it just has a cut down APIto take advantage of lambda expressions.

One restriction with AsafTaskBuilder is there is no way to pulish progress, so if you need to publish progress during your asynchronous operation, just stick to a plain AsafTask.


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

A conveient way to make this happen is to inject the WorkMode into the enclosing class at construciton time so that it WorkMode.ASYNCHRONOUS can be used for deployed code and WorkMode.SYNCHRONOUS can be used for testing. This method is demonstrated in Example 3.


# Retrofit and the CallProcessor

//TODO


# UI Widgets and Helpers

//TODO
