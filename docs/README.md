# android fore

[![license-apache2](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://github.com/erdo/android-fore/blob/master/LICENSE.txt){: .float-left}

![jcenter-1.0.0](https://img.shields.io/badge/jcenter-1.0.0-green.svg){: .float-left}

[![circleci](https://circleci.com/gh/erdo/android-fore/tree/master.svg?style=shield)](https://circleci.com/gh/erdo/android-fore/tree/master){: .float-left}

<br/> 

[(click here if you're reading this on github)](https://erdo.github.io/android-fore/#shoom)

A tiny library that helps you write android code in the [**MVO**](https://erdo.github.io/android-fore/00-architecture.html#shoom) style that is **simple**, **robust** and **performant**. MVO focusses on the boundary between the view layer the rest of your app, i.e. it helps you implement a very clean [reactive UI](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom) while supporting rotation **by default** - no additional work is required.

<br/>
<span class="news">
New tutorial available [here!](https://medium.com/@erdo9000/tutorial-android-fore-basics-fb10788d388c)
</span>
<br/>
<br/>

MVO addresses issues like **testability**; **lifecycle management**; **UI consistency**; **memory leaks**; and **development speed** - and if you're spending time dealing with any of those issues in your code base or team, it's well worth considering (especially if your current architecture struggles a little when it comes to supporting rotation).

Though the resulting code is often very sparse and clear, there is a considerable amount of thought required to get it to that stage. MVO requires a slight mindset change: the [**View layer**](https://erdo.github.io/android-fore/01-views.html#shoom) is extremely thin, typically more so than with other architectures.


## Quick Start
**Latest version: 1.0.0**  (pre-androidX use 0.11.1)


```
implementation (group: 'co.early.fore', name: 'fore-core', version: '1.0.0', ext: 'aar')
```
optional:

```
implementation (group: 'co.early.fore', name: 'fore-adapters', version: '1.0.0', ext: 'aar')
implementation (group: 'co.early.fore', name: 'fore-retrofit', version: '1.0.0', ext: 'aar')
implementation (group: 'co.early.fore', name: 'fore-lifecycle', version: '1.0.0', ext: 'aar')
```

See the [upgrading guide](https://erdo.github.io/android-fore/06-upgrading.html#shoom) if you're coming from an older version.


## New to fore

**If you're new to fore, Welcome! might I suggest:**

 1. Cloning this git repo
 2. Getting the example apps running (you'll need at least Android Studio 3)
 3. While referring to the code of the [sample apps](#sample-apps), dip in to the following sections of the site:
  [**MVO Architecture**](https://erdo.github.io/android-fore/00-architecture.html#shoom),
  [**Views**](https://erdo.github.io/android-fore/01-views.html#shoom), [**Models**](https://erdo.github.io/android-fore/02-models.html#shoom), [**Reactive UIs**](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom)


### Method Counts
![fore-core methods](https://img.shields.io/badge/fore--core-126-orange.svg){: .float-left}
![fore-adapters methods](https://img.shields.io/badge/fore--adapters-84-orange.svg){: .float-left}
![fore-retrofit methods](https://img.shields.io/badge/fore--retrofit-38-orange.svg){: .float-left}
![fore-lifecycle methods](https://img.shields.io/badge/fore--lifecycle-59-orange.svg){: .float-left}

<br/><br/>

**fore** (though now stable) has been going through iterations privately for more than half a decade - and that privacy has facilitated the focussed *removal* of surplus functionality and methods, in a way that would probably be more difficult for a public project. The result is an MVO implementation which is particularly small - so small in fact that you could just copy and paste the code into your app if you wanted (it's small enough to be self-manageable, and the core code hasn't changed in a while anyway). I'd recommend you just use the JCenter version, but each to their own ;) There's also no reason you can't implement MVO yourself of course.

### Overview

Due to the sparseness of the resulting view layer code, MVO is particularly **scalable with regards to UI complexity**, and because of the [reactive UI](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom) strategy used, it's typically very performant. The **fore** library implementation already supports a number of commercial android applications.

In a nutshell, developing with MVO means:

> "Observable **Models**; **Views** doing the observing; and some **Reactive UI** tricks to tie it all together"

In [**MVO**](https://erdo.github.io/android-fore/00-architecture.html#shoom) (like with most MV* architectures) the model knows nothing about the View. When the view is destroyed and recreated, the view re-attaches itself to the model in line with the observer pattern and syncs its view. Any click listeners or method calls as a result of user interaction are sent directly to the relevant model (no benefit sending them via a Presenter in this case). With this architecture you remove a lot of problems around lifecycle management and handling rotations, it also turns out that the code to implement this is a lot less verbose **(and it's also very testable and scalable)**.

**There are a few important things in MVO that allow you an architecture this simple:**

* The first is a very robust but simple [**Observer implementation**](https://erdo.github.io/android-fore/03-reactive-uis.html#fore-observables) that lets views attach themselves to any model they are interested in
* The second is the [**syncView()**](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview) convention
* The third is writing [**models**](https://erdo.github.io/android-fore/02-models.html#shoom) at an appropriate level of abstraction, something which comes with a little practice
* The fourth is making appropriate use of [**DI**](https://erdo.github.io/android-fore/05-extras.html#dependency-injection-basics)

If you totally grok those 4 things, that's pretty much all you need to use MVO successfully, the [**code review guide**](https://erdo.github.io/android-fore/05-extras.html#troubleshooting--how-to-smash-code-reviews) should also come in handy as you get up to speed, or you bring your team up to speed.

The **fore** library also includes some testable wrappers for AsyncTask: [**Async**](https://erdo.github.io/android-fore/04-more-fore.html#asynctasks-with-lambdas) and [**AsyncBuilder**](https://erdo.github.io/android-fore/04-more-fore.html#asyncbuilder) (which lets you make use of lambdas)

There are also optional extras that simplify [**adapter animations**](https://erdo.github.io/android-fore/04-more-fore.html#adapters-notifydatasetchangedauto) and abstract your networking layer when using [**Retrofit2**](https://erdo.github.io/android-fore/04-more-fore.html#retrofit-and-the-callprocessor).


## Sample Apps

![simple basket](img/screenshot_asaf_samples_phone_all_1000.png)

The apps here are deliberately sparse and ugly so that you can see exactly what they are doing. These are not examples for how to nicely structure XML layouts or implement ripple effects - all that you can do later in the **View** layers and it should have no impact on the stability of the app.

These apps are however, totally robust and comprehensively tested (and properly support rotation). And that's really where you should try to get to as quickly as possible, so that you can **then** start doing the fun stuff like adding beautiful graphics and cute animations.

For these sample apps, all the **View** components are located in the **ui/** package and the **Models** are in the **feature/** package. This package structure gives the app code good glanceability and should let you find what you want easily.

For the sample apps there is a one-to-one relationship between the sub-packages within **ui/**, and the sub-packages within **feature/** but it needn't be like that and for larger apps it often isn't. You might have one BasketModel but it will be serving both a main BasketView and a BasketIconView located in a toolbar for instance. A more complex view may use data from several different models at the same time eg a BasketModel and an AccountModel.


### **fore 1** Reactive UI Example

[video](https://www.youtube.com/watch?v=wDu6iaSzKHI) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foredatabinding) \| [source code](https://github.com/erdo/android-fore/tree/master/example01databinding)

![fore reactive UI sample app](https://j.gifs.com/MQ33GB.gif)

This app is a bare bones implementation of **fore** reactive UIs. No threading, no networking, no database access - just the minimum required to demonstrate [Reactive UIs](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom). It's still a full app though, supports rotation and has a full set of tests to go along with it.

In the app you move money from a "Savings" wallet to a "Mobile" wallet and then back again. Its inspiration is the diagram in the [architecture](https://erdo.github.io/android-fore/00-architecture.html#bad-diagram) section, although it sadly doesn't look quite as awesome as that diagram does.



### **fore 2** Asynchronous Code Example

[video](https://www.youtube.com/watch?v=di_xvaYUTxo) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.forethreading) \| [source code](https://github.com/erdo/android-fore/tree/master/example02threading)

![fore threading sample app](https://j.gifs.com/32LLNn.gif)

This one demonstrates asynchronous programming, and importantly how to test it. It uses ([Async](https://erdo.github.io/android-fore/04-more-fore.html#async) and [AsyncBuilder](https://erdo.github.io/android-fore/04-more-fore.html#asyncbuilder)). Again, it's a bare bones (but complete and tested) app - just the minimum required to demonstrate asynchronous programming.

This app has a counter that you can increase by pressing a button (but it takes 20 seconds to do the increasing - during which time you can rotate the device, background the app etc). There are two methods demonstrated, one which allows you to publish progress, and one which lets you take advantage of lambda expressions.


### **fore 3** Adapter Example

[video](https://www.youtube.com/watch?v=eAbyhOyoMxU) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foreadapters) \| [source code](https://github.com/erdo/android-fore/tree/master/example03adapters)

![fore adapters sample app](https://j.gifs.com/wmJJ3m.gif)


This one demonstrates how to use [**adapters**](https://erdo.github.io/android-fore/04-more-fore.html#adapters-notifydatasetchangedauto) with **fore** (essentially call notifyDataSetChanged() inside the syncView() method).

It also demonstrates how to take advantage of the built in list animations that Android provides. Once you have set your adapter up correctly, you just call notifyDataSetChangedAuto() inside the syncView() method and **fore** will take care of all the notify changes work.

Two lists are displayed side to side so you can see the effect this has when adding or removing items. The "Simple" list is on the left, the "Advanced" one that uses notifyDataSetChangedAuto() is on the right.

As usual it's a complete and tested app but contains just the minimum required to demonstrate adapters.



### **fore 4** Retrofit Example

[video](https://www.youtube.com/watch?v=zOIoK8Fj0Ug) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foreretrofit) \| [source code](https://github.com/erdo/android-fore/tree/master/example04retrofit)

![fore retrofit sample app](https://j.gifs.com/qYzz3D.gif)

Clicking the buttons in this app will perform a network request to some static files that are hosted on [Mocky](https://www.mocky.io/) (have you seen that thing? it's awesome). The first button gets a successful response, the last two get failed responses which are handled in two different ways. The first is a simple error, based on the HTTP code the app receives back from the server. The other is a more specific error based on parsing the body of the error response for an error object. That's managed by the [CallProcessor](https://erdo.github.io/android-fore/04-more-fore.html#retrofit-and-the-callprocessor) which is the main innovation in the fore-retrofit library.

As you're using the app, please notice:

- **how you can rotate the device with no loss of state or memory leaks**. I've used Mocky to add a 3 second delay to the network request so that you can rotate the app mid-request to clearly see how it behaves (because we have used **fore** to separate the view from everything else, rotating the app makes absolutely no difference to what the app is doing, and the network busy spinners remain totally consistent).

As usual this is a complete and tested app. In reality the tests are probably more than I would do for a real app this simple, but they should give you an idea of how you can do **unit testing**, **integration testing** and **UI testing** whilst steering clear of accidentally testing implementation details when using **fore**.


### **fore 5** UI Helpers Example (Tic Tac Toe)

[video](https://www.youtube.com/watch?v=Zuwe45EttY4) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foreui) \| [source code](https://github.com/erdo/android-fore/tree/master/example05ui)

![fore tic-tac-toe sample app](https://j.gifs.com/zKMM35.gif)

A regular Tic Tac Toe game that makes use of:

- The [SyncableXXX](https://erdo.github.io/android-fore/04-more-fore.html#lifecycle-components) lifecycle convenience classes which reduce boiler plate slightly and automatically handle databinding (the adding and removing of observers in line with various lifecycle methods)

- [SyncTrigger](https://erdo.github.io/android-fore/04-more-fore.html#synctrigger) which bridges the gap between the observer pattern and one off triggers that you want to fire (such as displaying a win animation at the end of a game)


No automated tests for this app (but you should be getting the idea by now - sample apps 1-4 all have comprehensive tests included).


### **fore 6** DB Example (Room db driven to-do list)
<div class="shoom" id="fore-6-db-example-room"/>

[video](https://www.youtube.com/watch?v=a1ehGU5O8i8) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foredb) \| [source code](https://github.com/erdo/android-fore/tree/master/example06db)

![fore room db sample app](https://j.gifs.com/Xo88J8.gif)


A To-do list on steroids that lets you:

- manually add 50 random todos at a time
- turn on a "boss mode" which randomly fills your list with even more todos over the following 10 seconds
- "work from home" which connects to the network and downloads 25 extra todos (up to 9 simultaneous network connections)
- randomly delete about 10% of your todos
- randomly change 10% of your outstanding todos to done

It's obviously ridiculously contrived, but the idea is to implement something that would be quite challenging and to see how little code you need in the view layer to do it.

It is driven by a Room db, and there are a few distinct architectural layers: as always there is a view layer and a model layer (in packages ui and feature). There is also a networking and a persistence layer. The UI layer is driven by the model which in turn is driven by the db.

All the database changes are done off the UI thread, RecyclerView animations using DiffUtil are supported (for lists below 1000 rows) and the app is totally robust and supports rotation out of the box. There is a TodoListModel written in Java and one in Kotlin for convenience, in case you are looking to use these as starting points for your own code.

There is only one test class included with this app which demonstrates how to test Models which are driven by a Room DB (using CountdownLatches etc). For other test examples, please see sample apps 1-4


### Other Full App Examples

- There is a full app example hosted in a separate repo written in Kotlin **[here](https://github.com/erdo/fore-full-example-01-kotlin)**

- The **Password123** sample app uses **fore** for its reactive UI and the **[kotlin source](https://github.com/erdo/password123)** is available.


### Tutorials

- There is a medium tutorial covering the basics of fore, complete with a small sample app written in Kotlin [here](https://medium.com/@erdo9000/tutorial-android-fore-basics-fb10788d388c)


## Contributing
Please read the [Code of Conduct](https://erdo.github.io/android-fore/CODE-OF-CONDUCT.html#shoom), and check out the [issues](https://github.com/erdo/android-fore/issues) :)


## License


    Copyright 2017-2018 early.co

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
