

### Views:
Views are not just XML layouts, in Android views are composed of the **Activity**, **Fragment** and **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

In short they are no place to put business logic, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted such as:

- loosing data stored in memory (causing null pointers)
- maintaining UI consistency
- guarding against memory leaks

Sound familiar? I have no stats for this but I would guess that those issues account for well over half the bugs present in a typical android app.

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from manage their lifecycle and display the state of whatever model they are interested in. As you'll see in that package, click listeners and references to the UI widgets are in the subclassed Android View classes, and they reference model classes directly from there.


### Model to View comms

The models are observable and the views mainly do the observing.

When a model changes, it's the model's responsibility to notify all the observing classes.

When an observer is told that something changed, it is the observers responsibility to find out what the latest model state is.

More details on this process in the data binding section

### View to Model comms

The button click listeners etc are specified in the view classes and call methods directly on models.

//TODO example of a full view class
