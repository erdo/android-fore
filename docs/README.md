``[nothing much here yet, still writing tutorials and sample apps, android project is there so you can clone it to get the code]


# asaf
Alarmingly Simple Android Framework

## TL;DR

A few quick framework classes that help you write android code that is **simple** and **robust** for even the most complex android app. The library is tiny, but you you need to know how to use it, and it also helps to know why it is like it is.


## Quick Start

...


//TODO gradle stuff

...

Writing simple code turns out to be a lot harder than writing complicated code, ASAF is a small collection of framework type classes that can help you write a concise and robust commercial grade android application with code that is as simple as possible. Used correctly these classes should help you avoid some of the more common problems of android development on a non trivial app: **testability**; **lifecycle management**; **UI consistency**; and **memory leaks**. It usually results in a very concise code base and it supports rotation by default. Did I mention it's also very simple?

If you haven't coded with ASAF before, it's probably best to take a look at the sample app and literally copy and paste a feature and it's associated UI components (see the **feature/** and **ui/** packages) then change them from there. I will be writing a tutorial for each app as soon as I get a chance so that you can step through the process of writing an app from scratch.

In your app you'll need something to inject the feature models into your UI components like the **ObjectGraph** class does in the sample app, if you're already using the **Dagger** library you can just use that instead.

More details below, but essentially you will be writing observable and testable Model classes for all your logic and data, and getting your UI classes to observe these models for any changes so that they can update their views immediately.


## Architectural Approach
ASAF is basically a light touch implementation of **MVVM written for Android** using the observer pattern. It could almost be considered **MV** as we don't need to make a distinction between Models and ViewModels. You can use it to implement **MVP** if you wish, but you might find that with this framework you don't really need the **P**.

It also formalises an approach to **simple one way data binding** using a syncView() method that never leaves your view out of sync with your model.




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

