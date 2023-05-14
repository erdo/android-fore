# <a name="somethingchanged-parameter"></a>fore deep dive

Much of what we are about to discuss is specific to mobile applications, the server side is a very different beast. Of course, applying best practice from one context to a totally different context without recognising the differences would be a mistake that we are going to avoid here!

## Server side and Client side are different
A lot of "developer advice internet" is implicitly talking about the server side. Unfortunately this advice is sometimes absorbed and repeated unthinkingly for a mobile context, without the new author realising that the prior _assumptions_ on which the advice was built, are no longer relevant in their new context.

**Stateless vs Stateful** is a great example of this. Stateless microservices on the server-side have a lot going for them. While potentially less intuitive and sometimes less performant, stateless is easier to scale using cloud services, stateless can be more robust (state can be hard to recover after a system crash, on the server side which can potentially affect millions of users at once), and if the service can be designed to not even need a database, it can be significantly less expensive to maintain and meet SLAs with that service. There are therefore good reasons that a lot of "developer advice internet" is pro stateless architectures!

None of those considerations are applicable to a mobile client. But there are more universal state considerations that _do_ apply, no matter what the context.

## State
Something that's pretty universal: if we're not careful, state (and especially duplication of state) can be a source of complexity, and therefore of bugs. That's a strong argument for having a crystal clear, single source of truth for your state.

> "state and its duplication can be a significant source of complexity, and therefore of bugs"

- The source of truth for state in "stateless" mobile architectures implemented with reactive streams is often "in the stream" and can be spread across multiple useCases returning Flows (potentially backed by StateFlows, SharedFlows, Channels etc, each with their own caching or replay idiosyncrasies)

- If not "in the stream", the truth will be kept in network caches, databases or other data sources and re-read / parsed each time it is required (which can have a significant impact on how "sluggish" an app feels to a user - many production apps in the wild have this problem).

Sharing this state with various components (UI or otherwise) can start to be problematic as soon as the app becomes more complex than a collection of simple independent pages of data - can we be sure that the whole app has the same view of state when it's being accessed via different usecase instances and from different co-routine contexts?

