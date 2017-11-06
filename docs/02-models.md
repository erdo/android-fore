
# Models
There are lots of definitions of the word **Model**. Here we use it to mean anything that is not a View. In practice model classes might have several layers, some contain data and/or logic, they would very likely pass off tasks like network or database access to other layers. The important thing is that none of this should be anywhere near our **View** classes.

In the sample apps, the models are all found in the **feature** package.

Here's an example: [FruitFetcher.java](https://github.com/erdo/asaf-project/blob/master/exampleretrofit/src/main/java/foo/bar/example/asafretrofit/feature/fruit/FruitFetcher.java)

## Writing a Basic Model

If you crack how to write a good model, using it in the rest of your app should be a piece of cake.

You'll see that in all the sample apps, the models have been written with the assuption that all the methods are being accessed on a single thread (which for a live app would be the UI thread).

Writing your app so that it operates on a single thread by default is a *very* helpful short cut to take by the way, it considerably simplifies your model code.

When you need to pop onto another thread, do it explicitly with something like an [AsafTaskBuilder](/04-more.html#asaftaskbuilder) for example, and then pop back on to the UI thread when you are done. The ASAF ASYNCHRONOUS Observables notify on the UI thread anyway, so you don't need to do any extra work when you want to update the UI.

If you're already comfortable writing model code skip down to the [check list](#model-check), check out a [few](https://github.com/erdo/asaf-project/blob/master/exampleretrofit/src/main/java/foo/bar/example/asafretrofit/feature/fruit/FruitFetcher.java) [examples](https://github.com/erdo/asaf-project/blob/master/examplethreading/src/main/java/foo/bar/example/asafthreading/feature/counter/CounterWithProgress.java) from the sample apps and you should be good to go.

### For more detail, read on

Writing model code gets easier with practice but as a starting point you could do a lot worse than to start by modelling real world concepts. That should also make it easier for other developers to understand what you are aiming for.

For example if you have a printer attached to your android app that you need to use, you probably want a *Printer* class for a model.

(In ASAF, almost all the models end up having global scope, if for some reason you have a model that you want to restrict the scope of, you can use a Factory class to get a local instance, or use a library like Dagger. Just don't call "new" in a View layer class because then you won't be able to mock it out for tests.)

In this case it makes sense to give our *Printer* model global application scope because a) the real printer is right there by your application ready for printing no matter what part of the app you are in and b) it's easy to do - also c) at some point the designers will probably want to be able to print various things, from various parts of the app and there is no point in limiting ourselves here.

There will also only be one instance of the *Printer* model, because: there is only one real printer.

```
public class Printer {
}
```

The *Printer* model might have a boolean that says if it's busy printing or not. It might have a boolean that says that is has run out of paper. It might have a number that tells you how many items it has in the queue at the moment. All of this state should be available via getters for any View or other Observer code to access.

```
public class Printer {
    
    private boolean isBusy = false;
    private boolean hasPaper = true;
    private int thingsLeftToPrint;
    
    public boolean isBusy() {
        return isBusy;
    }
    
    public boolean isHasPaper() {
        return hasPaper;
    }
    
    public int getThingsLeftToPrint() {
        return thingsLeftToPrint;
    }
}
```


The *Printer* class will have some public methods like *print(Page pageToPrint)* for example, and as this will take a while, that call will need to be asynchronous and that means you should probably have a listener callback that will get called when it is finished: 

```
public class Printer {

    private boolean isBusy = false;
    private boolean hasPaper = true;
    private int numPagesLeftToPrint;

    
    public void printThis(Page pageToPrint, CompleteCallBack completeCallBack){

        isBusy = true;
        numPagesLeftToPrint++;

        //...do the printing asynchronously, then once back on the UI thread:

        isBusy = false;
        numPagesLeftToPrint--;

        completionCallBack.complete();
    }


    public boolean isBusy() {
        return isBusy;
    }

    public boolean isHasPaper() {
        return hasPaper;
    }

    public int getNumPagesLeftToPrint() {
        return numPagesLeftToPrint;
    }
}
```

The *Printer* model will need USB connection stuff and maybe a Formatter that will let you format your page appropriately for the type of printer you have (or something). We'll add these dependencies as constuctor arguments, and we are going to deliberately crash if some crazy dev mistakenly tries to send us null values here (nulls will never work here so we may as well crash immediately and obviously). Annotating parameters to not be null is not really enough because it's only a compile time check and can still let things slip through.

```
private final USBStuff usbStuff;
private final Formatter formatter;

public Printer(USBStuff usbStuff, Formatter formatter) {
    this.usbStuff = Affirm.notNull(usbStuff);
    this.formatter = Affirm.notNull(formatter);
}
```


We're nearly there. If we want to involve this *Printer* model in the view layer we will probably want it to be Observable so that any observing view will be notified whenever it changes (and therefore the view needs to be refreshed).

The quickest way to do that is to extend ObservableImp:

```
public class Printer extends ObservableImp {
```
    
Next we need to make sure that the observers are notifed each time the *Printer* model's state changes, and we do that by calling **notifyObservers()** whenever that happens:

```
isBusy = true;
numPagesLeftToPrint++;
notifyObservers();  //ASAF Observable will take care of the rest
```

The asynchronous printing that we've glossed over so far could be implemented with an [AsafTaskBuilder](/04-more.html#asaftaskbuilder) like so:

