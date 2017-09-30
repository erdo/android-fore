

# ~~Frequently~~ Occasionally Asked Questions

## 1) Why not put a parameter in the Observer.somethingChanged() method?

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


## 2) Do tell me more about separating view code from everything else...

No one asks me this. I wish they would though, rather than just nodding when I say it's important to keep your data and logic seperate from your views.

Android developers (especially if they have only developed using Android during their career) often have a hard time understanding in practice how to separate view code from everything else (despite universally declaring it to be a good thing).

Unfortunately, right from its inception the Android platform was developed with almost no consideration for data binding or for a separation between view code and testable business logic.

Instead of seperating things horizontally in layers with views in one layer and data in another layer, they seperated things vertically. Each self contained Activity (encorprating UI, data and logic) wrapped up in it's own little reusable component. That's probably why testing was such an afterthougt for years with Android.

Unfortunately, right from its inception the Android platform was developed with almost no consideration for data binding or for a separation between view code and testable business logic. Instead of seperating things horizontally in layers with views in one layer and data in another layer, they seperated things vertically. Each self contained Activity (encorprating UI, data and logic) wrapped up in it's own little reusable component.

>Instead of seperating things horizontally like most UI platforms (views/logic/data), Android decided to separate things vertically (login/take a photo/post it)

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



## 3) When should I use an Observer, when should I use a callback listener?

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
  

## 4) What's the deal with the Controller in MVC, where is that? 

In modern UI frameworks most of the controller work is implemented for you by the framework itself - these are the button click listeners that simply catch user input and send it on to the right place.

Originally a controller might have been a class that accepts mouse clicks at specific pixel co-ordinate, did some collision detection to find out which UI component was clicked, then sent that information on to the appropriate UI component classes for further processing.

Nowadays we just use click listeners or something similar and we don't need to worry much about that.

(Android also lets you use Activities as kind of Controllers by letting you specify callback methods right in the XML for buttons which will end up getting called on whatever activity is hosting that particular view. It's kind of nasty, I'd recommend not using it at all because it encourages (forces) you to get the activity involved in something that it doesn't need to be involved in. If you leave everything out of the Activity then you can use your view in any activity you like, without needing to re-implement all those button call backs for each time.)


## 5) But hey doesn't Android have an official data binding and MVVM solution now?
Hmm. I have to say that I'm generally not too impressed with some of the "helpful" code we get from Google sometimes. Too much of what is published seems like an after thought, or just the latest replacement for the last thing they messed up. Sometimes what they give the Android community is too complicated / badly thought out / rushed / or just not finished.

If it's not good enough, or it's too new to know if it it's good enough, and it's from Google, and I have a better alternative - I'll hapilly skip it. (In the past when I've not done that, I have often regretted it).

Don't get me wrong, I love Android, and the only reason it exists is because Google didn't want to loose out on mobile add revenue when everything moved away from desktops, so good for them! But (and sorry for any Google fans out there) just because Google wrote some new code, doesn't mean it's any good, or even that it is sticking around and people will adopt it and it will become standard.

MVVM is not new, it's been around at least a decade, MVC for 3 decades! The fact that it has taken the google android team half a decade of running a UI platform to realise that MV* is a thing that's actually quite important when writing an app with a UI, well it speaks volumes doesn't it?

Anyway rant over, but the code in this library is (I think) a lot better (of course, otherwise I wouldn't bother using it).


## 6) Syncing the whole view feels wasteful, I'm just going to update the UI components that have changed for efficiency reasons.

Let me guess you're one of those people who only uses ints instead of enums right? or forgoes getters and setters for extra performance? I knew it!

Well apart from the obvious "permature optimisation is the route of all evil" or however that quote goes, you might be in danger of seriously underestimating how fast even the most basic android phone runs nowadays.

### In to the matrix

Before we go any further, if you haven't already, go to developer settings on android and check out the debug tools that let you see the screen updates as they happen.

***WARNING if you're epileptic, maybe skip this part, you will see some incredibly annoying rapid screen flashing as the screen is updated multiple times a second. I'm not epileptic but a few minutes of that makes me feel car sick.***

The first one is **"Show surface updates"** it flashes when part of the screen is being redrawn. You might be surprised just how often the screen is being updated as you use your android device.

The second option you have is **"Show GPU view updates"** this only shows GPU updates and depending on your device you may see this working a lot, or not at all.

Now that you've peeked a little under the hood, you'll be able to appreciate that if you're looking at a single waiting animation (like a standard indeterminate progress bar on Android), the screen (or at least that part of it) will be updating around 30 times a second. Any scrolling of a list view; any background blurring animation; even a blinking cursor will sometimes cause the screen to be redrawn 30 times a second or so. That's how fast it needs to be to trick human eyes into thinking something is moving when it isn't - it's just a sequence of still images.

If you put some logs in the syncView() method you'll also see that it completes pretty quickly, as all of your getters should be returning fast anyway (it should be in the order of a few miliseconds).

In addition, if you are setting a value on a UI element that is the same as the value it already has, it would be a bug in the android framework if it caused a complete re-layout in response (I'm not saying such bugs don't exist, but if you ever get any kind of performance issues with this tecnique, that's the time to measure and see what is happening, but if you follow the guidelines here correctly you almost certianly will never have any problems at all, and what you get in return is unparalleled robustness). Take a look at the simple exmple in the syncView() section for more on this.

If you have a model that is changing in some way that an observer just so happens NOT be interested in, you will end up making a pass through syncView() unecessarily, and in all likely hood you should just not worry about it and be happy with the knowledge that your UI is *definitely* consistent.

If you're going to use the observer pattern to implement a custom animation (which is something that you absolutely can do given the performance of the observer implementation in this library) and you're expecting 50 syncView() passes a second, you *probably* want to seperate that view anyway (and it's associated model), but do it for clean code purposes rather than a misguided idea of improving performance.

