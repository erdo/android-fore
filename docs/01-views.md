
# Views
Views are not just XML layouts, in Android the classes that form the view layer of an app are not just the classes extending View either, they include the **Activity**, **Fragment** *and* **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

> "View layers are: ephemeral; tightly coupled to the context; slow to test"


In short they are no place to put business logic, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted such as:

- loosing data stored in memory (causing null pointers or requiring uneccessary network calls)
- maintaining UI consistency
- guarding against memory leaks

It seems obvious but still, those issues probably account for well over half the bugs present in a typical android app.


## Examples

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from:

- manage their lifecycle
- route button clicks and other widget listeners to the right place (usually directly to a model class)
- correctly display the state of whatever models they are interested in.


Here are few examples:

- [Wallets View](https://github.com/erdo/asaf-project/blob/master/example01databinding/src/main/java/foo/bar/example/asafdatabinding/ui/wallet/WalletsView.java) which is referenced in [this XML](https://github.com/erdo/asaf-project/blob/master/example01databinding/src/main/res/layout/fragment_wallet.xml)

- [Counter View](https://github.com/erdo/asaf-project/blob/master/example02threading/src/main/java/foo/bar/example/asafthreading/ui/CounterView.java) which is referenced in [this XML](https://github.com/erdo/asaf-project/blob/master/example02threading/src/main/res/layout/fragment_counter.xml)



## Custom Android Views

You'll notice in the sample apps, nearly every view is explicitly called out as such by being named **LoginView** or similar, and those classes all extend an Android Layout class like **LinearLayout** (which itself extends from View). This means that they can be referenced directly in an XML Layout.

As much view related functionality gets put in these classes as possible, so unlike in many Android code bases you will have seen, the view elements like text fields and buttons also live here. (You can put them in the Activity or the Fragment classes if you insist on it - but you should really take a moment to ask yourself why you want to do that).

This is part of the ASAF philosophy of making things as clear as possible. If it's to do with the view, put it in a class called *View. This also frees up the Fragment and Activity classes to do as little as possible except manage their lifecycles (which are considerably more complex than those of custom views).

The [data binding](/asaf-project/03-databinding.html#shoom) section has more details about how views and models communicate in ASAF.


## Ancient Android History

Sometimes, us Android developers (especially if we have only developed using Android during our career) can have a hard time understanding **in practice** how to separate view code from everything else (despite universally declaring it to be a good thing).

Unfortunately, right from its inception the Android platform was developed with almost no consideration for data binding or for a separation between view code and testable business logic, and that legacy remains to this day.

Instead of separating things *horizontally* in layers with views in one layer and data in another layer, the Android designers separated things *vertically*. Each self contained Activity (encorporating UI, data and logic) wrapped up in its own little reusable component. That's also probably why testing was such an afterthought for years with Android - if you architect your apps like this, testing them becomes extremely difficult.

Android seems to have been envisioned a little like this:

![vertical separation](img/vertical-separation.png)

A more standard way of looking at UI frameworks would have been to do something like this:

![horizontal separation](img/horizontal-separation.png)

I know, crap diagrams, but anyway a lot of the complication of Android development comes from treating the Activity class as some kind of reusable modular component and not as a thin view layer (which is what it really is).  Hacks like onSaveInstanceState() etc, are the result of fundamentally missing the basic requirement of (all?) UI platforms: the need to separate the view layer from everything else.

Despite the obvious problems of writing networking code or asynchronous code inside an ephemeral view layer, think about how many Android apps you've encountered that fill their Activity and Fragment classes with exactly that. And think about how much additional code is then required to deal with a simple screen rotation (or worse, how many apps simply disable screen rotation because of the extra headache). Sometimes even smart developers can fail to see the forrest for all the trees.

Fortunately it's almost all completely uneccessary. The [sample apps](https://erdo.github.io/asaf-project/#sample-apps) should clearly demonstrate just how clean android code can become once you start properly separating view code from everything else.

