

# Architecture

If you enjoy architecture diagrams, hopefully you'll love this page. If you don't, let's just say that ASAF is **MVW** and be done with it ;)

![simple basket](img/arch_mvw_asaf.png)

That block diagram above is what ASAF looks like (it's simplified of course, and how we get there is detailed below). Another way to look at it is in *crap diagram* mode. So here's a crap diagram showing the flow of a button click from **step 1** to **3** with a single **observable model** and a **view** that observes it:

<a name="bad-diagram"></a>

![data binding](img/data-binding.png)

Importantly, during the syncView() stage, the view syncs [**all the view states**](https://erdo.github.io/asaf-project/03-databinding.html#syncview), it's a fundamental part of why ASAF code looks so sparse.

That diagram matches what is happening in [**sample app 1**](https://erdo.github.io/asaf-project/#asaf-1-data-binding-example). Here are the relevant bits of code: the [**observable model code**](https://github.com/erdo/asaf-project/blob/master/example01databinding/src/main/java/foo/bar/example/asafdatabinding/feature/wallet/Wallet.java) and the [**view code**](https://github.com/erdo/asaf-project/blob/master/example01databinding/src/main/java/foo/bar/example/asafdatabinding/ui/wallet/WalletsView.java) that does the observing.

The code looks extremely simple and it is, but surprisingly the technique works the same if you're using [**adapters**](https://github.com/erdo/asaf-project/blob/master/example03adapters/src/main/java/foo/bar/example/asafadapters/ui/playlist/PlaylistsView.java), or if you're doing [**threaded work in your model**](https://github.com/erdo/asaf-project/blob/master/example02threading/src/main/java/foo/bar/example/asafthreading/feature/counter/CounterWithLambdas.java), or fetching data [**from a network**](https://github.com/erdo/asaf-full-app-example/blob/master/app/src/main/java/co/early/asaf/fullapp01/feature/fruitcollector/FruitCollectorModel.java). It even works when you have a heavily animated view like we do in [**sample app 5**](https://erdo.github.io/asaf-project/asaf-5-ui-helpers-example-tic-tac-toe) here's the [**view code**](https://github.com/erdo/asaf-project/blob/master/example05ui/src/main/java/foo/bar/example/asafui/ui/tictactoe/TicTacToeView.java) for that app. Here's a kotlin [**view**](https://github.com/erdo/password123/blob/master/app/src/main/java/co/early/password123/ui/passwordchooser/PwChooserView.kt) that similarly has a lot of animations in it.

Oh and did I mention? all that code just works if you rotate the screen - without you needing to do a single thing.

> "the code works if you rotate the screen - without you needing to do a single thing"

## How we got there

Discussions of **MVC**, **MVP** and **MVVM** can get quite abstract, and specific implementations often differ considerably. For the purposes of our discussion the following flow diagrams will do:

![simple basket](img/arch_mvc.png)

This is quite a common representation of **MVC**, however I don't think it's a particularly useful diagram - it depends entirely on the specifics of your controller which often isn't mentioned at all. If you are considering your Android Activity class to be the controller, then implementing something like this on Android is going to be a mess. If you are considering your controllers to be your click listeners then it's basically a nothing diagram that shows a View interacting with a Model. (See below for a discussion of [Controllers](#whats-a-controller)).

There is one important thing to note about about this diagram however. If we focus on the **Model** [click here for our definition of Model](https://erdo.github.io/asaf-project/02-models.html#shoom), all the arrows (dependencies) point towards the Model. This tells us that while the View and Controller know about each other and the Model, the Model knows nothing about the View or the Controller. That's exactly the way we want it. This way a Model can be tested independently, and needs to know nothing about the view layer. It can support any number of different Views which can come and go as they please (when an Android device is rotated for example, the Model is not affected - or even aware of it).

I did say that I thought the typical MVC diagram is not particularly useful, I think it's main purpose is just to be shown before the MVP diagram is - so that we can see a particular difference. So here is a typical MVP diagram:

![simple basket](img/arch_mvp.png)

It's basically the same thing except here the View doesn't know about the Model. All interactions with the Model go via a Presenter class. The Presenter class usually does two main things: it sets UI states on the View (so it needs to know about the View) and it forwards commands from click listeners and the like, to the underlying Model / Models (so it needs to know about those Models too).

In a typical MVP Android app, quite a bit of boiler plate is required to let the Presenter do its job, typical implementations also create the Presenter from scratch each time the view is constructed, and that can make handling rotations difficult.

Note that as with MVC, the Model is not aware of the higher level View related classes - which is a good thing. Moving code from the View to a Presenter class also means that we can now unit test it, which is great. (The Presenter is aware of the View but this is usually via an injected interface, so for a unit test you don't need to set up an actual View, just its interface)

The main issue with both of these approaches on Android though, is the arrow pointing to the View

![simple basket](img/arch_mvpx.png)

Android has a particular problem with this as the Views are destroyed and created even due to a simple screen rotation and each time that happens, all the references need to be recreated.

Here's the MVVM equivalent diagram:

![simple basket](img/arch_mvvm.png)

Again there are different ways of doing MVVM, even on Android, but the main difference here is that the View-Model is not aware of the View like the Presenter is. All the arrows go from the edge of the system where the UI is, towards the centre where things like business logic reside, down in the model layer.

In MVVM you typically have a View-Model for each View, so even though there are no dependencies on the View from the View-Model (no arrow pointing from View-Model to View), it's still a specific implementation for that View, you can't use one View-Model for different Views. A slightly more realistic situation for a whole app with different views looks like this:

![simple basket](img/arch_mvvm_reality.png)

You can implement this using something like LiveData on Android, but when you get into the details I don't think it's a particularly nice solution (related to [this](https://erdo.github.io/asaf-project/06-faq.html#somethingchanged-parameter) and the [syncView](https://erdo.github.io/asaf-project/03-databinding.html#syncview) convention that we use with ASAF) - it's a considerable step forward none the less, and it may work for you. Importantly, all the arrows are pointing the right way! (which, no surprise, happens to match the direction of the arrows in [clean architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html))

## Finally ASAF

As we mentioned, here is what ASAF looks like in a real app:

![simple basket](img/arch_mvw_asaf.png)

Well how does that work? you can't just remove boxes and call it better! (I hear you say).


> "Observable **Models**; **Views** doing the observing; and some **Data Binding** tricks to tie it all together"


As with all the architectures discussed so far, here the Model knows nothing about the View. In ASAF, when the view is destroyed and recreated, the view re-attaches itself to the model using the observer pattern. Any click listeners or method calls as a result of user interaction are sent directly to the relevant model (no benefit here in sending them via a Presenter). With this architecture you remove a lot of problems around lifecycle management and handling rotations, it also turns out that the code to implement this is a lot less verbose (and it's also very testable and scalable).

**There are a few important things in ASAF that allow you an architecture this simple:**

* The first is a very robust but simple [**Observer implementation**](https://erdo.github.io/asaf-project/03-databinding.html#asaf-observables) that lets views attach themselves to any model they are interested in
* The second is the [**syncView()**](https://erdo.github.io/asaf-project/03-databinding.html#syncview) convention
* The third is writing [**models**](https://erdo.github.io/asaf-project/02-models.html#shoom) at an appropriate level of abstraction, something which comes with a little practice
* The fourth is making appropriate use of [**DI**](https://erdo.github.io/asaf-project/04-more.html#dependency-injection)

 If you totally grok those 4 things, that's pretty much all you need to use ASAF successfully, the [**code review guide**](https://erdo.github.io/asaf-project/05-code-review-checklist.html#shoom) should also come in handy as you get up to speed, or you bring your team up to speed.

### BTW, What's a Controller
It helps to remember that MVC is at least 3 decades old, I think it was Microsoft who invented it [I saw a Microsoft white paper written about it once, but I can't find it anywhere now]. A controller means different things on different platforms.

Originally a controller might have been a class that accepts mouse clicks at specific pixel co-ordinate, did some collision detection to find out which UI component was clicked, then sent that information on to the appropriate UI classes for further processing. (A controller in a web app however, might be a main entry point URL that forwards on requests to different parts of the system.)

In modern app frameworks most of the controller work is implemented for you by the UI framework itself - these are the button click listeners that simply catch user input and send it on to the right place. As we need to worry less about controllers now a days, we talk more about more "modern" things like MVVM - which is only about **10(!)** years old.

(Android also lets you use Activities as kind of "Controllers" by letting you specify callback methods right in the XML for buttons which will end up getting called on whatever activity is hosting that particular view. The idea is to not have to write click listeners - I'd recommend not using it because it encourages (forces) you to get the activity involved in something that it doesn't need to be involved in. If you leave everything out of the Activity then you can re-use your custom view in any activity you like, without needing to re-implement all those button call backs each time.)
