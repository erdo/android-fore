# asaf
Alarmingly Simple Android Framework

## TL;DR


This is a small collection of framework type classes that can help you write a concise and robust commercial grade android application. Used correctly it helps address some of the common problems of android development: **testability**; **lifecycle management**; **UI consistency**; and **memory leaks**. It usually results in a very concise code base and it supports rotation by default. It's also very simple.

## Quick Start

...


//TODO gradle stuff

...



If you haven't coded in this way before it's probably best to take a look at the sample app and literally copy and paste a feature and it's associated UI components (see the **feature/** and **ui/** packages) then change them from there.

In your app you'll need something to inject the feature models into your UI components like the **ObjectGraph** class does in the sample app, if you're already using the **Dagger** library you can just use that instead.

More details below, but essentially you will be writing observable and testable Model classes for all your logic and data, and getting your UI classes to observe these models for any changes so that they can update their views immediately.


## Overall Approach
ASAF is basically a light touch implementation of **MVVM written for Android** using the observer pattern. It could almost be considered **MV** as we don't need to make a distinction between Models and ViewModels. You can use it to implement **MVP** if you wish, but you might find that with this framework you don't really need the **P**.

It also formalises an approach to **simple one way data binding** using a syncView() method that never leaves your view out of sync with your model.

### Models:
There are lots of definitions of the word **Model**. Here we use it to mean anything that is not a View. In practice model classes might have several layers, some contain data and/or logic, they would very likely pass off tasks like network or database access to other layers. The important thing is that none of this should be anywhere near our **View** classes.

In the sample apps, the models are all found in the **feature** package.

A check list of requirements for the model classes, as used in ASAF

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
- By the way, it's very useful to immediately **crash in your model constructor if any caller tries to send you null obects**. Your constructor is your public interface and could be used by anyone. You can help other developers out by immediately crashing here rather than sometime later when the cause might not be so obvious.


### Views:
Views are not just XML layouts, in Android views are composed of the **Activity**, **Fragment** and **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

In short they are no place to put business logic, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted such as:

- loosing data stored in memory (causing null pointers)
- maintaining UI consistency
- guarding against memory leaks

Sound familiar? I have no stats for this but I would guess that those issues account for well over half the bugs present in a typical android app.

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from manage their lifecycle and display the state of whatever model they are interested in. As you'll see in that package, click listeners and references to the UI widgets are in the subclassed Android View classes, and they reference model classes directly from there.


### Model to View comms

The models are observable and the views mainly do the observing.

When a model changes, it's the model's responsibility to notify all the observing classes.

When an observer is told that something changed, it is the observers responsibility to find out what the latest model state is.

More details on this process in the data binding section

### View to Model comms

The button click listeners etc are specified in the view classes and call methods directly on models.


## Data Binding
***AKA - how can I stop getting bug reports about displaying the wrong state in some weird hard to reproduce situation - while also writing less code?***

**One Way Data Binding**: Any changes of state in your underlying model get automatically represented in your view.

So if your shopping basket model is empty: the checkout button on your view needs to be invisible or disabled. And as soon as your shopping basket model has something in it: your checkout button needs to reflect that by being enabled (and obvs, it still needs to work when you rotate the screen)

**Two Way Data Binding**: In addition to the above, the binding goes the other way too. So lets say you are editing your online profile in an editable text view, your view edits will automatically be reflected in your underlying profile model.

Automatic two way data binding turns out to be a bit of a pain in the derriere, and it doesn't seem to be as useful as you might expect. It's also very easy to do for specific cases (just not in the general case).

Anyway I'm going to show you how to do rock solid one way data binding with this library, if it turns out you need some two way data binding you can just do something like this:

	saveChangesButton.setOnClickListener(new View.OnClickListener() {  
            @Override
            public void onClick(View v) {
                myProfile.setText(profileEditText.getText().toString());
            }
        });


### SyncView()

There are a load of different ways of implementing one way data binding. In line with the name of this framework, we are going to use the most simple (but extremely reliable) implementation you can have.

It really all boils down to a single syncView() method, but there are some important implementation details to discuss. The basic philosophy is: if a model being observed changes **in anyway**, then the **entire** view is refreshed. That simplicity is surprisingly powerful so we're going to go into further detail about why, after I've quoted myself to make it seem more profound...

> if a model being observed changes **in anyway**, then the **entire** view is refreshed.

### simple example

Here's an example of how addhoc data binding can get rapidly complicated, especially when you have a lifecycle to deal with.

