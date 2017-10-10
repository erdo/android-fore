*Under construction!*


# ASAF
Alarmingly Simple Android Framework

A few tiny framework classes that help you write android code that is **simple**, **robust** and **performant** for even the most complex commercial grade android application.

The library is the result of over half a decade of app development in teams comprising developers with various backgrounds and abilities. It focuses relentlessly on being clear and easy to understand. Addressing areas such as **testability**; **lifecycle management**; **UI consistency**; and **memory leaks** it lets the developer focus on the cool stuff and not have to worry about the usual android problems.

Check out the [sample apps](#sample-apps) to see how it's done.


## Quick Start

...

//TODO gradle stuff

...


## Overview

ASAF starts with the assumption that the clearer and more obvious code is, the easier it is to maintain, and the less likely it is that bugs will creep in when you're not looking.

Writing simple code is of course a lot harder than writing complicated code. And all code can get complicated - because often, once you get into it, requirements are complicated.

But that's not the whole story. There is also code which has nothing to do with features or business requirements, it's just there to handle the platform and tie things together. Sometimes this code is sprinkled throughout an app, mixed in with the busines logic, hiding in plain sight. This code is also where a lot of the bugs are found. Developing with ASAF isolates this code, drastically simplifies it and leaves your business logic testable and out in the clear.

The framework is basically a light touch implementation of **MVVM** written for Android using the observer pattern. Perhaps more appropriately it could be considered **MV** as we don't need to make a distinction between Models and ViewModels. It's flexible enough that you can use it to implement **MVP** if you wish.

In any case, all the sample apps included here are written in the same way, this method results in an extremely concise code base, which of course is robust enough to support rotation and other context switches by default.


![data binding](img/data-binding.png)


At a very high level you will be writing observable and testable **Model** classes for all your logic, with **View** layer classes observing these models for any changes so that they can update their views immediately.

> "Observable **Models**; **Views** doing the observing; and some **Data Binding** tricks to tie it all together"

ASAF also includes a testable alternative for AsyncTask ([**AsafTask**](/asaf-project/05-asynchronous-code.html#asaftask) and [**AsafTaskBuilder**](/asaf-project/05-asynchronous-code.html#asaftaskbuilder)), and formalises an approach to **simple one way data binding** using a **syncView()** method that never leaves your view out of sync with your model.

You might be surprised how much android code becomes uneccesasary when you take this approach to development.

## Sample Apps

If you haven't coded with ASAF before, it's probably best to take a look at some of the  sample apps and literally copy and paste a feature and it's associated UI components (see the **feature/** and **ui/** packages) then change them from there.

As for the apps, they are deliberately sparse and ugly so that you can see exactly what they are doing. These are not examples for how to nicely structure XML layouts or implement ripple effects - all that you can do later in the **View** layers and it should have no impact on the stability of the app.

These apps are however, totally robust and comprehensively tested. And that's really where you should try to get to as quickly as possible, so that you can **then** start doing the fun stuff like adding beautiful graphics and cute animations.

Asside from the apps, there is also a lot of information (and more crap diagrams) in this guide that will take you through the detail of how and why ASAF works.


### Data Binding Example
This app is a bare bones implementation ASAF databinding. No threading, no networking, no database access - just the minimum required to demostrate [**Data Binding**](/03-databinding.html). It's still a full app though, supports rotation and has a full set of tests to go alog with it.

You move money from a "Savings" wallet to a "Mobile" wallet and then back again. It's inspiration is the diagram above, although it sadly doesn't look quite as awesome as that diagram.

[Data Binding Example App Source Code](/asaf-project/tree/master/exampledatabinding)


### Asynchronous Code Example
This one demostrates asynchronous programing, and importantly how to test it. It uses ([**AsafTask**](/asaf-project/05-asynchronous-code.html#asaftask) and [**AsafTaskBuilder**](/asaf-project/05-asynchronous-code.html#asaftaskbuilder)). Again, it's a bare bones (but complete and tested) app - just the minimum required to demostrate asynchronous programing.

This app has a counter that you can increase by pressing a button (but it takes 20 seconds to do the increasing - during which time you can rotate the device, background the app etc). There are two methods demonstrated, one which allows you to publish progress, one which lets you take advantage of lambda expressions.

It's really a very exciting app, I should probably put it on the play store before someone steals the idea.

[Asynchronous Example App Source Code](/asaf-project/tree/master/examplethreading)



### Networking Code Example

//TODO


### Basic Twitter Client Example

//TODO


### Tic Tac Toe Example

//TODO



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