(I dare say we can, but it's rare to encounter a project with sufficient rigor and consistency to ensure that - once the number of useCases starts to balloon, and especially considering the ease with which you can switch coroutine contexts)

Even _accessing_ that truth suddenly becomes non-trivial. If you want to check a Boolean over a reactive stream, you will at the very least need to write code that launches a coroutine, chooses an appropriate dispatcher, and collects a Flow. You'll also need to ensure you don't accidentally leak memory while doing that, and your tests will also become marginally more complicated. And this tax is being paid, for no particular reason other than deciding to access state via a reactive stream.

Those fairly common situations are a good argument against using reactive streams in a mobile app unless absolutely necessary (it's necessary when you need backpressure to handle multiple streams of asynchronous data from say an IoT device see:[reactive-streams.org](http://www.reactive-streams.org/))

> "And this tax is being paid, for no particular reason other than deciding to access state via a reactive stream"

fore's approach to state is to keep the source of truth inside the relevant model classes e.g: accountModel.state.balance, internally managed, always consistent from the perspective of the UI thread, always easily and immediately _readable_ from anywhere, (and persistently stored to disk with something like a db or [PerSista](https://github.com/erdo/persista) if it's something you want to recover in the event of process-death).

## Mobile's unique context

The mobile context has its own, unique considerations such as:
- mobile clients have a UI thread
- view layers come into and out of existence from something like a device rotation (i.e. it's easy to cause memory leaks: see rxJava's checkered history with Android and memory leaks [example](https://medium.com/@scanarch/how-to-leak-memory-with-subscriptions-in-rxjava-ae0ef01ad361))
- the devices have low processor speeds, but they have high performance requirements such that any screen "jank" significantly affects a user's perception of speed.

This fits pretty well with the way that fore works. fore enables your code to operate almost exclusively in synchronous mode (i.e. on the UI thread) which means much less unnecessary suspend / co-routine theatre gets written, especially in the UI layer. The UI layers being thinner also require far less boiler plate to ensure memory leak free code. Performance is also extremely snappy as the state is available in memory for immediate rendering on a UI (and fetched or saved asynchronously, away from the view layer)

## Reactive Streams

So far these docs probably seem quite anti reactive streams! but that's not the case. It's simply a plea to introduce a powerful tool like reactive streams mindfully, when it makes sense to do so, with a recognition of how it will change your code base if it's used for a purpose it is not designed for.

Anyway by now I'm hoping that you're considering the possibility that using reactive streams to tie architectural layers together in a mobile client app may only be providing a local maximum in terms of performance and code clarity, and that there is another way...

Actually let's backup a little first and discuss reactive streams itself (which is a term many users of RxJava of Flow are a little vague about). Both RxJava and Kotlin Flow are implementations of the reactive streams initiative. Reactive Streams could just as well have been called Observable Streams, and you can consider it a combination of two concepts:

- Observers (tell me whenever you've changed)
- Streams (data and operators like .map .filter etc)

For some specific use cases: handling streams of changing data which you want to process on various threads (like the raw output of an IoT device for example) reactive streams is a natural fit. That's especially true when it comes to **back pressure**.

### Back pressure

Back pressure refers to the problem of data being *produced*, faster than it is able to be *consumed*. Handling back pressure in streams of data is basically what [reactive streams](http://www.reactive-streams.org/) lives for. The needs of most android app architectures however tend to be a little more along the lines of:

 - connect to a network to download discreet pieces of data (_always_ on an IO thread, takes _seconds_)
 - update a UI, based on some change of state (_always_ on the UI thread, takes _milliseconds_)

The timescales that these UI state changes happen in [loading=true, (wait), loading=false], are orders of magnitude slower than the timescales that would require back pressure management.

To put that another way: the **production** of data in an app (a user logs in, and a session token is fetched from the network) tends to happen in the order of **seconds**. The **consumption** of that data (the waiting spinner on the ui is re-rendered from visible to invisible) is often a **sub-millisecond** affair.

This is very obviously not a situation that reactive streams was designed to help with (unlike processing streaming video for example).

If you weren't there from the beginning, you might wonder why RxJava became popular in android architectures in the first place. I have some theories about that (of course I do 😂) [\[1\]](#1)

## The fore approach

Those two concepts we mentioned above (Observers / Streams) can be treated separately. We can consider a piece of code's _observable nature_ separate to the _data that actually changed_ . And that's what fore does - it deals exclusively with the first, letting you handle the second however you wish. This means your function signatures don't have to change, you can continue returning a Boolean if that's what you need to do, and Observable&lt;Something&gt;s or Flow&lt;Something&gt;s won't slowly spread throughout your code base unnecessarily.

One of the benefits of this is that it lets you isolate asynchronous code styles, to code that actually needs to be asynchronous (db access, network connections, calculation work etc). The rest of the code remains explicitly synchronous, on the UI thread, and highly predictable / testable because of it.

### The UI thread

The funny thing is... if you're writing an app that has a UI, much of the code that you write will be on the UI thread _anyway_.

Take a very common pattern with reactive streams based android apps: an app **collecting a Flow in a ViewModel to update its UI**. That code _still_ runs on the UI thread, even though it's written with all the trappings of asynchronous reactive streams 🧐 Android prevents you from shooting yourself in the foot here - for a change ;) and viewModelScope is bound to the UI thread - this is why you can update the UI from inside it without needing to switch to the UI thread first. (It's the same reason that fore's syncView() is always called from the UI thread).

### What if we just pretend?

You certainly _can_ treat everything as a reactive stream if you wish, and if parts of your app actually aren't a great match for reactive streams, you can (and sometimes must) have your functions return Single&lt;Whatever&gt;s. Unfortunately regular code that touches reactive streams often gets _reactive-streamified_ like this, giving it unasked-for complexity (even code that isn't, and has no need to be asynchronous or reactive, let alone reactive streams, in the first place).[\[2\]](#2)

So consider a world where we remove this pretend asynchronous code from the view layer and strip out as much observer and memory-leak-management boiler plate as possible, it takes us closer to the fundamental minimum requirements of a UI layer: "what things look like"

> "reduce view layer code to its absolute fundamentals: what things look like"

## fore's API

fore's observer API is very simple, it's a single, parameter-less function: **somethingChanged()** which is called on the UI thread whenever the model in question has changed.

The function doesn't include a parameter for the actual data that changed, so why not add that? This is the obvious question for anyone familiar with reactive streams based APIs like RxJava or Kotlin Flow, or indeed anyone who worked with the messenger bus APIs that used to be popular on Android like EventBus by GreenRobot or Otto by Square. And actually, in the distant past (long before publishing) the fore Observer interface did briefly have a generic on it along the lines of Observable&lt;SomeClass&gt; which supported this behaviour. But it was removed when we realised that adding it had *significantly increased* the amount of boiler plate and view code that had to be written.

> "adding generics to the API *significantly increases* the amount of boiler-plate required to observe multiple data sources"

If you're sceptical about that - and why wouldn't you be? I'd encourage you to do a before and after comparison in a small sample app, there is nothing quite like seeing it for yourself. You could use one of the fore samples to get started [very simple app](https://github.com/erdo/persista/tree/main/example-app) or [clean modules app](https://github.com/erdo/clean-modules-sample) - make sure to consider what happens once you add more data sources that need to be observed.

The reason fundamentally boils down to the fact that UI layers (ViewModels/Activities/Fragments) typically want things from more than one source (UseCase/Repository/DataSource). And if you're passing the data via your observable API, **observers will be tied to the Type**, and can't be shared across observables.

> "Observers will be tied to a Type"

### LiveData
Let's take a LiveData example (a similar issue presents itself with RxJava or Flow - or EventBus for that matter). We need a different observer instance for each type

```
emailInbox.unreadCountLiveData.observer(this, Observer { unread -> // Int
  // update the view / presenter / viewmodel / viewState / whatever
})
accountModel.hasSessionTokenLiveData.observer(this, Observer { hasToken -> // Boolean
  // update the view / presenter / viewmodel / viewState / whatever
})
accountModel.lastLoggedInTimeStampLiveData.observer(this, Observer { timeStamp -> // Long
  // update the view / presenter / viewmodel / viewState / whatever
})
accountModel.accountStateLiveData.observer(this, Observer { status -> // Enum
  // update the view / presenter / viewmodel / viewState / whatever
})
weatherModel.weatherForecastLiveData.observer(this, Observer { forecast -> // String
  // update the view / presenter / viewmodel / viewState / whatever
})
weatherModel.temperatureLiveData.observer(this, Observer { temperature -> // Float
  // update the view / presenter / viewmodel / viewState / whatever
})
```

### Flow

Let's try it with Flow

```
scope.launch {
    observeUnreadCountUseCase().collect { unread -> // Int
        // update the view / presenter / viewmodel / viewState / whatever
    }
    observeHasSessionTokenUseCase().collect { hasToken -> // Boolean
        // update the view / presenter / viewmodel / viewState / whatever
    }
    observeLastLoggedInTimeStampUseCase().collect { timeStamp -> // Long
        // update the view / presenter / viewmodel / viewState / whatever
    }
    observeAccountStatusUseCase().collect { status -> // Enum
        // update the view / presenter / viewmodel / viewState / whatever
    }
    observeWeatherForecastUseCase().collect { forecast -> // String
        // update the view / presenter / viewmodel / viewState / whatever
    }
    observeTemperatureUseCase().collect { temperature -> // Float
        // update the view / presenter / viewmodel / viewState / whatever
    }
}
```

Both Flow and RxJava do have dedicated function implementations for combining: **two things**, **three things** etc (Flow's combine functions go up to **five things**).

```
scope.launch {
    val combinedFlow: Flow<ViewState> = combine(
        observeUnreadCountUseCase(),
        observeHasSessionTokenUseCase(),
        observeLastLoggedInTimeStampUseCase(),
        observeAccountStatusUseCase(),
        observeWeatherForecastUseCase(),
    ) { unread, hasToken, timeStamp, status, forecast ->
        ViewState (
            unread = unread, // Int
            hasToken = hasToken, // Boolean
            timeStamp = timeStamp, // Long
            status = status, // Enum
            forecast = forecast, // String
        )
    }
}
```

Even here though you can see the API slightly creaking under the weight of the Types being returned. If we stick to a maximum of 5 Flows then we're ok. But beyond that, we need to switch to the vararg function, which then provides us an Array with the type information lost, leaving us no option but to cast values

```
scope.launch {
    val combinedFlow: Flow<ViewState> = combine(
        observeUnreadCountUseCase(),
        observeHasSessionTokenUseCase(),
        observeLastLoggedInTimeStampUseCase(),
        observeAccountStatusUseCase(),
        observeWeatherForecastUseCase(),
        observeTemperatureUseCase(),
    ) { array -> // Array<Any>
        ViewState(
            unread = array[0] as Int,
            hasToken = array[1] as Boolean,
            timeStamp = array[2] as Long,
            status = array[3] as Enum,
            forecast = array[4] as String,
            temperature = array[5] as Float,
        )
    }
}
```

Flow isn't that bad here (if you know how to use it, and can stomach the casting) but all the other issues still exist. At the end of the day it's an enormous and complicated API which is not particularly well suited, nor designed for, the simple task of reactively tieing architectural layers together in an app.

*If you think this example is a little extreme by the way, at the time of writing I am part of the dev team of a fairly popular app (500k+ users), whose main UI reactively updates itself based on 7 different observable data sources*

### fore

Even here, fore has a slight advantage over Flow because of fore's stupidly simple API (no need to use suspend functions or manage a scope with fore, and of course no need for casting)

```
lifecycle.addObserver(
    LifecycleObserver(this, emailInbox, accountModel, weatherModel)
)

...

fun syncView(){
    ViewState(
        unread = emailInbox.unread,
        hasToken = accountModel.hasSessionToken,
        timeStamp = accountModel.lastLoggedInTimeStamp,
        status = accountModel.status,
        forecast = weatherModel.forecast,
        temperature = weatherModel.temperatire,
    )
)
```

Once in a compose UI though, the three finally start to get close to parity

```
// LiveData
val viewState by viewModel.viewStateLiveData.observeAsState(ViewState())
// Flow
val viewState by viewModel.viewStateFlow.collectAsState(ViewState())
// Fore
val viewState by viewModel.observeAsState { viewModel.state }
```

[This section](https://dev.to/erdo/tutorial-android-fore-basics-1155#now-for-the-really-cool-stuff) of the dev.to tutorial on fore basics is getting pretty dated now, but it's worth a read for historical interest.

#### <a name="1"></a> [1] Android meets reactive streams
I think there were a few reasons that RxJava exploded in popularity when it arrived on the android scene. Firstly: every one hated AsyncTask (although you could always wrap it, and once you were able to [give it a lamda interface](https://erdo.github.io/android-fore/04-more-fore.html#asynctasks-with-lambdas) it was actually pretty ok - but not many people were aware you could do that). The second often stated reason was that it could help prevent "callback-hell", there are some pretty decent ways of [handling that](https://dev.to/erdo/intro-to-eithers-in-android-2om9#so-you-said-eithers-were-good) in kotlin nowadays regardless. Apart from those (no longer relevant) arguments...it seems a little uncharitable to say so, but the rx cool-juice was once pretty strong (maybe because it was difficult to master, and once you mastered it you were justifiably proud of that).

There was also a serious dearth of knowledge around at the time, maybe because android was so new, I remember encountering people who thought RxJava had literally invented the observer pattern(!)

And as for Kotlin Flow? Flow is a much better RxJava in Android, so if the team is already heavily invested in a reactive streams way of thinking, there is a clear mental migration path from RxJava to Flow (which also has the benefit of letting us completely avoid facing up to the sunk cost of learning reactive streams in the first place!).

#### <a name="2"></a> [2] Reactive stream tentacles
This is how reactive streams can unintentionally spread complexity throughout a code base. When this **tendency-to-spread** is combined with a large non-obvious API, and a focus on asynchronicity even when none is required it can quite easily swamp otherwise fairly trivial app projects. That risk is increased on larger projects employing developers with a mixture of skill levels, especially where there is a reasonably high turn over of developers. Keeping control of ever ballooning complexity in these situations can be a significant challenge.

This is somewhat related to the famous [what color is your function](http://journal.stuffwithstuff.com/2015/02/01/what-color-is-your-function/) blog post - although that post is dealing with asynchronous code in general. There is a parallel here though where blue is regular code, and red is reactive streams code.