Let's say you're developing a view for a very basic shopping basket. We need to be able to **add** and **remove** items, and to apply (or not apply) a **10% discount**. The basket model has already been written and has already been nicely unit tested. All we need now is to hook up our basic view to this basket model.


**Step 1)** First we hook up the **add item button** so in the onclick listener we: call basket.addItem(), and then we just call an updateTotalPriceView() method which updates the amount shown in the total field (no point in syncing the whole basket view here).

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
            }
        });

**Step 2)** Then when we hook up the **remove item button** we do something similar: call basket.removeItem(), and the call the updateTotalPriceView() method (again, no point syncing the entire view here)

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
            }
        });

**Step 3)** The designers decide they want to display the **total number of items in the basket** as well as the price, so now we add a updateTotalNumberOfItemsView() method, which does what you think it does. Of course, we need to hook that up with the Add and Remove buttons so that they now both call updateTotalPriceView(); and then updateTotalNumberOfItemsView();

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.removeItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

**Step 4)** Now we get to the **apply discount** checkbox, if the box is checked, the discount is applied, if not there is no discount applied. Remember the model calculations have already been written and tested so what we need in the click listener is: basket.setDiscount(applyDiscount); then updateDiscountView() which just shows the discount that has been applied. We also need to call updateTotalPriceView() as that will have changed, but not updateTotalNumberOfItemsView() because of course discounts have no effect there.

        apply10PercOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean applyDiscount) {
                basket.setDiscount(applyDiscount);
                updateDiscountView();
                updateTotalPriceView();
            }
        });

Here is the psuedo code we end up with for this (very over simplified) case

    Button addItemButton;
    Button removeItemButton;
    CheckBox apply10PercOff;

    TextView totalItems;
    TextView totalDiscount;
    TextView totalPrice;

    private void setupButtonListeners() {

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.removeItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

        apply10PercOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean applyDiscount) {
                basket.setDiscount(applyDiscount);
                updateDiscountView();
                updateTotalPriceView();
            }
        });
    }


    private void updateTotalNumberOfItemsView(){
        totalItems.setText(basket.getTotalItems);
    }

    private void updateDiscountView(){
        totalDiscount.setText(basket.getTotalDiscount);
    }

    private void updateTotalPriceView(){
        totalPrice.setText(basket.getTotalPrice);
    }

And don't forget if we need to rotate this view, all the fields will be out of sync with our model. We could use the sticking plasters that android gives to deal with this problem, but because we have been smart and seperated our model from our view anyway, we don't care about such lifecycle trivialities and we can just re sync everything up like so:

	private void updatePostRotation(){
		updateTotalNumberOfItemsView();
		updateDiscountView();
		updateTotalPriceView();
	}

That's already looking like quite a bit of code, but what if we want to add some more UI details like disabling a checkout button if there is nothing in the basket, or making the total colour red if it is under the minumum card transaction value of $1.

It very quickly starts to become untidy and complicated (which is not what you want in a view class which is not easy to test).

### But that's not the worst problem....
The worst problem with this code is that there is a **bug** in it. Did you spot it?

It's a class of bug related to UI consistency that crops up *all the time* in any code that doesn't have proper data binding, and given that data binding is barely even a thing among most android developers, it's a class of bugs that crops up *all the time* in android apps, even ones that dissable rotation.

I'm guessing you have gone back and spotted the bug by now, but in case you haven't you can recreate it by selecting the discount checkbox first and then adding or removing an item. It's that simple. The add and remove item click listeners will correctly talk to the model, so the model state is correct. However the developer forgot to call updateDiscountView() so this value will be incorrect in the view until the discount checkbox is toggled again.

Very simple views can easily become a complete mess and it's easy to create subtle UI consistency bugs like this. Luckily there is a simple solution and all you have to do is apply it everywhere you have a view.

> if a model being observed changes **in anyway**, then the **entire** view is refreshed.

Using a syncView() for the example above we end up with something like this:


    Button addItemButton;
    Button removeItemButton;
    CheckBox apply10PercOff;

    TextView totalItems;
    TextView totalDiscount;
    TextView totalPrice;

    private void setupButtonListeners() {

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
            }
        });

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.removeItem();
            }
        });

        apply10PercOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean applyDiscount) {
                basket.setDiscount(applyDiscount);
            }
        });
    }

    private void syncView(){
        totalItems.setText(basket.getTotalItems);
        totalDiscount.setText(basket.getTotalDiscount);
        totalPrice.setText(basket.getTotalPrice);
    }

