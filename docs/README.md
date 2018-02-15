# ASAF
**Alarmingly Simple Android Framework**

[(if you're reading this on github click here)](https://erdo.github.io/asaf-project/#shoom)

A few tiny framework classes which help you write android code that is **simple**, **robust** and **performant**. ASAF is most helpful when applied to the boundary between the view layer the rest of your app, i.e. it helps you implement very clean [data binding](https://erdo.github.io/asaf-project/03-databinding.html#shoom) while supporting rotation **by default** - no additional work is required.

ASAF addresses **testability**; **lifecycle management**; **UI consistency**; and **memory leaks** and if you're spending time dealing with those issues in your code base while trying to support rotation, you should give it a try.

The architecture is also highly **scalable**, supporting commercial grade android applications and complex UIs.  *(it really is tiny - asaf-core is less than 500 lines of code)*

More about the architecture is [**here**](https://erdo.github.io/asaf-project/07-architecture.html#architecture), but probably the best place to learn is in the code of the [sample apps](#sample-apps), or just keep reading.




## Quick Start
Latest version: 0.9.26


```
compile (group: 'co.early.asaf', name: 'asaf-core', version: '0.9.26', ext: 'aar')
```
optional:

```
compile (group: 'co.early.asaf', name: 'asaf-adapters', version: '0.9.26', ext: 'aar')
compile (group: 'co.early.asaf', name: 'asaf-retrofit', version: '0.9.26', ext: 'aar')
compile (group: 'co.early.asaf', name: 'asaf-ui', version: '0.9.26', ext: 'aar')
```


**If you're not familiar with ASAF and you want to get up to speed quickly with how this library works from scratch, it's probably 1-2 hours work. I'd recommend:**

 1. Cloning the git repo
 2. Getting the example apps running (you'll need at least Android Studio 3)
 3. Reading the following sections on this site: [**Views**](https://erdo.github.io/asaf-project/01-views.html#shoom), [**Models**](https://erdo.github.io/asaf-project/02-models.html#shoom), [**Data Binding**](https://erdo.github.io/asaf-project/03-databinding.html#shoom) while referring to the code of the [sample apps](#sample-apps)


## Overview

There are many over-engineered android app architectures in existence - (probably because it's easy to accidentally write something that is over-engineered). What's surprising is that many of these architectures don't get basic View / Model separation correct or they gloss over rotation support.

*(Try rotating a sample app or two and see if it triggers a network call each time - and if not, check for any ```if(firstTime){callNetwork()}``` style hacks that exist in the model layer - that's a sure sign that the separation between the view and model layers is a mirage. Now try adding a couple of seconds delay to the network call to simulate real behaviour - does the ui accurately reflect what's happening? are the "busy" indicators consistent? How about if you rotate the screen mid-network call... "busy" indicators no longer showing even though there is a network call in progress? - you're looking at a broken data binding implementation causing a UI consistency problem)*

What's hard, is to produce something that is simple but also generically applicable - that often requires multiple iterations. ASAF (though now very stable) has been going through those iterations privately for years - and that privacy has facilitated the focussed *removal* of surplus functionality and methods, in a way that would be more difficult for a public project.

ASAF's overriding goal is to be **clear** and **easy to understand**, which makes the apps it supports **robust**, **quick to develop**, and **easy to change**. Hopefully that will become obvious to you as you familiarize yourself with how to use ASAF in your own projects.

### Yes, but what is it?

The ASAF framework is basically a specific implmentation of the [Observer pattern](https://en.wikipedia.org/wiki/Observer_pattern) combined with [dependency injection](https://erdo.github.io/asaf-project/04-more.html#dependency-injection) which enables you to bind data to the view layer in a way that completely separates it from the rest of your app (and in doing so remove a whole class of problems from typical android development).

It most closely resembles MVVM (but without using any android xml bindings) the [**architecture page**](https://erdo.github.io/asaf-project/07-architecture.html#architecture) has more discussion about this.

You'll notice ASAF uses custom views as a place to put all the view related code (this enables you to avoid many Activity/Fragment lifecycle clutter that you might otherwise encounter).

Developing with ASAF generally means writing observable and testable [**Model**](https://erdo.github.io/asaf-project/02-models.html#shoom) classes for all your logic (or converting the models you already have to be Observable), and writing [**View**](https://erdo.github.io/asaf-project/01-views.html#shoom) layer classes which observe these models for any changes (so that the views can sync their UI / run animations etc.)

> "Observable **Models**; **Views** doing the observing; and some **Data Binding** tricks to tie it all together"

ASAF also includes some testable alternatives for AsyncTask ([**AsafTask**](https://erdo.github.io/asaf-project/04-more.html#asaftask) and [**AsafTaskBuilder**](https://erdo.github.io/asaf-project/04-more.html#asaftaskbuilder)), and formalises an approach to **simple one way data binding** using a [**syncView()**](https://erdo.github.io/asaf-project/03-databinding.html#syncview) method that **never** leaves your view out of sync with your model.

There are also optional extras that help with using [**adapters**](https://erdo.github.io/asaf-project/04-more.html#adapters) and working with [**Retrofit2**](https://erdo.github.io/asaf-project/04-more.html#retrofit-and-the-callprocessor).

You might be shocked at how much android code becomes unnecessary when you take this approach to development.

## Sample Apps

![simple basket](img/screenshot_asaf_samples_phone_all_1000.png)

The apps here are deliberately sparse and ugly so that you can see exactly what they are doing. These are not examples for how to nicely structure XML layouts or implement ripple effects - all that you can do later in the **View** layers and it should have no impact on the stability of the app.

These apps are however, totally robust and comprehensively tested. And that's really where you should try to get to as quickly as possible, so that you can **then** start doing the fun stuff like adding beautiful graphics and cute animations.

For these sample apps, all the **View** components are located in the **ui/** package and the **Models** are in the **feature/** package. This package structure gives the app code good glanceability and should let you find what you want easily.

For the sample apps there is a one-to-one relationship between the sub-packages within **ui/**, and the sub-packages within **feature/** but it needn't be like that and for larger apps it often isn't. You might have one BasketModel but it will be serving both a main BasketView and a BasketIconView located in a toolbar for instance. A more complex view may use data from several different models at the same time eg a BasketModel and an AccountModel.

Aside from the apps, there is also a lot of information in this guide that will take you through the detail of how and why ASAF works.


### ASAF 1 Data Binding Example

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example01databinding/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafdatabinding) \| [source code](https://github.com/erdo/asaf-project/tree/master/example01databinding)

This app is a bare bones implementation of ASAF databinding. No threading, no networking, no database access - just the minimum required to demostrate [Data Binding](https://erdo.github.io/asaf-project/03-databinding.html#shoom). It's still a full app though, supports rotation and has a full set of tests to go along with it.

In the app you move money from a "Savings" wallet to a "Mobile" wallet and then back again. Its inspiration is the diagram in the [architecture](https://erdo.github.io/asaf-project/07-architecture.html#bad-diagram) section, although it sadly doesn't look quite as awesome as that diagram does.



### ASAF 2 Asynchronous Code Example

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example02threading/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafthreading) \| [source code](https://github.com/erdo/asaf-project/tree/master/example02threading)

This one demonstrates asynchronous programing, and importantly how to test it. It uses ([AsafTask](https://erdo.github.io/asaf-project/04-more.html#asaftask) and [AsafTaskBuilder](https://erdo.github.io/asaf-project/04-more.html#asaftaskbuilder)). Again, it's a bare bones (but complete and tested) app - just the minimum required to demostrate asynchronous programing.

This app has a counter that you can increase by pressing a button (but it takes 20 seconds to do the increasing - during which time you can rotate the device, background the app etc). There are two methods demonstrated, one which allows you to publish progress, and one which lets you take advantage of lambda expressions.

It's really a very exciting app, I recently launched it on the play store and I'm just waiting for the inevitable interview requests to come through from arstechnica and techcrunch.



### ASAF 3 Adapter Example

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example03adapters/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafadapters) \| [source code](https://github.com/erdo/asaf-project/tree/master/example03adapters)

This one demostrates how to use [**adapters**](https://erdo.github.io/asaf-project/04-more.html#adapters) with ASAF (essentially call notifyDataSetChanged() inside the syncView() method).

It also demonstrates how to take advantage of the built in list animations that Android provides. Once you have set your adapter up correctly, you just call notifyDataSetChangedAuto() inside the syncView() method and ASAF will take care of all the notify changes work.

Two lists are displayed side to side so you can see the effect this has when adding or removing items. The "Simple" list is on the left, the "Advanced" one that uses notifyDataSetChangedAuto() is on the right.

As usual it's a complete and tested app but contains just the minimum required to demostrate adapters. It's not been nominated for any design awards, as yet.



### ASAF 4 Retrofit Example

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example04retrofit/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafretrofit) \| [source code](https://github.com/erdo/asaf-project/tree/master/example04retrofit)

Clicking the buttons in this app will perform a network request to some static files that are hosted on [Mocky](https://www.mocky.io/) (have you seen that thing? it's awesome). The first button gets a successful response, the last two get failed responses which are handled in two different ways. The first is a simple error, based on the HTTP code the app receives back from the server. The other is a more specific error based on parsing the body of the error response for an error object. That's managed by the [CallProcessor](https://erdo.github.io/asaf-project/04-more.html#retrofit-and-the-callprocessor) which is the main innovation in the asaf-retrofit library.

As you're using the app, please notice:

- **how you can rotate the device with no loss of state or memory leaks**. I've used Mocky to add a 3 second delay to the network request so that you can rotate the app mid-request to clearly see how it behaves (because we have used ASAF to seperate the view from everything else, rotating the app makes absolutely no difference to what the app is doing, and the network busy spinners remain totally consistent).

As usual this is a complete and tested app. In reality the tests are probably more than I would do for a real app this simple, but they should give you an idea of how you can do **unit testing**, **integration testing** and **UI testing** whilst steering clear of accidentally testing implementation details when using ASAF.


### ASAF 5 UI Helpers Example (Tic Tac Toe)

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example05ui/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafui) \| [source code](https://github.com/erdo/asaf-project/tree/master/example05ui)

A regular Tic Tac Toe game that makes use of a few UI convenience classes:


- The [SyncableXXX](https://erdo.github.io/asaf-project/04-more.html#syncable-convenience-classes) classes which reduce boiler plate slightly and automatically handle databinding (the adding and removing of observers in line with various lifecycle methods)

- [SyncTrigger](https://erdo.github.io/asaf-project/04-more.html#synctrigger) which bridges the gap between the observer pattern and one off triggers that you want to fire (such as displaying a win animation at the end of a game)


No automated tests for this app yet! (but you should be getting the idea by now - sample apps 1-4 all have comprehensive tests included). If I get a spare moment I will add them at least for the **Board** class which contains all the logic.




### Other Full App Examples

*There is a full app example hosted in a separate repo: one branch for **pure DI**, one for **Dagger 2***
**[here](https://github.com/erdo/asaf-full-app-example)**

*The same app written in **Kotlin** (functional but probably a little more to do clean code wise)*
**[here](https://github.com/erdo/asaf-full-app-example-kotlin)**



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


### Say Thanks
Nothing says thank you like bitcoin sent to 3MpUaLWeNUQLxonMxBYyaEugmoEMrEzuc6

![bitcoin](img/bitcoin.png)


