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

ASAF starts with the assumption that the clearer and more obvious code is, the easier it is to maintain, and the less likely it is that bugs will creep in when you're not looking.

Writing simple code is of course a lot harder than writing complicated code. And all code can get complicated - because often, once you get into the detail, requirements are complicated.

But that's not the whole story. There is usually also code which has nothing to do with features or business requirements, it's just there to handle the platform and tie things together. It's also where a lot of the bugs are found, and that's the code ASAF has in its sights.

The framework is basically a light touch implementation of **MVVM** written for Android using the observer pattern. Perhaps more appropriately it could be considered **MV** as we don't need to make a distinction between Models and ViewModels. It's small enough that you can use it to implement **MVP** if you wish.


![data binding](img/data-binding.png)


In any case, all the sample apps included here are written in the same way, resulting in an extremely concise code base which of course is robust enough to support rotation by default.

At a very high level you will be writing observable and testable **Model** classes for all your logic, with **View** layer classes observing these models for any changes so that they can update their views immediately.

> "Observable **Models**, **Views** doing the observing, and some **Data Binding** tricks to tie it all together"

ASAF also includes a testable alternative for AsyncTask ([AsafTask](/asaf-project/05-asynchronous-code.html#asaftask) and [AsafTaskBuilder](/asaf-project/05-asynchronous-code.html#asaftaskbuilder)), and formalises an approach to **simple one way data binding** using a **syncView()** method that never leaves your view out of sync with your model.

You might be surprised how much android code becomes uneccesasary when you take this approach to development.

### Check Out The Code

If you haven't coded with ASAF before, it's probably best to take a look at the sample app and literally copy and paste a feature and it's associated UI components (see the **feature/** and **ui/** packages) then change them from there.

There is also a lot of information (and more crap diagrams) in this guide that takes you through the detail of how and why ASAF works.



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