The syncView() gets called by the observer which is triggered whenever **any** state of the model changes. It's also called on rotation. If you want to add any more states it's easy and clean and totally consistent if they are set inside the syncView() method:

    private void syncView(){
    	checkoutButton.setEnabled(basket.isAboveMinimum());
    	totalPrice.setColour(basket.isAboveMinimum() ? black : red);
    	removeButton.setEnabled(basket.getTotalItems>0);
 		totalItems.setText(basket.getTotalItems);
 		totalDiscount.setText(basket.getTotalDiscount);		totalPrice.setText(basket.getTotalPrice);
	}

### Writing an effective syncView() method


The important thing about a syncView() method is that it must set an affirmative state for every view element property that you are interested in. What that means is that where there is an **if** there must always be an **else** for each property.

It's not good enough to just set a button as **disabled** if a total is 0 or less. You must also set that button as **enabled** if the total is greater than 0. If you don't set an affirmative step for both the positive and negative scenarios, then you run the risk of a syncView() call not setting any state, which means that the result will be undeterministic (it will be whatever state it had previously).

So don't do this:

	if (basket.isBelowMinimum()){
		checkoutButton.setEnabled(false);
		totalPrice.setColour(red);
	}
	
At the very least you must do this:
	
	if (basket.isBelowMinimum()){
		checkoutButton.setEnabled(false);
		totalPrice.setColour(red);
	}else{
		checkoutButton.setEnabled(true);
		totalPrice.setColour(black);
	}

But you'll find that by focusing on the property first rather than the condition, you can get some extremely clean code using the elvis operator like so:

	checkoutButton.setEnabled(!basket.isBelowMinimum());
	totalPrice.setColour(basket.isBelowMinimum() ? red : black);



## Adapters

Ahh adapters, I miss the good old days when all you had to do was call notifyDataSetChanged(). And the best place to call it is from inside the syncView() method:

    public void syncView() {

		// set enabled states and visibilities etc
		...
		
        adapter.notifyDataSetChanged();
    }

In this way you let your adapters piggy back on the observer which you have already setup for your view (which is what calls syncView()).

(You could also add your adapter as an observer on the model directly, but doing it like that usually causes problems because you will also need to find a way to remove it correctly.)

If you're not overly concerned with list animations I would continue to call notifyDataSetChanged anyway (yes it is marked as deprectated, but the alternative methods that android is offering are so difficult to implement correctly that I strongly suspect they will never be able to remove the original adapter.notifyDataSetChanged() method from the API)

### list animations

So onwards and upwards if you want list animations on android, they make you work quite hard for it.

In order to get animations, you need to tell the adapter what kind of change actually happened, what rows were added or removed etc. This is one case in particular that it was so tempting to just add a parameter to the ASAF observable. It still wasn't worth it though.

//TODO


## Cursor Loaders

Cursor loaders are interesing, underneath the hood they use a standard observer pattern to propogate their changes to a view just as this library does. It works very nicely, the only criticism I have is that (as with so much Android code) it forces you down a path of adding model code to the Activity and Fragment classes.

//TODO


## Identifying Problems / How to smash code reviews
As mentioned, it can take a little bit of effort to understand how to use this library properly at first, so here is a list of warning signs you can use to highlight potentially incorrect code (this list is especially helpful for code reviews - these are all things I have seen from developers who have just been introduced to this library).


1) **Any code setting or updating view states that is not inside the syncView() method**. Example: "clickListener -> setDisabled". That's usually an indication that the developer might not understand why syncView() is designed like it is, and will almost certainly result in hard to identify UI consistency bugs when screens are rotated etc. Point them to the data binding section where it talks about syncView().

2) **Activities and Fragments that have more than a few lines of code in them**. Sometimes there are good reasons for putting code in Activities and Fragments, setting up bundles and intents for example, but you should be immediately suspicious of any errant code that gets into these classes. Often this code can be moved to a model class, safely away from tricky lifecycle issues and where it can also be more easily tested. (If you value your sanity and that of your team, you should make sure that there are absolutely **no** AsyncTask instances or networking code in any Activity or Fragment classes at all. Ever.)

