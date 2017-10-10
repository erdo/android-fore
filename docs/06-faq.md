

# ~~Frequently~~ Occasionally Asked Questions

## 1) Why not put a parameter in the Observer.somethingChanged() method?

If I had a dollar for everyone who asked me this question! (I would have, about $4)

There is a very good reason why we don't have a parameter here, but it is complicated, so stay with me. We could use a generic maybe and let a model send data or a message directly to the observers. Sending data like that might at first seem like an easy, convenient thing to do, but in my experience it basically always ends up in a world of maintenance pain.

Adding a parameter here would let client code use the observer like some kind of messenger thing or an event bus. While that could be a perfectly valid thing to do for the specific situation you find yourself in, it almost always ends up destroying the long term maintainability of the code base.

(Adding a parameter here has been tried by yours truly in many different projects over the years by the way, it always ends up being removed resulting in a considerably cleaner code base, so this has been the approach now for a number of years and it seems to work very well).

One reason (but not the only one) is that often, different views or other Observers will want different things from a model and as the code evolves, models slowly end up having to support many different flavoured observables all with different parameter requirements.

Another reason is that there will be views or other Observables that are interested in more than one model, and if those models all have different observable interfaces, all those interfaces will need to be implemented and managed, rather than just using the same one. It balloons the amount of code that needs to be written. It also leads developers down the wrong path regarding data binding and ensuring consistency when your application is rotated etc (see more on that in
the [data binding](/asaf-project/03-databinding.html) section).

*[Quick example, [this view](https://github.com/erdo/asaf-project/blob/master/examplethreading/src/main/java/foo/bar/example/asafthreading/ui/counter/CounterView.java) is driven by [these](https://github.com/erdo/asaf-project/blob/master/examplethreading/src/main/java/foo/bar/example/asafthreading/feature/counter/CounterWithLambdas.java) [two](https://github.com/erdo/asaf-project/blob/master/examplethreading/src/main/java/foo/bar/example/asafthreading/feature/counter/CounterWithProgress.java) models. If each model had a different parameter in its somethingChanged() method, the view would need to implement two different observer callbacks - now what if the view was interested in 5 different models? (which would not be a problem at all for ASAF by the way): The view would need to implement and manage a whole bunch of different observers, or you would need a super model that wrapped all that and presented one observable interface to the view, i.e. extra code, extra tests, no benefit]*

Passing a parameter here is also the "obvious" thing to do - which means, if it's an option, it will always be chosen by the less experienced developers in the team. Apart from giving you code review headaches, letting a new developer do that would prevent that developer from learning the more powerful way to use this framework - which, although extremely simple, can take a while to get your head around.

This is one case where ASAF is stopping you from making an easy but horrible archiectural mistake. The library is as valuable for what you can't do with it, as it is for what you can do with it.


> This library is as valuable for what you can't do with it, as it is for what you can do with it 


Try to get comfortable using these observers to just notify observing client code of any (unspecified) changes to the model's state (once the observing client code has been told there are changes, it can use fast returning getters on the model to find out what actually happened, redraw it's state, or whatever - if this isn't straight forward then the models you have implemented probably need to be refactored slightly). For some, this is a strange way to develop, but once you've done it a few times and you understand it, the resulting code is rock solid and very compact.

If you want a library that lets you send data in these observables, you should look at RxJava which gives you enough rope to hang yourself with (and then some). Both libraries are an implementation of the Observer pattern and let you subscribe to notifications from data sources, but RxJava has a huge and flexible API allowing you to be very clever indeed (which I think is a major problem with it).

Though we like to pretend otherwise sometimes, even the most complicated android app is still just some logic and data, a bit of network access, and a UI put on top of it. Once you remove the crazy (writing all the code in the view layer) turns out it's not rocket science after all.


## <a name="observer-listener"></a> 2) When should I use an Observer, when should I use a callback listener?

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

//TODO link to some real examples, and separate the listener interface so you can use lambdas

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
  


## 3) Syncing the whole view feels wasteful, I'm just going to update the UI components that have changed for efficiency reasons.

Let me guess you're one of those people who only uses ints instead of enums right? or forgoes getters and setters for extra performance? I knew it!

Well apart from the obvious "permature optimisation is the route of all evil" or however that quote goes, you might be in danger of seriously underestimating how fast even the most basic android phone runs.

This is not usually a problem for any developer that has written game loops, or implemented their own animations using easing equations or similar, but if you've never done that type of development, you might be surprised about the following.

First make sure you've understood the section on [syncView()](/asaf-project/03-databinding.html#syncview) and the example of how doing adhoc updates can go wrong. And if you still want to sacrific that robustness to chase "performance" or "battery life", then read on...

### In to the matrix

Before we go any further, if you haven't already, go to developer settings on android and check out the debug tools that let you see the screen updates as they happen.

***WARNING if you're epileptic, maybe skip this part, you will see some incredibly annoying rapid screen flashing as the screen is updated multiple times a second. I'm not epileptic but a few minutes of that makes me feel seriously car sick.***

The first one is **"Show surface updates"** it flashes when part of the screen is being redrawn. You might be surprised just how often the screen is being updated as you use your android device.

The second option you have is **"Show GPU view updates"** this only shows GPU updates and depending on your device you may see this working a lot, or not at all.

Now that you've peeked a little under the hood, you'll be able to appreciate that if you're looking at a single waiting animation (like a standard indeterminate progress bar on Android), the screen (or at least that part of it) will be updating the UI around 30 times a second in response to a ui widget that is continually recalculating its state, also 30 times a second. Any scrolling of a list view; any background blurring animation; even a blinking cursor will sometimes cause the screen to be redrawn 30 times a second or so. That's how fast it needs to be to trick human eyes into thinking something is moving when it isn't - you're just seeing a sequence of still images.

If you put some logs in the syncView() method you'll also see that it is in fact hardly called at all most of the time, unless you are using the Observables to run an animation loop (which is something that you absolutely can do given the performance of the observer implementation in this library by the way).

The syncView() also completes pretty quickly as all of your getters should be returning fast anyway, as recommended [here](/asaf-project/02-models.html#model-checklist).

In addition, if you are setting a value on a UI element that is the same as the value it already has, it would be a bug in the android framework if it caused a complete re-layout in response anyway (I'm not saying such bugs don't exist, but if you ever get any kind of performance issues with this technique, that's the time to measure and see what is happening, but if you follow the guidelines here correctly you will almost certainly  never have any problems at all, and what you get in return is unparalleled robustness).

If you have a model that is changing in some way that an observer just so happens NOT be interested in, you will end up making a pass through syncView() unecessarily (but still not actually redrawing the screen) you should really chilax and be happy with the knowledge that your UI is *definitely* consistent.

