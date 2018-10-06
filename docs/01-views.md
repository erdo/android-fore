
# Views
Views are not just XML layouts, in Android the classes that form the view layer of an app are not just the classes extending View either, they include the **Activity**, **Fragment** *and* **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

> "View layers are: ephemeral; tightly coupled to the context; slow to test"


In short they are no place to put business logic, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted such as:

- loosing data stored in memory (causing null pointers or requiring unecessary network calls)
- maintaining UI consistency
- guarding against memory leaks

It seems obvious but still, those issues account for a fairly large chunk of the bugs present in a typical android app.


## Examples

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from:

- manage their lifecycle
- route button clicks and other widget listeners to the right place (usually directly to a model class)
- correctly display the state of whatever models they are interested in.


Here are few examples:

- [Wallets View](https://github.com/erdo/android-fore/blob/master/example01databinding/src/main/java/foo/bar/example/foredatabinding/ui/wallet/WalletsView.java) which is referenced in [this XML](https://github.com/erdo/android-fore/blob/master/example01databinding/src/main/res/layout/fragment_wallet.xml)

- [Counter View](https://github.com/erdo/android-fore/blob/master/example02threading/src/main/java/foo/bar/example/forethreading/ui/CounterView.java) which is referenced in [this XML](https://github.com/erdo/android-fore/blob/master/example02threading/src/main/res/layout/fragment_counter.xml)



## Custom Android Views

You'll notice in the sample apps, nearly every view is explicitly called out as such by being named **LoginView** or similar, and those classes all extend an Android Layout class like **LinearLayout** (which itself extends from View). This means that they can be referenced directly in an XML Layout.

As much view related functionality gets put in these classes as possible, so unlike in many Android code bases you will have seen, the view elements like text fields and buttons also live here. (You can put them in the Activity or the Fragment classes if you insist on it - but take a moment to double check why you want to do that). Very occasionally, you need to get the Fragment or Activity involved because [Android gives you no choice](https://erdo.github.io/android-fore/05-extras.html#androids-original-mistake) in the matter, but it's often unnecessary.

This is part of the **fore** library philosophy of making things as clear as possible. If it's to do with the view, put it in a class called *View*. This also frees up the Fragment and Activity classes to do as little as possible except manage their lifecycles (which are considerably more complex than those of custom views).


The [data binding](https://erdo.github.io/android-fore/03-databinding.html#shoom) section has more details about how views and models communicate in **fore**.