```
new AsafTaskBuilder<Void, Void>(workMode)
        .doInBackground(new DoInBackgroundCallback<Void, Void>() {
            @Override
            public Void doThisAndReturn(Void... input) {

                //...do the printing

                return null;
            }
        })
        .onPostExecute(new DoThisWithPayloadCallback<Void>() {
            @Override
            public void doThis(Void result) {

                //back on the UI thread

                isBusy = false;
                numPagesLeftToPrint--;
                notifyObservers();

                completeCallBack.complete();
            }
        })
        .execute((Void) null);
```

Taking advantage of lambda expressions this becomes:

```
new AsafTaskBuilder<Void, Void>(workMode)
        .doInBackground(input -> {

            //...do the printing

            return null;
        })
        .onPostExecute(result -> {

            //back on the UI thread

            isBusy = false;
            numPagesLeftToPrint--;
            notifyObservers();

            completeCallBack.complete();
        })
        .execute((Void) null);
```

Here's what we might end up with for a rough *Printer* model:

```
public class Printer extends ObservableImp {

    private final USBStuff usbStuff;
    private final Formatter formatter;
    private final WorkMode workMode;

    private boolean isBusy = false;
    private boolean hasPaper = true;
    private int numPagesLeftToPrint;


    public Printer(USBStuff usbStuff, Formatter formatter, WorkMode workMode) {
        super(workMode);
        this.usbStuff = Affirm.notNull(usbStuff);
        this.formatter = Affirm.notNull(formatter);
        this.workMode = Affirm.notNull(workMode);
    }

    public void printThis(Page pageToPrint, final CompleteCallBack completeCallBack) {

        if (isBusy){
            completeCallBack.fail();
            return;
        }

        isBusy = true;
        numPagesLeftToPrint++;
        notifyObservers();

        new AsafTaskBuilder<Void, Void>(workMode)
                .doInBackground(input -> {

                    //...do the printing

                    return null;
                })
                .onPostExecute(result -> {

                    //back on the UI thread

                    isBusy = false;
                    numPagesLeftToPrint--;
                    notifyObservers();

                    completeCallBack.complete();
                })
                .execute((Void) null);

    }
    
    public boolean isBusy() {
        return isBusy;
    }

    public boolean isHasPaper() {
        return hasPaper;
    }

    public int getNumPagesLeftToPrint() {
        return numPagesLeftToPrint;
    }

}
```

Obviously that doesn't work yet, we've ignored numPagesLeftToPrint and the printing details, but you get the idea.

There is something important that snuck in to that version though: The **WorkMode** parameter tells the Observable implementation *ObservableImp* how you want your notifications to be sent, it's also being used by the AsafTaskBuilder. Usually you will pass WorkMode.ASYNCHRONOUS here.

When you construct this *Printer* model for a test though, along with mocking the USBStuff, you will pass in WorkMode.SYNCHRONOUS as the contructor argument. SYNCHRONOUS will have the effect of making all the asynchronous code run in sequence so that testing is super easy.

Take a look at how the CounterWithLambdas model in sample app 2 is [tested](https://github.com/erdo/asaf-project/blob/master/examplethreading/src/test/java/foo/bar/example/asafthreading/feature/counter/CounterWithLambdasTest.java) for example.

***NB: to make your view code extra clean, ASYNCHRONOUS notifications from an Observable in ASAF are always sent on the UI thread, so there is no need to do any thread hopping to update a UI.***


Take a look at the check list below and then head over to the [Data Binding](/asaf-project/03-databinding.html#shoom) section where we tie it all together.


## <a name="model-check"></a> Model Checklist

For reference here's a check list of recommendations for the model classes, as used in ASAF. Once you've had a go at writing one you can come back here to double check you have everything down:

- The model classes should **know nothing about android lifecycle methods**
- In fact **the less the models knows about Android the better**
- **Avoid referencing Contexts** from your model if you can, although sometimes the design of Android makes this awkward
- **Prefer referencing Application over Context or Activity** if you have a choice, as that reduces the chance of a memory leak
- The model **shouldn't know anything about View classes**, Fragments or specific Activities.
- The model's current state at any point in time is typically exposed by getters. These are used by the View classes to ensure they are displaying the correct data, and by the test classes to ensure the model is calculating its state correctly.
- The **getters must return quickly**. Don't do any complicated processing here, just return data that the model should already have. i.e. front load the processing and do the work in the setters not the getters
- When any data in your model changes, inside the model code call **notifyObservers()** after the state has changed.
- The models should make good use of dependency injection (via constructor arguments or otherwise). A good way to check this is to look for the **new** keyword anywhere in the model's code. If you see **new** anywhere, then you have a dependency that is not being injected and will be difficult to mock for a test. Android's AsyncTask has this problem, but ASAF's [AsafTask](/asaf-project/04-more.html#asaftask) goes a long way to working around this as does [AsafTaskBuilder](/asaf-project/04-more.html#asaftaskbuilder)
- Written in this way, the models will already be testable but it's worth highlighting **testability** as a specific goal. The ability to thouroughly test model logic is a key part of reducing unecessary app bugs.
- If the models are to be observable, they can do this in one of 2 ways. They may simply extend from **ObservalbleImp** or they can implement the **Observable interface** themselves, passing the addObservable() and removeObservable() method calls to an ObservableImp that they keep a reference to internally.
- Do check out [When should I use an Observer, when should I use a callback listener?](/asaf-project/06-faq.html#observer-listener) in the FAQs to double check you're making the right choice for your model.
- By the way, it's very useful to immediately **crash in your model constructor if any caller tries to send you null obects**. Your constructor is your public interface and could be used by anyone. You can help other developers out by immediately crashing here rather than sometime later when the cause might not be so obvious.
