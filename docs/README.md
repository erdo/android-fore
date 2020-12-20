# android fore

[![license-apache2](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://github.com/erdo/android-fore/blob/master/LICENSE.txt){: .float-left}

![jcenter-1.3.2](https://img.shields.io/badge/jcenter-1.3.2-green.svg){: .float-left}

![api-16](https://img.shields.io/badge/api-16%2B-orange.svg){: .float-left}

[![circleci](https://circleci.com/gh/erdo/android-fore/tree/master.svg?style=shield)](https://circleci.com/gh/erdo/android-fore/tree/master){: .float-left}

<br/>
<br/>

[(click here if you're reading this on github)](https://erdo.github.io/android-fore/#shoom)

**fore** helps you move code out of the view layer. Because once you do that, magical things start to happen.

The most important class in the fore library is the observable implementation. This very simple class lets you **make anything observable** (usually it's models & repositories that are made observable, things in the view layer like activities & fragments do the observing).


<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>

<pre class="tabcontent tabbed java"><code>
public class AccountRepository extends ObservableImp {

  public AccountRepository(WorkMode workMode) {
    super(workMode);
  }

  ...

}
 </code></pre>

<pre class="tabcontent tabbed kotlin"><code>
class AccountRepository : Observable by ObservableImp() {

  ...

}
 </code></pre>

The core package is tiny (the java version references **128 methods** in all, and adds just **12.5KB** to your apk _before_ obfuscation). You can simply use the observer to make your view layer [**reactive**](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom) and **testable**, or go full on [MVO](https://erdo.github.io/android-fore/00-architecture.html#shoom) and wonder where all your code went ;)

The view layer is particularly sparse when implementing MVO with **fore** and the apps are highly scalable from a complexity standpoint, so **fore** works for both quick prototypes, and large complex commercial projects with 100K+ lines of code.

Specifically _why_ it is that apps written this way are both sparse _and_ scalable is not always immediately obvious. This dev.to [article](https://dev.to/erdo/tutorial-android-architecture-blueprints-full-todo-app-mvo-edition-259o) details the whys and the hows of converting the Android Architecture Blueprint Todo sample app from MVP to MVO (and in doing so drops the lines-of-code count by about half). This [discussion](https://erdo.github.io/android-fore/03-reactive-uis.html#somethingchanged-parameter) also gets into the design of the **fore** api and why it drastically reduces boiler plate for a typical android app compared with alternatives. But these are subtle, advanced topics that are not really necessary to use **fore** at all - most of the actual code in the fore library is quite simple.

## Quick Start

for a more **kotlin** style API and running coroutines under the hood:
```
implementation "co.early.fore:fore-kt:1.3.2"
```

the original **java**:
```
implementation "co.early.fore:fore-jv:1.3.2"
```

The packages are available individually too:

```
implementation "co.early.fore:fore-core:1.3.2"
implementation "co.early.fore:fore-adapters:1.3.2"
implementation "co.early.fore:fore-network:1.3.2"
implementation "co.early.fore:fore-lifecycle:1.3.2"

implementation "co.early.fore:fore-core-kt:1.3.2"
implementation "co.early.fore:fore-adapters-kt:1.3.2"
implementation "co.early.fore:fore-network-kt:1.3.2"
```

If you want to check what versions of what dependencies each package pulls in, the definitive answer is found in the pom files hosted at [jcenter](https://jcenter.bintray.com/co/early/fore/). See the [release notes](https://erdo.github.io/android-fore/06-upgrading.html#shoom) if you're coming from an older version.


## New to fore

**If you're new to fore, Welcome! might I suggest to:**

 1. Clone this git repo
 2. Get the example apps running

(The repo contains 10 tiny example apps, any updates to fore are immediately reflected in the example apps and all their tests need to pass before new versions of fore are released, so they tend to remain current)

**Then either**

Check out some of the tutorials on dev.to [like this one](https://dev.to/erdo/tutorial-spot-the-deliberate-bug-165k) which demonstrates how the syncView() convention helps you to write less code, while removing a whole class of UI consistency bugs from the UI layer.

**Or**

While referring to the code of the [sample apps](#sample-apps), dip in to the following sections of this site:
  [**MVO Architecture**](https://erdo.github.io/android-fore/00-architecture.html#shoom),
  [**Views**](https://erdo.github.io/android-fore/01-views.html#shoom), [**Models**](https://erdo.github.io/android-fore/02-models.html#shoom), [**Reactive UIs**](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom)


Using **fore** and a few techniques outlined in these docs, you can quickly and robustly implement android apps in the [**MVO**](https://erdo.github.io/android-fore/00-architecture.html#shoom) architectural style _(it's like a radically reduced version of MVVM, with the addition of a render() style function similar to MVI/Redux, or like MvRx's invalidate() function - it's called **syncView()** in MVO)_. It usually results in much less code in the view layer, rock-solid UI consistency, great testability, and support for rotation **by default**.

MVO addresses issues like **testability**; **lifecycle management**; **UI consistency**; **memory leaks**; and **development speed** - and if you're spending time dealing with any of those issues in your code base or team, it's well worth considering.

**fore** (though now stable) has been going through iterations privately for more than half a decade - and that privacy has facilitated the focussed *removal* of surplus functionality and methods, in a way that would probably be more difficult for a public project.

The result is an MVO implementation which is particularly small but surprisingly powerful (just over 500 lines of code for the core package).

### Overview

In a nutshell, developing with **fore** means writing:

> "Observable **Models**; **Views** doing the observing; and some **Reactive UI** tricks to tie it all together"

In [**MVO**](https://erdo.github.io/android-fore/00-architecture.html#shoom) (like with most MV* architectures) the model knows nothing about the View. When the view is destroyed and recreated, the view re-attaches itself to the model in line with the observer pattern and syncs its view. Any click listeners or method calls as a result of user interaction are sent directly to the relevant model. With this architecture you remove a lot of problems around lifecycle management and handling rotations, it also turns out that the code to implement this is a lot less verbose **(and it's also very testable and scalable)**.

**There are a few important things in MVO that allow you an architecture this simple:**

* The first is a very robust but simple [**Observer API**](https://erdo.github.io/android-fore/03-reactive-uis.html#fore-observables) that lets views attach themselves to any model they are interested in
* The second is the [**syncView()**](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview) convention
* The third is writing [**models**](https://erdo.github.io/android-fore/02-models.html#shoom) at an appropriate level of abstraction, something which comes with a little practice
* The fourth is making appropriate use of [**DI**](https://erdo.github.io/android-fore/05-extras.html#dependency-injection-basics)

If you totally grok those 4 things, that's pretty much all you need to use **fore** successfully, the [**code review guide**](https://erdo.github.io/android-fore/05-extras.html#troubleshooting--how-to-smash-code-reviews) should also come in handy as you get up to speed, or you bring your team up to speed.

The **fore** library also includes some testable wrappers for AsyncTask (that Google should have provided, but didn't): [**Async**](https://erdo.github.io/android-fore/04-more-fore.html#asynctasks-with-lambdas) and [**AsyncBuilder**](https://erdo.github.io/android-fore/04-more-fore.html#asyncbuilder) - which support lambdas, making using them alot nicer to use.

If you've moved over to using coroutines already, a few fore [extension functions](https://github.com/erdo/android-fore/blob/master/fore-core-kt/src/main/java/co/early/fore/kt/core/coroutine/Ext.kt) are all you need to use coroutines in a way that makes them completely testable (something that is [still](https://github.com/Kotlin/kotlinx.coroutines/pull/1206) [pending](https://github.com/Kotlin/kotlinx.coroutines/pull/1935) in the official release).

There are also optional extras that simplify [**adapter animations**](https://erdo.github.io/android-fore/04-more-fore.html#adapter-animations) and abstract your networking layer when using [**Retrofit2**](https://erdo.github.io/android-fore/04-more-fore.html#retrofit2-and-apollo) or **Apollo** if you're using GraphQL. **fore** works really well with RoomDB too, checkout the sample app for details.

## Sample Apps

![all samples](img/screenshot_asaf_samples_phone_all_1000.png)

The apps here are deliberately sparse and ugly so that you can see exactly what they are doing. These are not examples for how to nicely structure XML layouts or implement ripple effects - all that you can do later in the **View** layers and it should have no impact on the stability of the app.

These apps are however, totally robust and comprehensively tested (and properly support rotation). And that's really where you should try to get to as quickly as possible, so that you can **then** start doing the fun stuff like adding beautiful graphics and cute animations.

For these example apps, all the **View** components are located in the **ui/** package and the **Models** are in the **feature/** package. This package structure gives the app code good glanceability and should let you find what you want easily.

For the sample apps there is a one-to-one relationship between the sub-packages within **ui/**, and the sub-packages within **feature/** but it needn't be like that and for larger apps it often isn't. You might have one BasketModel but it will be serving both a main BasketView and a BasketIconView located in a toolbar for instance. A more complex view may use data from several different models at the same time eg a BasketModel and an AccountModel.

<div class="shoom" id="fore-1-reactive-ui-example"/>
### **fore 1** Reactive UI Example

[video](https://www.youtube.com/watch?v=wDu6iaSzKHI) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foredatabinding) \| [source code (java)](https://github.com/erdo/android-fore/tree/master/example-jv-01reactiveui) \| [source code (kotlin)](https://github.com/erdo/android-fore/tree/master/example-kt-01reactiveui)

![fore reactive UI sample app](https://j.gifs.com/MQ33GB.gif)

This app is a bare bones implementation of **fore** reactive UIs. No threading, no networking, no database access - just the minimum required to demonstrate [Reactive UIs](https://erdo.github.io/android-fore/03-reactive-uis.html#shoom). It's still a full app though, supports rotation and has a full set of tests to go along with it.

In the app you move money from a "Savings" wallet to a "Mobile" wallet and then back again. It implements a tiny section of the diagram from the [architecture](https://erdo.github.io/android-fore/00-architecture.html#bad-diagram) section.


<div class="shoom" id="fore-2-async-example"/>
### **fore 2** Asynchronous Code Example

[video](https://www.youtube.com/watch?v=di_xvaYUTxo) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.forethreading) \| [source code (java)](https://github.com/erdo/android-fore/tree/master/example-jv-02threading) \| [source code (kotlin)](https://github.com/erdo/android-fore/tree/master/example-kt-02coroutine)

![fore threading sample app](https://j.gifs.com/32LLNn.gif)

This one demonstrates asynchronous programming, and importantly how to test it. The **java** version uses ([Async](https://erdo.github.io/android-fore/04-more-fore.html#async) and [AsyncBuilder](https://erdo.github.io/android-fore/04-more-fore.html#asyncbuilder)), the **kotlin** version uses coroutines (with some [fore extensions](https://github.com/erdo/android-fore/blob/master/fore-core-kt/src/main/java/co/early/fore/kt/core/coroutine/Ext.kt) that make the coroutines unit testable). Again, it's a bare bones (but complete and tested) app - just the minimum required to demonstrate asynchronous programming.

This app has a counter that you can increase by pressing a button (but it takes time to do the increasing - so you can rotate the device, background the app etc and see the effect). There are two methods demonstrated: one which uses lambda expressions (for java), and one which publishes progress.

<div class="shoom" id="fore-3-adapter-example"/>
### **fore 3** Adapter Example

[video](https://www.youtube.com/watch?v=eAbyhOyoMxU) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foreadapters) \| [source code (java)](https://github.com/erdo/android-fore/tree/master/example-jv-03adapters) \| [source code (kotlin)](https://github.com/erdo/android-fore/tree/master/example-kt-03adapters)

![fore adapters sample app](https://j.gifs.com/wmJJ3m.gif)


This one demonstrates how to use [**adapters**](https://erdo.github.io/android-fore/04-more-fore.html#adapter-animations) with **fore** (essentially call notifyDataSetChanged() inside the syncView() method).

To take advantage of the built in list animations that Android provides. Once you have set your adapter up correctly, you can instead call notifyDataSetChangedAuto() inside the syncView() method and **fore** will take care of all the notify changes work. (You could also use **fore**'s notifyDataSetChangedAuto() to do this for you from your render() function if you're using MVI / MvRx or some flavour of Redux).

The **java** sample has two lists side by side so you can see the effect this has when adding or removing items. The "Simple" list is on the left, the "Advanced" one that uses notifyDataSetChangedAuto() is on the right. As usual it's a complete and tested app but contains just the minimum required to demonstrate adapters.

The **kotlin** version has three lists, all of which use adapter animations. The first list uses google's **AsyncListDiffer** (which is what drives ListAdapter), the second list uses fore's **Updatable** (which uses android's notifyItem... methods for a very efficient animated adapter implementation), the third list uses fore's **Diffable** (which relies on DiffUtil under the hood). All three implementations have slightly different characteristics, check the source code for further infomation.


<div class="shoom" id="fore-4-retrofit-example"/>
### **fore 4** Retrofit Example

[video](https://www.youtube.com/watch?v=zOIoK8Fj0Ug) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foreretrofit) \| [source code (java)](https://github.com/erdo/android-fore/tree/master/example-jv-04retrofit) \| [source code (kotlin)](https://github.com/erdo/android-fore/tree/master/example-kt-04retrofit)

![fore retrofit sample app](https://j.gifs.com/qYzz3D.gif)

Clicking the buttons in this app will perform network requests to some static files that are hosted on [Mocky](https://www.mocky.io/) (have you seen that thing? it's awesome). The buttons make various network connections, various successful and failed responses are handled in different ways. It's all managed by the [CallProcessor](https://erdo.github.io/android-fore/04-more-fore.html#retrofit2-and-apollo) class which is the main innovation in the fore-network library, the kotlin implementation of CallProcessor is implemented with coroutines and has an API better suited to kotlin and functional programming.

As you're using the app, please notice:

- **how you can rotate the device with no loss of state or memory leaks**. I've used Mocky to add a delay to the network request so that you can rotate the app mid-request to clearly see how it behaves (because we have used **fore** to separate the view from everything else, rotating the app makes absolutely no difference to what the app is doing, and the network busy spinners remain totally consistent). Putting the device in airplane mode also gives you consistent behaviour when you attempt to make a network request.

As usual this is a complete and tested app. In reality the tests are probably more than I would do for a real app this simple, but they should give you an idea of how you can do **unit testing**, **integration testing** and **UI testing** whilst steering clear of accidentally testing implementation details.

<div class="shoom" id="fore-5-ui-example"/>
### **fore 5** UI Helpers Example (Tic Tac Toe)

[video](https://www.youtube.com/watch?v=Zuwe45EttY4) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foreui) \| [source code (java)](https://github.com/erdo/android-fore/tree/master/example-jv-05ui)

![fore tic-tac-toe sample app](https://j.gifs.com/zKMM35.gif)

A regular Tic Tac Toe game that makes use of:

- The [SyncXXX](https://erdo.github.io/android-fore/01-views.html#removing-even-more-boiler-plate) lifecycle convenience classes which reduce boiler plate even more by automatically handling the adding and removing of observers in line with various lifecycle methods

- [SyncTrigger](https://erdo.github.io/android-fore/04-more-fore.html#synctrigger) which is fore's way of bridging the two worlds of **state** and **events**. It's optional of course, but here we use it to fire event triggers (such as displaying a win animation at the end of a game) off the back of observable state changes.


No automated tests for this app (but you should be getting the idea by now - sample apps 1-4 all have comprehensive tests included).

<div class="shoom" id="fore-6-db-example-room"/>
### **fore 6** DB Example (Room db driven to-do list)

[video](https://www.youtube.com/watch?v=a1ehGU5O8i8) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.foredb) \| [source code (java)](https://github.com/erdo/android-fore/tree/master/example-jv-06db)

![fore room db sample app](https://j.gifs.com/Xo88J8.gif)


A To-do list on steroids that lets you:

- manually add 50 random todos at a time
- turn on a "boss mode" which randomly fills your list with even more todos over the following 10 seconds
- "work from home" which connects to the network and downloads 25 extra todos (up to 9 simultaneous network connections)
- randomly delete about 10% of your todos
- randomly change 10% of your outstanding todos to done

It's obviously ridiculously contrived, but the idea is to implement something that would be quite challenging and to see how little code you need in the view layer to do it.

It is driven by a Room db, and there are a few distinct architectural layers: as always there is a view layer and a model layer (in packages ui and feature). There is also a networking and a persistence layer. The UI layer is driven by the model which in turn is driven by the db.

All the database changes are done away from the UI thread, RecyclerView animations using DiffUtil are supported (for lists below 1000 rows), the app is totally robust and supports rotation out of the box. There is a TodoListModel written in Java and one in Kotlin for convenience, in case you are looking to use these as starting points for your own code.

There is only one test class included with this app which demonstrates how to test Models which are driven by a Room DB (using CountdownLatches etc). For other test examples, please see sample apps 1-4


### Tutorials

- There is a short series of **dev.to** tutorials which include more sample apps covering the basics of fore [here](https://dev.to/erdo/tutorial-android-fore-basics-1155)


### Other Full App Examples

- There is a full app example hosted in a separate repo written in Kotlin **[here](https://github.com/erdo/fore-full-example-02-kotlin)**



## Contributing
Please read the [Code of Conduct](https://erdo.github.io/android-fore/CODE-OF-CONDUCT.html#shoom), and check out the [issues](https://github.com/erdo/android-fore/issues) :)


## License


    Copyright 2015-2020 early.co

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
