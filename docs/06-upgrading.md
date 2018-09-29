
# Upgrading from previous versions

Since we've been publishing on jcenter the core functionality has remained the same, most version number bumps have been due to adding new classes to the optional packages (the version numbers for all the packages are incremented at the same time so that they will always match - but that means some version bumps have no effect for a particular package).

There was one minor breaking change though (sorry):

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