3) **Code in a Fragment that casts it's parent activity and then calls that activity for further processing**. Again sometimes that is appropriate, but unfortunately it's a very common pattern that is often misused. The idea of course is to let a Fragment communicate with an Activity in a safe way. When this technique is used as a way to access fuctionality written in the parent activity which should really have been written in a model class in the first place, it just acts as a sticking plaster for a problem that should never have existed in the first place. The answer of course is to put that code in a model, inject that model into the fragment and let the fragment access it directly, that totally removes the dependence on the host Activty and removes a lot of boiler plate in the process.

4) **Adding or removing observers outside of android lifecycle methods**. I'm not saying there is never a good reason to do that (particularly if you want to set up one long living model to observe the state of another long living model). But it is a bit unusual and might warrant a rethink. It's usually a mistake (and a cause of memory leaks)

5) Wherever you see an **addObserver()** it's always worth checking that you can see the associated **removeObserver()** call to make sure references are being cleaned up and memory isn't being leaked.

6) **Any change of state (usually a setter method) in an observable model that doesn't end with a call to notifyObservers()**. Even if it's not necessary for the current implementation, by not notifying the observers here, we now have a model that only works in certain (undocumented) circumstamces. If someone else comes along and wants to observe your model and does not get a notification as expected when some state changes, something will break.

7) **Any getter method that does more than pass back an in-memory copy of the data asked for**. In order for the databinding to be performant, we want any getter methods to return fairly quickly. Try to front load any processing in the setters rather than the getters.

8) **Any observers that do anything other than sync their entire view** are usually (but not always) incorrect. Generally the observer just does one thing (sync the view), and this means you can use the same instance of that observer to register with several different models in the same view (it's another reason for not doing what's discussed in OAQ 1)

9) **Code or tests that makes assumptions about the number of times syncView() will be called** This is pretty important, you can't fire one off events based on syncView() being called (like starting an activity for example), because you are depending on being notifed by the model an exact number of times. The deal is that whenever something (anything) changes in the model, you will be notified. But you maybe be notified more than you expect, especially if the model is refatored at a later date to add new features or in some way the internal implementation of the model changes. In order to be robust, your syncView must make no assumptions about the number of times it may or may not be called.

10) **A syncView() that is more than 5-10 lines long and/or doesn't have one line to set an affirmative value for each propery of each UI element you are concered with**. Take a look at the how to write a good syncView() method under the data binding section.

11) **Any click listeners or text change listeners should generally be talking directly to model classes, or asking for navigation operations** for example: MyActivity.startMe(getContext). Occasionally it's useful for listeners to directly call syncView() to refresh the view (when an edit text field has changed for example). What they generally shouldn't be doing is accessing other view components like fragments or activites and checking their state in some way, if you follow this code it generally ends up calling a model class somewhere down the line, in which case the model class should just be called directly (you get your model references to any view class using dependency injection).



## Occasionally Asked Questions

### 1) Why not put a parameter in the Observer.somethingChanged() method?

If I had a dollar for everyone who asked me this question! (I would have, about $4)

There is a very good reason why we don't have a parameter here, but it is tricky to get your head around why. We could use a generic maybe and let a model send data or a message directly to the observers. Sending data like that might at first seem like an easy, convenient thing to do, but in my experience it basically always ends up in a world of maintenance pain.

Adding a message parameter here would let client code use this observer like some kind of messenger thing or an event bus. While that could be a perfectly valid thing to do for the specific situation you find yourself in, it almost always ends up destroying the long term maintainability of the code base.

(Adding a parameter here has been tried by yours truly in many different projects over the years by the way, it always ends up being removed resulting in a considerably cleaner code base, so this has been the approach now for a number of years and it seems to work very well).

One reason (but not the only one) is that often, different views or other Observers will want different things from a model and as the code evolves, models slowly end up having to support many different flavoured observables all with different parameter requirements. It balloons the amount of code that needs to be written. It also leads developers down the wrong path regarding data binding and ensuring consistency when your application is rotated etc (see more on that in
the data binding section).

Passing a parameter here is also the "obvious" thing to do - which means, if it's an option, it will always be chosen by the less experienced developers in the team. Apart from giving you code review headaches, letting a new developer do that would prevent that developer from learning the more powerful way to use this framework - which, although extremely simple, does take a while to get your head around.

This library is as valuable for what you can't do with it, as it is for what you can do with it. The API is designed with robustness and long term maintainability in mind, especially in an environment where many people of different experience levels will be developing the code base.


> This library is as valuable for what you can't do with it, as it is for what you can do with it 



