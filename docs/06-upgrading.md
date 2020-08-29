
# Upgrading from previous versions

Since we've been publishing on jcenter the core functionality has remained the same, most version number bumps have been due to adding new classes to the optional packages (the version numbers for all the packages are incremented at the same time so that they will always match - but that means some version bumps have no effect for a particular package).

## default parameters for WorkMode, Logger and SystemTimeWrapper

From **1.2.0** the kotlin APIs will set default values for these parameters if you don't specify them. See more [here](https://erdo.github.io/android-fore/04-more-fore.html#default-params)

## removing deprecated sync views

These were removed for **1.1.0** (1.0.6 is identical to 1.1.0 but still has those syncviews).

## kotlin support

Of course, being java you can use any version of **fore** with kotlin, but **1.0.6** introduces explicit kotlin support with the packages **fore-core-kt** and **fore-retrofit-kt** (they're based on coroutines and fore-retrofit-kt's CallProcessor has a more functional API that uses suspend functions).

As a convenience, **1.0.6** also introduces the packages **fore-jv** and **fore-kt**, these contain all the classes from fore (suitable for java or kotlin as appropriate) in one aar file so you only have to add one line of dependency in your build file. Some classes are removed, for instance fore-kt does not contain the java based CallProcessor, only the kotlin one.

## android studio 3.5+

It used to be convenient to put the .idea/modules.xml file into source control, but now it messes things up, so with commit a1766e17f80fdc1b43e4176cfc6d60094322c83d .idea/modules.xml is removed and the whole of the .idea folder is put in .gitignore.

There's no need to do anything when you pull the latest code, but if you want to be sure your project is all setup nicely you just have to: close Android Studio; delete the .idea directory (it's a hidden directory by the way); restart Android Studio.

## androidx support from 1.0.0 onwards

Versions from **1.0.0** support **androidx**, if you are not using androidx yet you can continue to use 0.11.1

There was one minor breaking change previous to 0.11.1 (sorry):

* SuccessCallBack -> SuccessCallback (case change)

## Post the rename from ASAF to fore (>0.9.32)

These are more substantial, but are mainly related to renaming or shuffling packages around, there are no functionality changes.

* most of the packages have changed from **co.early.asaf** to **co.early.fore**
* the **asaf-ui** package has been renamed to **fore-lifecycle** to better reflect what it is
* the deprecated classes **SimpleChangeAwareAdapter** and **SimpleChangeAwareList** have finally been removed (you should use **ChangeAwareAdapter** and **ChangeAwareList** as appropriate)
* the deprecated **SyncTrigger** methods: **resetAfterCheckFails(), resetAfterCheckAlways(), setImmediatelyResetAfterCheck()** have now been removed. Use the construction parameter: **ResetRule** instead.
* the deprecated **SyncTrigger** method: **check(boolean swallowTriggerForFirstCheck)** has been made private. Use **check()** and **checkLazy()** instead (checkLazy() swallows the trigger on first check so it's equivalent to calling the old check(false))
* **SyncTrigger** has also been moved into **fore-core** and placed under the **ui** package
* **AsafTask** and **AsafTaskBuilder** have been renamed **Async** and **AsyncBuilder** accordingly
