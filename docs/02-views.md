

# Views
Views are not just XML layouts, in Android the classes that form the view layer of an app are not just the classes extending View either, they include the **Activity**, **Fragment** *and* **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

> "View layers are: ephemeral; tightly coupled to the context; slow to test"


In short they are no place to put business logic, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted such as:

- loosing data stored in memory (causing null pointers)
- maintaining UI consistency
- guarding against memory leaks

Sound familiar? I have no stats for this but I would guess that those issues account for well over half the bugs present in a typical android app.

There is more in the FAQ about Views and the mistakes that were made in the original Android design.

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from manage their lifecycle and display the state of whatever model they are interested in. As you'll see in that package, click listeners and references to the UI widgets are also in the subclassed Android View classes, and they reference model classes directly from there.


## Custom Android Views

You'll notice in the sample apps, nearly every view is explicitly called out as such by being named **LoginView** or similar, and those classes all extend an Android Layout class like **LinearLayout** (which itself extends from View). This means that they can be referenced directly in an XML Layout.

As much view related functionality gets put in these classes as possible, so unlike in many Android code bases you will have seen, the view elements like text fields and buttons all live here.

This is part of the ASAF philosophy of making things as clear as possible. If it's to do with the view, put it in a class called *View. This also frees up the Fragment and Activity classes to do as little as possible except manage their lifecycles (which are considerably more complex than those of the custom views).

The data binding section has more details about how views and models communicate in ASAF.
