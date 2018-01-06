# ASAF
**Alarmingly Simple Android Framework**

[(if you're reading this on github click here)](https://erdo.github.io/asaf-project/#shoom)

A few tiny framework classes which can help you write android code that is **simple**, **robust** and **performant**. The architecture is also highly **scalable**, supporting commercial grade android applications and complex UIs.  *(it really is tiny - asaf-core is less than 500 lines of code)*

ASAF addresses areas such as **testability**; **lifecycle management**; **UI consistency**; and **memory leaks** and it lets the developer focus on the cool stuff and not have to worry about the usual android problems. The ASAF architecture supports rotation **by default** - no additional work is required.

The library's lazer tight focus is the result of continuous iteration in private beta, during more than half a decade of app development in mixed ability teams. It's main goal is to be **clear** and **easy to understand**, which makes the apps it supports **robust**, **quick to develop**, and **easy to change**.



## Quick Start


```
compile (group: 'co.early.asaf', name: 'asaf-core', version: '0.9.24', ext: 'aar')
```
optional:

```
compile (group: 'co.early.asaf', name: 'asaf-adapters', version: '0.9.24', ext: 'aar')
compile (group: 'co.early.asaf', name: 'asaf-retrofit', version: '0.9.24', ext: 'aar')
compile (group: 'co.early.asaf', name: 'asaf-ui', version: '0.9.24', ext: 'aar')
```


**If you're not familiar with ASAF and you want to get up to speed quickly with how this library works from scratch, it's probably 1-2 hours work. I'd recommend:**

 1. Cloning the git repo
 2. Getting the example apps running (you'll need at least Android Studio 3)
 3. Reading the following sections on this site: [**Views**](https://erdo.github.io/asaf-project/01-views.html#shoom), [**Models**](https://erdo.github.io/asaf-project/02-models.html#shoom), [**Data Binding**](https://erdo.github.io/asaf-project/03-databinding.html#shoom) while refering to the code of the [sample apps](#sample-apps)


## Overview

There are many over-engineered android app architectures in existence - (probably because it's easy to write something that is over-engineered). What's surprising is that many of these architectures don't even get basic View / Model separation correct or they gloss over rotation support.

*(Try rotating a sample app or two and see if it triggers a network call each time - and if not, check for any ```if(firstTime){callNetwork()}``` style hacks that exist in the model layer - that's a sure sign that the separation between the view and model layers is a mirage. Now try adding a couple of seconds delay to the network call to simulate real behaviour - does the ui accurately reflect what's happening? are the "busy" indicators consistent? How about if you rotate the screen mid-network call... "busy" indicators no longer showing, even though there is a network call in progress? - you're looking at a broken data binding implementation causing a UI consistency problem)*

What's hard, is to produce something that is simple but also generically applicable - that often requires multiple iterations. ASAF (though now very stable) has been going through those iterations privately for years - and that privacy has facilitated the focussed *removal* of surplus functionality and methods, in a way that would be more difficult for a public project. Hopefully that will become obvious to you as you familiarize yourself with how to use ASAF in your own projects.

### Yes, but what is it?

The ASAF framework is basically a light touch implementation of MVVM written for Android using a custom implementation of the [Observer pattern](https://en.wikipedia.org/wiki/Observer_pattern) to implement data binding (not using android xml bindings).

You'll notice it uses custom views as a place to put all the view related code (this enables you to avoid many Activity/Fragment lifecycle problems that you might otherwise encounter). To help you do that, it makes extensive use of [dependency injection](https://erdo.github.io/asaf-project/04-more.html#dependency-injection). More about the architecture is [here](https://erdo.github.io/asaf-project/07-architecture.html#architecture), but probably the best place to learn is in the code of the [sample apps](#sample-apps).

Developing with ASAF generally means writing observable and testable [**Model**](https://erdo.github.io/asaf-project/02-models.html#shoom) classes for all your logic (or converting the models you already have to be Observable), and writing [**View**](https://erdo.github.io/asaf-project/01-views.html#shoom) layer classes which observe these models for any changes (so that the views can sync their UI / run animations etc.)

> "Observable **Models**; **Views** doing the observing; and some **Data Binding** tricks to tie it all together"

ASAF also includes some testable alternatives for AsyncTask ([**AsafTask**](https://erdo.github.io/asaf-project/04-more.html#asaftask) and [**AsafTaskBuilder**](https://erdo.github.io/asaf-project/04-more.html#asaftaskbuilder)), and formalises an approach to **simple one way data binding** using a [**syncView()**](https://erdo.github.io/asaf-project/03-databinding.html#syncview) method that **never** leaves your view out of sync with your model.

There are also optional extras that help with using [**adapters**](https://erdo.github.io/asaf-project/04-more.html#adapters) and working with [**Retrofit2**](https://erdo.github.io/asaf-project/04-more.html#retrofit-and-the-callprocessor).

You might be shocked at how much android code becomes unnecessary when you take this approach to development.

## Sample Apps

For these sample apps, all the **View** components are located in the **ui/** package and the **Models** are in the **feature/** package. This package structure gives the app code good glanceability and should let you find what you want easily.

For the sample apps there is a one-to-one relationship between the sub-packages within **ui/**, and the sub-packages within **feature/** but it needn't be like that and for larger apps it often isn't. You might have one BasketModel but it will be serving both a main BasketView and a BasketIconView located in a toolbar for instance. A more complex view may use data from several different models at the same time eg a BasketModel and an AccountModel.

The apps here are deliberately sparse and ugly so that you can see exactly what they are doing. These are not examples for how to nicely structure XML layouts or implement ripple effects - all that you can do later in the **View** layers and it should have no impact on the stability of the app.

ASAF has been designed to make the most of lambda expressions by the way, however most of the sample apps don't use lambdas - purely to make the code more accessible to people who aren't comfortable with them yet. Obviously replacing the anonymous inner classes with lambdas will make the code even tighter.

These apps are however, totally robust and comprehensively tested. And that's really where you should try to get to as quickly as possible, so that you can **then** start doing the fun stuff like adding beautiful graphics and cute animations.

Asside from the apps, there is also a lot of information in this guide that will take you through the detail of how and why ASAF works.


### ASAF 1 Data Binding Example

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example01databinding/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafdatabinding) \| [source code](https://github.com/erdo/asaf-project/tree/master/example01databinding)

This app is a bare bones implementation of ASAF databinding. No threading, no networking, no database access - just the minimum required to demostrate [**Data Binding**](https://erdo.github.io/asaf-project/03-databinding.html#shoom). It's still a full app though, supports rotation and has a full set of tests to go along with it.

In the app you move money from a "Savings" wallet to a "Mobile" wallet and then back again. Its inspiration is the diagram in the [architecture](https://erdo.github.io/asaf-project/07-architecture.html#bad-diagram) section, although it sadly doesn't look quite as awesome as that diagram does.



### ASAF 2 Asynchronous Code Example

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example02threading/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafthreading) \| [source code](https://github.com/erdo/asaf-project/tree/master/example02threading)

This one demonstrates asynchronous programing, and importantly how to test it. It uses ([**AsafTask**](https://erdo.github.io/asaf-project/04-more.html#asaftask) and [**AsafTaskBuilder**](https://erdo.github.io/asaf-project/04-more.html#asaftaskbuilder)). Again, it's a bare bones (but complete and tested) app - just the minimum required to demostrate asynchronous programing.

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

If you're using Retrofit (and I'm guessing you probably are), there are some nice ASAF classes that help you use [Retrofit2](https://erdo.github.io/asaf-project/04-more.html#retrofit-and-the-callprocessor) in a particularly clean and testable way. This is the example app for that.

Clicking the buttons will perform a network request to some static files that are hosted on [Mocky](https://www.mocky.io/) (have you seen that thing? it's awesome).

The first button gets a successful response, the last two get failed responses. The failed responses are handled in two different ways. The first is a simple error, based on the HTTP code the app receives back from the server. The other is a more specific error based on parsing the body of the error response for an error object. That's managed by the [CallProcessor](https://github.com/erdo/asaf-project/blob/master/asaf-retrofit/src/main/java/co/early/asaf/retrofit/CallProcessor.java) which is the main inovation in the asaf-retrofit library.

As you're using the app, notice:

- **how you can rotate the device with no loss of state or memory leaks**. I've used Mocky to add a 3 second delay to the network request so that you can rotate the app mid-request to clearly see how it behaves (because we have used ASAF to seperate the view from everything else, rotating the app makes absolutely no difference to what the app is doing).
- **how it is not possible to mess things up by speed tapping the buttons**. No matter how rapidly the testers can click multiple buttons, the app is totally robust. It is robust for two reasons: one is that the model checks to see if it's busy before starting anything anyway. The other is that all the button clicks and the network responses come through on the UI thread, see more on that in [model](https://erdo.github.io/asaf-project/02-models.html#shoom) section. Even with an android adapter involved, it would be impossible to get any problems when calling notifyDataSetChanged() as long as when any list data is changed it is changed on the UI thread and notifyDataSetChanged() is called **immediately** after - there will be a more compex standalone networking/adapter example that demonstrates that, but the best description for the moment is in the source of [ObservableImp](https://github.com/erdo/asaf-project/blob/master/asaf-core/src/main/java/co/early/asaf/core/observer/ObservableImp.java) where it talks about the notificationMode.

The app contains just the minimum code required to demonstrate networking (ok apart from an unecessary animated-tasty-rating-bar, but whatever, it's just one class ;p ).

As usual it's a complete and tested app. In reality the tests are probably more than I would do for a real app this simple, but they should give you an idea of how you can do **unit testing**, **integration testing** and **UI testing** whilst steering clear of accidentally testing implementation details when using ASAF.

I also hope you appreciate the lemon icons, I made them in Inkscape.



### ASAF 5 UI Helpers Example (Tic Tac Toe)

[screen shot](https://raw.githubusercontent.com/erdo/asaf-project/master/example05ui/screenshot.png) \| [playstore listing](https://play.google.com/store/apps/details?id=foo.bar.example.asafui) \| [source code](https://github.com/erdo/asaf-project/tree/master/example05ui)


#### SyncableX convenience classes

The first thing this app demonstrates the use of the [SyncableAppCompatActivity](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/activity/SyncableAppCompatActivity.java) class - see also: [SyncableActivity](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/activity/SyncableActivity.java), [SyncableSupportFragment](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/fragment/SyncableSupportFragment.java), [SyncableFragment](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/fragment/SyncableFragment.java) which all work in a similar fasion. These completely optional classes let you remove some lifecycle boiler plate from your custom view classes.

#### SyncTrigger

The second thing this app demonstrates is the use of the [SyncTrigger](https://github.com/erdo/asaf-project/blob/master/asaf-ui/src/main/java/co/early/asaf/ui/SyncTrigger.java) class. One thing the Observer pattern is not great for (when you add rotating android screens into the mix) is firing one-off events such as you might use for running single shot animations. The SyncTrigger class lets you bridge the gap between syncView() (which is called at any time, any number of times) and an event like an animation that must happen only once.

When using a SyncTrigger you need to implement a method to be run when it is triggered (e.g. running an animation), and also a method which will be used to check if some value is over a threshold (e.g. when a game state changes to WON).

The first time the threshold is breached i.e. checkThreshold() returns **true**, the trigger will be called. Once checkThreshold() again returns **false**, the trigger is reset.

Passing true in the check() method will cause the first checkThreshold() result to be ignored (so for example if the threshold has already been breached, the first check will not cause a trigger to occur). This is useful for not re-triggering just because your user rotated the device after receiving an initial trigger.

No tests on this one yet! when I get a second I will add them, at least for the Board class which contains all the logic.




### Other Full App Examples

//TODO - these aren't complete yet but they will be multiple activity apps, some using Dagger 2 for DI rather than the home grown ObjectGraph class and I'll be hosting them in a separate repo.



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


