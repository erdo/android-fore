
# Models
There are lots of definitions of the word **Model**. Here we use it to mean anything that is not a View. In practice model classes might have several layers, some contain data and/or logic, they would very likely pass off tasks like network or database access to other layers. The important thing is that none of this should be anywhere near our **View** classes.

In the sample apps, the models are all found in the **feature** package.

## Writing a Basic Model

How you write your models will have quite a big impact on how easy it is to use within your app. It gets easier with practice but as a starting point you could do a lot worse than to start by modelling real world concepts. That should also make it easier for other developers to understand what you are aiming for.

For example if you have a printer attached to your android app that you need to use, you probably want a *Printer* class for a model, that *Printer* model probably has global application scope because the real printer is right there by the app ready for printing no matter what part of the app you are in. There will also only be one instance of the *Printer* model, because: there is only one real printer.

    public class Printer {
    }

The *Printer* model might have a boolean that says if it's busy printing or not. It might have a boolean that says that is has run out of paper. It might have a number thats tell you how many items it has in the queue at the moment. All of this state should be available via getters for any View or other code to access.

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


The *Printer* class will have some public methods like *print(Page pageToPrint)* for example, and as this will take a while, that call will need to be asynchronous and that means you should probably have a listener callback that will get called when it is finished: 


    public class Printer {

        private boolean isBusy = false;
        private boolean hasPaper = true;
        private int numPagesLeftToPrint;

    
        public void printThis(Page pageToPrint, CompletionCallBack completionCallBack){

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


The *Printer* model will need USB connection stuff and maybe a Formatter that will let you format your page appropriately for the type of printer you have (or something). We'll add these dependencies as constuctor arguments, and we are going to deliberately crash if some crazy dev mistakenly tries to send us null values here (nulls will never work here so we may as well crash immediatley and obviously). (Annotating parameters to not be null is not really enough because it's only a compile time check and can still let things slip through)

        private final USBStuff usbStuff;
        private final Formatter formatter;

        public Printer(USBStuff usbStuff, Formatter formatter) {
            this.usbStuff = Affirm.notNull(usbStuff);
            this.formatter = Affirm.notNull(formatter);
        }


We're nearly there. If we want to involve this *Printer* model in the view layer we will probably want it to be observable so that any observing view will be notified whenever it changes (and therefore the view needs to be refreshed).

The quickest was to do that is to extend ObservableImp:

    public class Printer extends ObservableImp{
    
Next we need to make sure that the observers are notifed each time the *Printer* model's state changes, and we do that by calling **notifyObservers()** whenever that happens

            isBusy = true;
            numPagesLeftToPrint++;
            notifyObservers();
            
Here's what we end up with for our fake *Printer* model:


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
    
        public void printThis(Page pageToPrint, final CompletionCallBack completionCallBack) {

            isBusy = true;
            numPagesLeftToPrint++;
            notifyObservers();


            new AsyncTaskWrapper<Void, Void, Void>(workMode) {

                @Override
                protected Void doInBackground(Void... params) {

                    //...do the printing
                    
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    //back on the UI thread
                    
                    isBusy = false;
                    numPagesLeftToPrint--;
                    notifyObservers();

                    completionCallBack.complete();
                }
            }.executeWrapper();

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


There are a few things that snuck in to the final version: The WorkMode parameter tells the observable implementation *ObservableImp* how you want your notifications to be sent. Usually you will pass WorkMode.ASYNCHRONOUS here.

When you construct this *Printer* model for a test though, along with mocking the USBStuff, you will pass in WorkMode.SYNCHRONOUS as the contructor argument. SYNCHRONOUS will have the effect of making all the asynchronous code run in sequence so that testing is super easy.

That's bascially what the AsyncTaskWrapper is helping you to do, if you pass SYNCHRONOUS instead of ASYNCHRONOUS here then onPreExecute(), doInBackground(), onPostExecute() will all be called sequentially as if the AsyncTask was just normal code.

***NB: to make your view code extra clean, ASYNCHRONOUS notifications from an Observable in ASAF are always sent on the UI thread, so there is no need to do any thread hopping to update a UI.***

When you are writing your own model, it's worth reviewing the section below called "When should I use an Observer, when should I use a callback listener?" making an inappropriate choice here will get you into an untold mess.


## Model Checklist

For reference here's a check list of my recommendations for the model classes, as used in ASAF. Once you've had a go at writing one you can come back here to double check you have everything down:

- The model classes should **know nothing about android lifecycle methods**
- In fact **the less the models knows about Android the better**
- **Avoid referencing Contexts** from your model if you can, although sometimes the design of Android makes this awkward
- **Prefer referencing Application over Context or Activity** if you have a choice, as that reduces the chance of a memory leak
- The model **shouldn't know anything about View classes**, Fragments or specific Activities.
- The model's current state at any point in time is typically exposed by getters. These are used by the View classes to ensure they are displaying the correct data, and by the test classes to ensure the model is calculating its state correctly.
- The **getters must return quickly**. Don't do any complicated processing here, just return data that the model should already have. i.e. front load the processing and do the work in the setters not the getters
- When any data in your model changes, inside the model code call **notifyObservers()** after the state has changed.
- The models should make good use of dependency injection (via constructor arguments or otherwise). A good way to check this is to look for the **new** keyword anywhere in the model's code. If you see **new** anywhere, then you have a dependency that is not being injected and will be difficult to mock for a test. Android's AsyncTask has this problem, but ASAF's AsyncTaskWrapper goes a long way to working around this.
- Written in this way, the models will already be testable but it's worth highlighting **testability** as a specific goal. The ability to thouroughly test model logic is a key part of reducing unecessary app bugs.
- If the models are to be observable, they can do this in one of 2 ways. They may simply extend from **ObservalbleImp** or they can implement the **Observable interface** themselves, passing the addObservable() and removeObservable() method calls to an ObservableImp that they keep a reference to internally.
- Do check out "When should I use an Observer, when should I use a callback listener?" in the OAQs. to double check your making the right choice for your model.
- By the way, it's very useful to immediately **crash in your model constructor if any caller tries to send you null obects**. Your constructor is your public interface and could be used by anyone. You can help other developers out by immediately crashing here rather than sometime later when the cause might not be so obvious.
