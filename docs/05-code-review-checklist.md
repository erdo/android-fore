

## Identifying Problems / How to smash code reviews
As mentioned, it can take a little bit of effort to understand how to use this library properly at first, so here is a list of warning signs you can use to highlight potentially incorrect code (this list is especially helpful for code reviews - these are all things I have seen from developers who have just been introduced to this library).


1) **Any code setting or updating view states that is not inside the syncView() method**. Example: "clickListener -> setDisabled". That's usually an indication that the developer might not understand why syncView() is designed like it is, and will almost certainly result in hard to identify UI consistency bugs when screens are rotated etc. Point them to the data binding section where it talks about syncView().

2) **Activities and Fragments that have more than a few lines of code in them**. Sometimes there are good reasons for putting code in Activities and Fragments, setting up bundles and intents for example, but you should be immediately suspicious of any errant code that gets into these classes. Often this code can be moved to a model class, safely away from tricky lifecycle issues and where it can also be more easily tested. (If you value your sanity and that of your team, you should make sure that there are absolutely **no** AsyncTask instances or networking code in any Activity or Fragment classes at all. Ever.)

3) **Code in a Fragment that casts it's parent activity and then calls that activity for further processing**. Again sometimes that is appropriate, but unfortunately it's a very common pattern that is often misused. The idea of course is to let a Fragment communicate with an Activity in a safe way. When this technique is used as a way to access fuctionality written in the parent activity which should really have been written in a model class in the first place, it just acts as a sticking plaster for a problem that should never have existed in the first place. The answer of course is to put that code in a model, inject that model into the fragment and let the fragment access it directly, that totally removes the dependence on the host Activty and removes a lot of boiler plate in the process.

4) **Adding or removing observers outside of android lifecycle methods**. I'm not saying there is never a good reason to do that (particularly if you want to set up one long living model to observe the state of another long living model). But it is a bit unusual and might warrant a rethink. It's usually a mistake (and a cause of memory leaks)

5) Wherever you see an **addObserver()** it's always worth checking that you can see the associated **removeObserver()** call to make sure references are being cleaned up and memory isn't being leaked.

6) **Any change of state (usually a setter method) in an observable model that doesn't end with a call to notifyObservers()**. Even if it's not necessary for the current implementation, by not notifying the observers here, we now have a model that only works in certain (undocumented) circumstamces. If someone else comes along and wants to observe your model and does not get a notification as expected when some state changes, something will break.

7) **Any getter method that does more than pass back an in-memory copy of the data asked for**. In order for the databinding to be performant, we want any getter methods to return fairly quickly. Try to front load any processing in the setters rather than the getters.

8) **Any observers that do anything other than sync their entire view** are usually (but not always) incorrect. Generally the observer just does one thing (sync the view), and this means you can use the same instance of that observer to register with several different models in the same view (it's another reason for not doing what's discussed in OAQ 1)

9) **Code or tests that makes assumptions about the number of times syncView() will be called** This is pretty important, you can't fire one off events based on syncView() being called (like starting an activity for example), because you are depending on being notifed by the model an exact number of times. The deal is that whenever something (anything) changes in the model, you will be notified. But you maybe be notified more than you expect, especially if the model is refatored at a later date to add new features or in some way the internal implementation of the model changes. In order to be robust, your syncView must make no assumptions about the number of times it may or may not be called.

10) **A syncView() that is more than 5-10 lines long and/or doesn't have one line to set an affirmative value for each propery of each UI element you are concered with**. Take a look at the how to write a good syncView() method under the data binding section.

11) **Any click listeners or text change listeners should generally be talking directly to model classes, or asking for navigation operations** for example: MyActivity.startMe(getContext). Occasionally it's useful for listeners to directly call syncView() to refresh the view (when an edit text field has changed for example). What they generally shouldn't be doing is accessing other view components like fragments or activites and checking their state in some way, if you follow this code it generally ends up calling a model class somewhere down the line, in which case the model class should just be called directly (you get your model references to any view class using dependency injection).

