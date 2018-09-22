# android fore

[(if you're reading this on github click here)](https://erdo.github.io/android-fore/#shoom)

A tiny library that helps you write android code in the [**MVO**](https://erdo.github.io/android-fore/00-architecture.html#overview) style that is **simple**, **robust** and **performant**. MVO focusses on the boundary between the view layer the rest of your app, i.e. it helps you implement very clean [data binding](https://erdo.github.io/android-fore/03-databinding.html#shoom) while supporting rotation **by default** - no additional work is required.

MVO addresses issues like **testability**; **lifecycle management**; **UI consistency**; **memory leaks**; and **development speed** and if you're spending time dealing with those issues in your code base, in a large team, while trying to support rotation: you should give it a try!

Though the resulting code is often very sparse and clear, there is a considerable amount of thought required to get it to that stage. MVO requires a slight mindset change: the [**View layer**](https://erdo.github.io/android-fore/01-views.html#shoom) is extremely thin, typically more so than with other architectures.

Due to the sparseness of the resulting view layer code, MVO is particularly **scalable with regards to UI complexity**. Because of the [data binding](https://erdo.github.io/android-fore/03-databinding.html#shoom) strategy used, it's typically very performant, and the **fore** library implementation supports a number of commercial android applications.

**fore** (though now stable) has been going through iterations privately for years - and that privacy has facilitated the focussed *removal* of surplus functionality and methods, in a way that would probably be more difficult for a public project. The result is an MVO implementation which is particularly small, so if you don't want to depend on this random github repo & maven central, you can literally just copy and paste it into your app should you so wish (the core code hasn't changed in a while). There's also no reason you can't implement MVO yourself of course.


## Quick Start
Latest version: 0.11.1


```
implementation (group: 'co.early.fore', name: 'fore-core', version: '0.11.1', ext: 'aar')
```
optional:

```
implementation (group: 'co.early.fore', name: 'fore-adapters', version: '0.11.1', ext: 'aar')
implementation (group: 'co.early.fore', name: 'fore-retrofit', version: '0.11.1', ext: 'aar')
implementation (group: 'co.early.fore', name: 'fore-lifecycle', version: '0.11.1', ext: 'aar')
```
### Method Counts
![fore-core methods](https://img.shields.io/badge/fore--core-126-orange.svg)
![fore-adapters methods](https://img.shields.io/badge/fore--adapters-84-orange.svg)
![fore-retrofit methods](https://img.shields.io/badge/fore--retrofit-38-orange.svg)
![fore-lifecycle methods](https://img.shields.io/badge/fore--lifecycle-59-orange.svg)

See the [upgrading guide](https://erdo.github.io/android-fore/06-upgrading.html#shoom) if you're coming from an older version.


## New to fore

**If you're new to fore, Welcome! might I suggest:**

 1. Cloning this git repo
 2. Getting the example apps running (you'll need at least Android Studio 3)
 3. While referring to the code of the [sample apps](#sample-apps), dip in to the following sections of the site:
  [**MVO Overview**](https://erdo.github.io/android-fore/00-architecture.html#overview),
  [**Views**](https://erdo.github.io/android-fore/01-views.html#shoom), [**Models**](https://erdo.github.io/android-fore/02-models.html#shoom), [**Data Binding**](https://erdo.github.io/android-fore/03-databinding.html#shoom)

In a nutshell, developing with MVO means:

> "Observable **Models**; **Views** doing the observing; and some **Data Binding** tricks to tie it all together"


The **fore** library also includes some testable wrappers for AsyncTask: [**Async**](https://erdo.github.io/android-fore/04-extras.html#asynctasks-with-lambdas) and [**AsyncBuilder**](https://erdo.github.io/android-fore/04-extras.html#asyncbuilder) (which lets you make use of lambdas)

There are also optional extras that simplify [**adapter animations**](https://erdo.github.io/android-fore/04-extras.html#adapters-notifydatasetchangedauto) and abstract your networking layer when using [**Retrofit2**](https://erdo.github.io/android-fore/04-extras.html#retrofit-and-the-callprocessor).


## Sample Apps

![simple basket](img/screenshot_asaf_samples_phone_all_1000.png)

The apps here are deliberately sparse and ugly so that you can see exactly what they are doing. These are not examples for how to nicely structure XML layouts or implement ripple effects - all that you can do later in the **View** layers and it should have no impact on the stability of the app.

These apps are however, totally robust and comprehensively tested (and properly support rotation). And that's really where you should try to get to as quickly as possible, so that you can **then** start doing the fun stuff like adding beautiful graphics and cute animations.

For these sample apps, all the **View** components are located in the **ui/** package and the **Models** are in the **feature/** package. This package structure gives the app code good glanceability and should let you find what you want easily.

For the sample apps there is a one-to-one relationship between the sub-packages within **ui/**, and the sub-packages within **feature/** but it needn't be like that and for larger apps it often isn't. You might have one BasketModel but it will be serving both a main BasketView and a BasketIconView located in a toolbar for instance. A more complex view may use data from several different models at the same time eg a BasketModel and an AccountModel.

Aside from the apps, there is also a lot of information in this guide that will take you through the detail of how and why **fore** works.


### **fore** 1 Data Binding Example

[screen shot](https://raw.githubusercontent.com/erdo/android-fore/master/example01databinding/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafdatabinding) \| [source code](https://github.com/erdo/android-fore/tree/master/example01databinding)

This app is a bare bones implementation of **fore** databinding. No threading, no networking, no database access - just the minimum required to demonstrate [Data Binding](https://erdo.github.io/android-fore/03-databinding.html#shoom). It's still a full app though, supports rotation and has a full set of tests to go along with it.

In the app you move money from a "Savings" wallet to a "Mobile" wallet and then back again. Its inspiration is the diagram in the [architecture](https://erdo.github.io/android-fore/00-architecture.html#bad-diagram) section, although it sadly doesn't look quite as awesome as that diagram does.



### **fore** 2 Asynchronous Code Example

[screen shot](https://raw.githubusercontent.com/erdo/android-fore/master/example02threading/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafthreading) \| [source code](https://github.com/erdo/android-fore/tree/master/example02threading)

This one demonstrates asynchronous programming, and importantly how to test it. It uses ([Async](https://erdo.github.io/android-fore/04-extras.html#asynctask) and [AsyncBuilder](https://erdo.github.io/android-fore/04-extras.html#asyncbuilder)). Again, it's a bare bones (but complete and tested) app - just the minimum required to demonstrate asynchronous programming.

This app has a counter that you can increase by pressing a button (but it takes 20 seconds to do the increasing - during which time you can rotate the device, background the app etc). There are two methods demonstrated, one which allows you to publish progress, and one which lets you take advantage of lambda expressions.

It's really a very exciting app, I recently launched it on the play store and I'm just waiting for the inevitable interview requests to come through from arstechnica and techcrunch.



### **fore** 3 Adapter Example

[screen shot](https://raw.githubusercontent.com/erdo/android-fore/master/example03adapters/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafadapters) \| [source code](https://github.com/erdo/android-fore/tree/master/example03adapters)

This one demonstrates how to use [**adapters**](https://erdo.github.io/android-fore/04-extras.html#adapters-notifydatasetchangedauto) with **fore** (essentially call notifyDataSetChanged() inside the syncView() method).

It also demonstrates how to take advantage of the built in list animations that Android provides. Once you have set your adapter up correctly, you just call notifyDataSetChangedAuto() inside the syncView() method and **fore** will take care of all the notify changes work.

Two lists are displayed side to side so you can see the effect this has when adding or removing items. The "Simple" list is on the left, the "Advanced" one that uses notifyDataSetChangedAuto() is on the right.

As usual it's a complete and tested app but contains just the minimum required to demonstrate adapters. It's not been nominated for any design awards, as yet.



### **fore** 4 Retrofit Example

[screen shot](https://raw.githubusercontent.com/erdo/android-fore/master/example04retrofit/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafretrofit) \| [source code](https://github.com/erdo/android-fore/tree/master/example04retrofit)

Clicking the buttons in this app will perform a network request to some static files that are hosted on [Mocky](https://www.mocky.io/) (have you seen that thing? it's awesome). The first button gets a successful response, the last two get failed responses which are handled in two different ways. The first is a simple error, based on the HTTP code the app receives back from the server. The other is a more specific error based on parsing the body of the error response for an error object. That's managed by the [CallProcessor](https://erdo.github.io/android-fore/04-extras.html#retrofit-and-the-callprocessor) which is the main innovation in the fore-retrofit library.

As you're using the app, please notice:

- **how you can rotate the device with no loss of state or memory leaks**. I've used Mocky to add a 3 second delay to the network request so that you can rotate the app mid-request to clearly see how it behaves (because we have used **fore** to separate the view from everything else, rotating the app makes absolutely no difference to what the app is doing, and the network busy spinners remain totally consistent).

As usual this is a complete and tested app. In reality the tests are probably more than I would do for a real app this simple, but they should give you an idea of how you can do **unit testing**, **integration testing** and **UI testing** whilst steering clear of accidentally testing implementation details when using **fore**.


### **fore** 5 UI Helpers Example (Tic Tac Toe)

[screen shot](https://raw.githubusercontent.com/erdo/android-fore/master/example05ui/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafui) \| [source code](https://github.com/erdo/android-fore/tree/master/example05ui)

A regular Tic Tac Toe game that makes use of a few UI convenience classes:


- The [SyncableXXX](https://erdo.github.io/android-fore/04-extras.html#lifecycle-components) classes which reduce boiler plate slightly and automatically handle databinding (the adding and removing of observers in line with various lifecycle methods)

- [SyncTrigger](https://erdo.github.io/android-fore/04-extras.html#synctrigger) which bridges the gap between the observer pattern and one off triggers that you want to fire (such as displaying a win animation at the end of a game)


No automated tests for this app (but you should be getting the idea by now - sample apps 1-4 all have comprehensive tests included).


### **fore** 6 DB Example (Room db driven todo list)
<div class="shoom" id="fore-6-db-example-room"/>

[screen shot](https://raw.githubusercontent.com/erdo/android-fore/master/example06db/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafadapters2) \| [source code](https://github.com/erdo/android-fore/tree/master/example06db)

A Todo list on steroids that lets you:

- manually add 50 random todos at a time
- turn on a "boss mode" which randomly fills your list with even more todos over the following 10 seconds
- "work from home" which connects to the network and downloads 25 extra todos (up to 9 simultaneous network connections)
- randomly delete about 10% of your todos
- randomly change 10% of your outstanding todos to done

It's obviously ridiculously contrived, but the idea is to implement something that would be quite challenging and to see how little code you need in the view layer to do it.

It is driven by a Room db, and there are a few distinct architectural layers: as always there is a view layer and a model layer (in packages ui and feature). There is also a networking and a persistence layer. The UI layer is driven by the model which in turn is driven by the db.

All the database changes are done off the UI thread, RecyclerView animations are supported (for lists below 1000 rows) and the app is totally robust and supports rotation out of the box. For testing, please see example apps 1-4


### Other Full App Examples

- There is a full app example hosted in a separate repo: one branch for **pure DI**, one for **Dagger 2**
**[here](https://github.com/erdo/asaf-full-app-example)**

- The same app written in **Kotlin** (functional but probably a little more to do clean code wise)
**[here](https://github.com/erdo/asaf-full-app-example-kotlin)**

- The **pwned4android** sample app and android library use **fore** for databinding and the source is available for both. **[library](https://github.com/erdo/pwned4android)**, **[sample app (kotlin)](https://github.com/erdo/password123)**. (You might find the **[integration instructions](https://github.com/erdo/pwned4android#useage-the-long-version)** of that library enlightening as they are quite concise and focus on the requirements of the view layer only)


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
