
# Version information

Since we've been publishing on <strike>jcenter</strike> & mavenCentral, the core code has remained almost identical. Most version number bumps have been due to updating dependencies, adding new classes to the optional packages, and occasionally tidying up the naming or the API (the version numbers for all the packages are incremented at the same time so that they will always match - this means some version bumps have no effect for a particular package).

**kotlin** API android package
```
co.early.fore:fore-kt-android
```

**java** API android package
```
co.early.fore:fore-jv-android
```

The fore library is divided into optional packages which you can pull in independently if you want (though the fat package is so small, it doesn't really seem worth it IMO).

**kotlin** API
```
co.early.fore:fore-kt-core
co.early.fore:fore-kt-android-core
co.early.fore:fore-kt-android-adapters
co.early.fore:fore-kt-android-network
```

**java** API
```
co.early.fore:fore-jv-core
co.early.fore:fore-jv-android-core
co.early.fore:fore-jv-android-adapters
co.early.fore:fore-jv-android-network
```

To check what versions of what transitive dependencies each package pulls in, the definitive answer is found in the pom files hosted at [mavenCentral](https://repo1.maven.org/maven2/co/early/fore/). The other packages you will see in mavenCentral have been rolled in to the packages listed above and are no longer updated. The GPG fingerprint used to sign the maven packages is: <strong>5B83EC7248CCAEED24076AF87D1CC9121D51BA24</strong> and the GPG public cert is [here](https://erdo.github.io/android-fore/gpg-pub-cert.asc).

## fore 1.5.0
**fore 1.5.0** bumps the kotlin version to **1.5.31** and adds a CallProcessor class (marked as experimental) supporting Apollo3 3.0.0-alpha07. A new package called **fore-kt-android-compose:1.1.0-alpha05** adds an extension function to use with jetpack compose 1.1.0-alpha05 (also marked as experimental).

## fore 1.4.7
**fore 1.4.7** bumps the kotlin version to **1.5.10** so if you haven't updgrade your project to kotin 1.5.10 yet, you might encounter strange problems with obfuscation or other issues. If you do, drop down to **fore 1.4.6** or upgrade kotlin in your project.

## fore 1.4.1
**fore 1.4.1** to **fore 1.4.6** inclusive are built with **kotlin 1.4.30**

## OkHttp3
As **fore** now wraps **Retrofit2**, **Apollo** or **Ktor** calls with a single package, the network logs need to work out which version of OkHttp3 your app is running (Retrofit2 and Apollo use v3.x.x, Ktor uses 4.x.x and these versions of OkHttp3 have slightly different APIs). From **1.3.7** we do this with reflection, but feel free to exclude the kotlin-reflect package from your release builds as follows: `exclude("org.jetbrains.kotlin", "kotlin-reflect")`, you just won't see any network logs when using the InterceptorLogging class (which you probably have turned off for release builds anyway).

## Ktor Client Support
**1.3.6** adds support for **Ktor**, it wraps the calls using a CallProcessor in a similar way to how Retrofit2 and Apollo is handled.

## fore-lifecycle deprecation
**1.3.5** was the last version of the fore-lifecycle package that will be published on mavenCentral. The useful classes from this package were moved into fore-core a while ago and with the ObservableGoup interface, most of what fore-lifecycle did can be done yourself pretty easily anyway. 1.3.5 won't be going anywhere of course and you can still pull it in individually if you want it.

## jcenter removal
**1.3.4** is the last version that was published on jcenter, mavenCentral has most versions since **1.1.0** (if for some reason you need an older version, please open an issue and I might be able to sign one and put it on mavenCentral for you). **All new versions will be released to mavenCentral only**. All references to jcenter have been removed from the project so it will continue to build after jcenter has been closed.

## GraphQL Support
**1.3.1** adds support for **Apollo**, it wraps the calls using a CallProcessor in a similar way to how Retrofit2 is handled.

## kotlin 1.4
**1.2.1** bumps the kotlin version to 1.4 so if you haven't updgrade your project to kotin 1.4 yet, you might encounter strange problems with obfuscation, if so drop down to **fore 1.2.0** or upgrade kotlin in your project, see [this issue](https://github.com/erdo/android-fore/issues/72)

## defaults for WorkMode, Logger and SystemTimeWrapper

From **1.2.0** the kotlin APIs will set default values for these parameters if you don't specify them. See more [here](https://erdo.github.io/android-fore/04-more-fore.html#default-params).

## removing deprecated sync views

These were removed for **1.1.0** (1.0.6 is identical to 1.1.0 but still has those syncviews).

## AS4

**1.1.0** is also the last version to support AS3

## kotlin support

**1.0.6** introduced explicit kotlin support with the packages **fore-core-kt** and **fore-retrofit-kt** (they're based on coroutines and fore-retrofit-kt's CallProcessor has a more functional API that uses suspend functions).

As a convenience, **1.0.6** also introduces the packages **fore-jv** and **fore-kt**, these contain all the classes from fore (suitable for java or kotlin as appropriate) in one aar file so you only have to add one line of dependency in your build file. Some classes are removed, for instance fore-kt does not contain the java based CallProcessor, only the kotlin one.

## android studio 3.5+

It used to be convenient to put the .idea/modules.xml file into source control, but now it messes things up, so with commit a1766e17f80fdc1b43e4176cfc6d60094322c83d .idea/modules.xml is removed and the whole of the .idea folder is put in .gitignore.

There's no need to do anything when you pull the latest code, but if you want to be sure your project is all setup nicely you just have to: close Android Studio; delete the .idea directory (it's a hidden directory by the way); restart Android Studio.

## androidx support from 1.0.0 onwards

Versions from **1.0.0** support **androidx**, if you are not using androidx yet you can continue to use 0.11.1

There was one minor breaking change previous to 0.11.1 (sorry):

* SuccessCallBack -> SuccessCallback (case change)

## Post the rename from ASAF to fore (&lt;0.9.32)

These are more substantial, but are mainly related to renaming or shuffling packages around, there are no functionality changes.

* most of the packages have changed from **co.early.asaf** to **co.early.fore**
* the deprecated classes **SimpleChangeAwareAdapter** and **SimpleChangeAwareList** have finally been removed (you should use **ChangeAwareAdapter** and **ChangeAwareList** as appropriate)
* the deprecated **SyncTrigger** methods: **resetAfterCheckFails(), resetAfterCheckAlways(), setImmediatelyResetAfterCheck()** have now been removed. Use the construction parameter: **ResetRule** instead.
* the deprecated **SyncTrigger** method: **check(boolean swallowTriggerForFirstCheck)** has been made private. Use **check()** and **checkLazy()** instead (checkLazy() swallows the trigger on first check so it's equivalent to calling the old check(false))
* **SyncTrigger** has also been moved into **fore-core** and placed under the **ui** package
* **AsafTask** and **AsafTaskBuilder** have been renamed **Async** and **AsyncBuilder** accordingly
