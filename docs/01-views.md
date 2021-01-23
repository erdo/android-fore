
# Views
Views are not just XML layouts, in Android the classes that form the view layer of an app are not just the classes extending View either, they include the **Activity**, **Fragment** *and* **View** classes.

These classes:

- are ephemeral
- are tightly coupled to the context (including the physical characteristics of the display)
- are slow to test

> "View layers are: ephemeral; tightly coupled to the context; slow to test"


In short they are no place to put business logic or networking code, any code placed in those classes will present the developer with a range of challenges related to managing a complicated lifecycle when screens are rotated or phone calls accepted, such as:

- loosing data stored in memory (causing null pointers or requiring unecessary network calls)
- maintaining UI consistency
- guarding against memory leaks

It might seem obvious, but still: handling those issues accounts for a fairly large chunk of the boiler plate present in a typical android app.


## Examples

All the view classes (Activity/Fragment/View) for the sample apps are found in the **ui** package and do as little as possible apart from:

- manage their lifecycle
- route button clicks and other widget listeners to the right place (usually directly to a model class)
- correctly display the state of whatever models they are interested in.


Here are few examples:

- [Wallets View](https://github.com/erdo/android-fore/blob/master/example-kt-01reactiveui/src/main/java/foo/bar/example/forereactiveuikt/ui/wallet/WalletsActivity.kt)

- [Counter View](https://github.com/erdo/android-fore/blob/master/example-jv-02threading/src/main/java/foo/bar/example/forethreading/ui/CounterActivity.java)


## Code that belongs in the view layer

Pretty much all views in **fore** do the same few things when they are created:

- get a reference to all the view components like Buttons, TextViews etc.
- get a reference to all models that the view needs to observe (using some form of DI)
- set up all the click listeners, text changed listeners etc
- *(optionally) set up any adapters*
- *(optionally) set up any SyncTriggers for things like animations*

In addition to that there will be:

- the [syncView()](https://erdo.github.io/android-fore/03-reactive-uis.html#syncview) function which sets an affirmative state on each of the view components, in line with what the models indicate (or proxys this to an [adapter](https://erdo.github.io/android-fore/04-more-fore.html#adapter-animations) by calling adapter.notifyDataSetChangedAuto method).

Often there will also be the add / remove observers methods where the view registers with the models it is interested in - this is handled automatically in the Sync... classes, see below.


## Removing even more boiler plate

If you don't want to have to bother with implementing the adding and removing of observers in your view code, you can let the Sync... classes do it for you.

### SyncActivityX and SyncActivity
By extending one of these classes (the first one is for androidx), you will just be left with having to state which observable models you want to observe, and then implementing the syncView() method:

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>
<pre class="tabcontent tabbed java"><code>
public class MySpeedoActivity extends SyncActivityX {

  private SpeedoModel speedoModel; //inject these
  private RoadInfoModel roadInfoModel; //inject these

  @Override
  public LifecycleSyncer.Observables getThingsToObserve() {
    return new LifecycleSyncer.Observables(
        speedoModel,
        roadInfoModel
    );
  }

  @Override
  public void syncView() {
    speedo_roadname_textview.text = roadInfoModel.getRoadName();
    speedo_speed_textview.text = speedoModel.getSpeed();
  }
}
 </code></pre>
<pre class="tabcontent tabbed kotlin"><code>
class MySpeedoActivity : SyncActivityX() {

  private val speedoModel: SpeedoModel by inject()
  private val roadInfoModel: RoadInfoModel by inject()

  override fun getThingsToObserve(): LifecycleSyncer.Observables {
    return LifecycleSyncer.Observables(
        speedoModel,
        roadInfoModel
    )
  }

  override fun syncView() {
    speedo_roadname_textview.text = roadInfoModel.roadName
    speedo_speed_textview.text = speedoModel.speed
  }
}
 </code></pre>


### SyncFragmentX and SyncFragment
If you prefer to use Fragments for your view layer you can extend one of these (and again, the first one is for androidx).

<!-- Tabbed code sample -->
 <div class="tab">
   <button class="tablinks java" onclick="openLanguage('java')">Java</button>
   <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
 </div>
<pre class="tabcontent tabbed java"><code>
public class MySpeedoFragment extends SyncFragmentX {

  private SpeedoModel speedoModel; //inject these
  private RoadInfoModel roadInfoModel; //inject these

  @Override
  public LifecycleSyncer.Observables getThingsToObserve() {
    return new LifecycleSyncer.Observables(
            speedoModel,
            roadInfoModel
    );
  }

  @Override
  public void syncView() {
    speedo_roadname_textview.text = roadInfoModel.getRoadName();
    speedo_speed_textview.text = speedoModel.getSpeed();
  }
}
 </code></pre>
<pre class="tabcontent tabbed kotlin"><code>
class MySpeedoFragment : SyncFragmentX() {

    private val speedoModel: SpeedoModel by inject()
    private val roadInfoModel: RoadInfoModel by inject()

    override fun getThingsToObserve(): LifecycleSyncer.Observables {
        return LifecycleSyncer.Observables(
            speedoModel,
            roadInfoModel
        )
    }

    override fun syncView() {
        speedo_roadname_textview.text = roadInfoModel.roadName
        speedo_speed_textview.text = speedoModel.speed
    }
}
 </code></pre>

### Sync[ViewGroup]
 There are a few Sync ViewGroup classes that you can use if you prefer working with custom views but also want the observer boiler plate taken care of. SyncConstraintLayout, SyncScrollView etc. If there is a ViewGroup you want, that isn't included - it's not too hard to do it yourself (just look at the source of the ones we have, they're very small classes. For example here's the one for [ScrollView](https://github.com/erdo/android-fore/blob/master/fore-lifecycle/src/main/java/co/early/fore/lifecycle/view/SyncScrollView.java)).

 <!-- Tabbed code sample -->
  <div class="tab">
    <button class="tablinks java" onclick="openLanguage('java')">Java</button>
    <button class="tablinks kotlin" onclick="openLanguage('kotlin')">Kotlin</button>
  </div>
 <pre class="tabcontent tabbed java"><code>
 public class MySpeedoView extends SyncConstraintLayout {

   private SpeedoModel speedoModel; //inject these
   private RoadInfoModel roadInfoModel; //inject these

   //constructors

   @Override
   public LifecycleSyncer.Observables getThingsToObserve() {
     return new LifecycleSyncer.Observables(
             speedoModel,
             roadInfoModel
     );
   }

   @Override
   public void syncView() {
     speedo_roadname_textview.text = roadInfoModel.getRoadName();
     speedo_speed_textview.text = speedoModel.getSpeed();
   }
 }
  </code></pre>
 <pre class="tabcontent tabbed kotlin"><code>
 class MySpeedoView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    SyncConstraintLayout(context, attrs, defStyleAttr) {

     private val speedoModel: SpeedoModel by inject()
     private val roadInfoModel: RoadInfoModel by inject()

     override fun getThingsToObserve(): LifecycleSyncer.Observables {
         return LifecycleSyncer.Observables(
             speedoModel,
             roadInfoModel
         )
     }

     override fun syncView() {
         speedo_roadname_textview.text = roadInfoModel.roadName
         speedo_speed_textview.text = speedoModel.speed
     }
 }
  </code></pre>

### ViewModels observing many observables
Similarly you can observe multiple observables from a ViewModel like this:


``` kotlin

class MyViewModel(
    private val accountModel: AccountModel,
    private val networkInfo: NetworkInfo,
    private val emailInBox: EmailInBox,
    private val weatherRepository: WeatherRepository
) : ViewModel(), SyncableView, AutoSyncable by ObservableGroup(
    accountModel,
    networkInfo,
    emailInBox,
    weatherRepository) {

    init {
        addObserversAndSync(this)
    }

    override fun syncView() {
       // Here you might create an immutable view state
       // to pass to your fragment (based on the state of
       // the models that you're observing).
       // You can use LiveData to make the final hop
       // to the fragment from here, or again use a fore
       // observable to make the ViewModel itself observable
        ...
    }

    override fun onCleared() {
        super.onCleared()
        removeObservers()
    }
}

```