Try to use these observers to just notify client code of changes to the model's state (once the client code has been told there are changes, it can use fast returning getters on the model to find out what actually happened, redraw it's state, or whatever - if this isn't straight forward then the models you have implemented probably need to be refactored slightly). For some, this is a strange way to develop, but once you've done it a few times and you understand it, the resulting code is rock solid and very compact.

If you want a library that lets you send data in these observables, you should look at RxJava which gives you enough rope to hang yourself with (and then some). In some ways that library is similar to this one (both are an implementation of the Observer pattern and let you subscribe to notifications from data sources) but RxJava has a huge and flexible API, and implements many cool features (none of which I have so far ever required - even the most complicated android app is still just some logic and data with a UI put on top of it. Once you remove the crazy, turns out it's not rocket science after all). Because ASAF has been private for so long, the API has actually gotten smaller over the years rather than larger (as I and the other developers realised we were trying to be too clever).


> Once you remove the crazy, turns out it's not rocket science after all


### 2) Do tell me more about separating view code from everything else...

No one asks me this. I wish they would though, rather than just nodding when I say it's important to keep your data and logic seperate from your views.

Android developers (especially if they have only developed using Android during their career) often have a hard time understanding in practice how to separate view code from everything else (despite universally declaring it to be a good thing).

Unfortunately, right from its inception the Android platform was developed with almost no consideration for data binding or for a separation between view code and testable business logic.

Instead of seperating things horizontally in layers with views in one layer and data in another layer, they seperated things vertically. Each self contained Activity (encorprating UI, data and logic) wrapped up in it's own little reusable component. That's probably why testing was such an afterthougt for years with Android.

> Unfortunately, right from its inception the Android platform was developed with almost no consideration for data binding or for a separation between view code and testable business logic. Instead of seperating things horizontally in layers with views in one layer and data in another layer, they seperated things vertically. Each self contained Activity (encorprating UI, data and logic) wrapped up in it's own little reusable component. 

Anyway a lot of the complication of Android development comes from subsequent attempts to fix issues that would not even exist were there a clearer separation here.

A classic mistake (one which I think even the original developers of Android fell in to) is to think of the view in Android as just being the XML layouts. Views are not just these XML layouts, in Android views are composed of the *Activity, Fragment and View classes. These classes:

* are all ephemeral
* are tightly coupled to the context (including the physical
characteristics of the display)
* are slow to test.

In short they are no place to put business logic, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted such as:

* loosing data stored in memory (causing null pointers)
* maintaining UI consistency
* guarding against memory leaks

Despite this obvious problem, think about how many Android apps you've encountered (including code samples from Google) that fill their Activity and Fragment classes with various pieces of logic or networking code. And think about how much additional code is then required to deal with a simple screen rotation (or worse, how many apps simply disable screen rotation because of the extra headache). Sometimes even smart developers can fail to see the wood for the trees.

> Sometimes even smart developers can fail to see the wood for the trees

Fortunately with a few basic rules and techniques embodied in this library you can almost completely remove these considerations from your Android project, leaving your Activity and Fragment classes nearly bare. What you will be left with is a remarkably performant and robust framework which supports screen rotation with ease and leaves you to get on with the fun stuff.

*In Android the Activity classes also occasionally act like a Controller



### 3) When should I use an Observer, when should I use a callback listener?

An observer pattern is not always going to be suitable for what you want to do. In
particular, if you are looking to receive a one off success/fail result from a model as a direct result of the model performing some operation (like a network request) then a regular call back will probably serve you better. In this case the succes or failure of the network call does not alter any fundamental state of the model, so a callback / listener is ideal.

for example:

   	model.doStuffOnAThread(new ResultListener{
     	@Override
     	public void success(){
        	//do next thing
     	}
     	@Override
     	public void fail(UserMessage reason){
        	showMessage(reason);
     	}
   	});

You can use both patterns in the same model with no problem of course, in the example above the model could have a busy state that changes from false to true and back to false again during a network request so that view code can redraw itself as showing a busy swirly if appropriate. That would be better managed using an observer pattern as follows:

  	public void doStuffOnAThread(final ResultListener resultListener){
  	
    	busy = true;
    	notifyObservers();
    	
    	startAsyncOperation(new FinishedListener(){
        	@Override
        	public void finished(){
            	busy = false;
            	resultListener.success();
            	notifyObservers();
        	}
    	});
    }
  

As a result of the notifyObservers() call, any observers would get notified and then synchronise their views, calling isBusy() to establish the current state of the model. This particular busy state would then be used to show a swirly or disable a submit button while the model is currently busy with something. Because this behaviour is completely decoupled from the View layer, this state would remain accurate even if the app is rotated during the network request providing syncView() is called on re-construction of the view layer.
  
  	public boolean isBusy(){
    	return busy;
  	}


Alternatively the network call might be fetching some data for the model as in the following case:

	private String name = "Ms Default";

  	public void fetchName(){

		busy=true;
    	notifyObservers();

    	startAsyncNameFetch(new NameFetchListener(){
        	@Override
        	public void success(String nameFromServer){
        		logger.i("success");
        		name = nameFromServer;
            	complete()
        	}
        	@Override
     		public void fail(ErrorMessage reason){
        		logger.i(reason);
        		complete()
     		}
    	});
    }
    
    private void complete(){
		busy = false;
        notifyObservers();
    }
    
    public String getName(){
    	return name;
  	}
  

### 4) What's the deal with the Controller in MVC, where is that? 

In modern UI frameworks most of the controller work is implemented for you by the framework itself - these are the button click listeners that simply catch user input and send it on to the right place.

Originally a controller might have been a class that accepts mouse clicks at specific pixel co-ordinate, did some collision detection to find out which UI component was clicked, then sent that information on to the appropriate UI component classes for further processing.

Nowadays we just use click listeners or something similar and we don't need to worry much about that.

(Android also lets you use Activities as kind of Controllers by letting you specify callback methods right in the XML for buttons which will end up getting called on whatever activity is hosting that particular view. It's kind of nasty, I'd recommend not using it at all because it encourages (forces) you to get the activity involved in something that it doesn't need to be involved in. If you leave everything out of the Activity then you can use your view in any activity you like, without needing to re-implement all those button call backs for each time.)


### 5) But hey doesn't Android have an official data binding solution now?
Uh huh. Good luck with that.


### 6) Syncing the whole view feels wasteful, I'm just going to update the UI components that have changed for efficiency reasons.

Let me guess you're one of those people who only uses ints instead of enums right? or forgoes getters and setters for extra performance? I knew it!

Well apart from the obvious "permature optimisation is the route of all evil" or however that quote goes, you might be in danger of seriously underestimating how fast even the most basic android phone runs nowadays.

#### In to the matrix

Before we go any further, if you haven't already, go to developer settings on android and check out the debug tools that let you see the screen updates as they happen.

***WARNING if you're epileptic, maybe skip this part, you will see some incredibly annoying rapid screen flashing as the screen is updated multiple times a second. I'm not epileptic but a few minutes of that makes me feel car sick.***

The first one is **"Show surface updates"** it flashes when part of the screen is being redrawn. You might be surprised just how often the screen is being updated as you use your android device.

The second option you have is **"Show GPU view updates"** this only shows GPU updates and depending on your device you may see this working a lot, or not at all.

Now that you've peeked a little under the hood, you'll be able to appreciate that if you're looking at a single waiting animation (like a standard indeterminate progress bar on Android), the screen (or at least that part of it) will be updating around 30 times a second. Any scrolling of a list view; any background blurring animation; even a blinking cursor will sometimes cause the screen to be redrawn 30 times a second or so. That's how fast it needs to be to trick human eyes into thinking something is moving when it isn't - it's just a sequence of still images.

If you put some logs in the syncView() method you'll also see that it completes pretty quickly, as all of your getters should be returning fast anyway (it should be in the order of a few miliseconds).

In addition, if you are setting a value on a UI element that is the same as the value it already has, it would be a bug in the android framework if it caused a complete re-layout in response (I'm not saying such bugs don't exist, but if you ever get any kind of performance issues with this tecnique, that's the time to measure and see what is happening, but if you follow the guidelines here correctly you almost certianly will never have any problems at all, and what you get in return is unparalleled robustness). Take a look at the simple exmple in the syncView() section for more on this.

If you have a model that is changing in some way that an observer just so happens NOT be interested in, you will end up making a pass through syncView() unecessarily, and in all likely hood you should just not worry about it and be happy with the knowledge that your UI is *definitely* consistent.

If you're going to use the observer pattern to implement a custom animation (which is something that you absolutely can do given the performance of the observer implementation in this library) and you're expecting 50 syncView() passes a second, you *probably* want to seperate that view anyway (and it's associated model), but do it for clean code purposes rather than a misguided idea of improving performance.


License
-------

    Copyright 2017 early.co

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

