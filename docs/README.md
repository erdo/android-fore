*Under construction!*


# ASAF
Alarmingly Simple Android Framework

## TL;DR

A few tiny framework classes that help you write android code that is **simple**, **robust** and **performant** for even the most complex commercial grade android application.

The library is the result of over half a decade of app development in teams comprising developers with various backgrounds and abilities. It focuses relentlessly on being clear and easy to understand. Addressing areas such as **testability**; **lifecycle management**; **UI consistency**; and **memory leaks** it lets the developer focus on the cool stuff and not have to worry about the usual android problems.



## Quick Start

...

//TODO gradle stuff

...


## What is it

ASAF is basically a light touch implementation of **MVVM** written for Android using the observer pattern. It could almost be considered **MV** as we don't need to make a distinction between Models and ViewModels. You can use it to implement **MVP** if you wish, but you might find that with this framework you don't really need the **P**.

It also includes a testable alternative for AsyncTask ([AsafTask](/04-asynchronous-code.html#asaftask) and [AsafTaskBuilder](/04-asynchronous-code.html#asaftaskbuilder), and formalises an approach to **simple one way data binding** using a **syncView()** method that never leaves your view out of sync with your model.

If you haven't coded with ASAF before, it's probably best to take a look at the sample app and literally copy and paste a feature and it's associated UI components (see the **feature/** and **ui/** packages) then change them from there. I will be writing a tutorial for each app as soon as I get a chance so that you can step through the process of writing an app from scratch. There is also a lot of information here already that will take you through the detail of how and why ASAF works.

At a very high level you will be writing observable and testable Model classes for all your logic and data, and getting your UI classes to observe these models for any changes so that they can update their views immediately.

In your app you'll need something to inject the feature models into your UI components like the **ObjectGraph** class does in the sample apps, if you're already using the **Dagger** library you can just use that instead.


## Background

Writing simple code turns out to be a lot harder than writing complicated code, ASAF is a small collection of framework type classes that can help you write a concise and robust commercial grade android application, with code that is as simple as possible. Used correctly these classes should help you avoid some of the more common problems of android development on a non trivial app: **testability**; **lifecycle management**; **UI consistency**; and **memory leaks**. It usually results in a very concise code base and of course, it supports rotation by default.

ASAF starts with the assumption that the clearer and more obvious code is, the easier it is to maintain, and the less likely it is that bugs will creep in when you're not looking.

Code can get really complicated, because sometimes requirements are really complicated. But that's not the whole story. In a typical android app there exists huge amounts of complex code that has nothing to do with business requirements, but eveything to do with work-arounds for the peculiarities of the platform.

> "In a typical android app there exists huge amounts of complex code that has nothing to do with business requirements"


For various reasons (see the FAQ for more details) android is a little "special" in that respect. It does a particularly bad job of seperating view code from the rest and if android is the only major platform your developers or yourself have experience of, you may be missing a serious trick that a lot of platforms have baked in from the get-go.

Luckily with a few techniques (and this framework if you like it), you can have all the benefits of a platform that properly seperates view code from the rest of it. And you might be surprised how much android code becomes uneccesasary when you take this approach to development.



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

