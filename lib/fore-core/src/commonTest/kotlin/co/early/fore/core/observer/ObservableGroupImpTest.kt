package co.early.fore.core.observer

import co.early.fore.core.DelegateTestSynchronousCopy
import co.early.fore.core.delegate.Fore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ObservableGroupImpTest {

    private val observer1 = ObserverNotificationCollector()
    private val observer2 = ObserverNotificationCollector()

    private val observable1 = ObservableImp()
    private val observable2 = ObservableImp()
    private val observable3 = ObservableImp()

    private lateinit var delegate: DelegateTestSynchronousCopy

    @BeforeTest
    fun setup() {

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        delegate = DelegateTestSynchronousCopy()
        Fore.setDelegate(delegate)
    }

    @AfterTest
    fun cleanup() {
        delegate.cleanup()
    }

    @Test
    fun `when created with no observables - throws error`() {

        // arrange, act, assert
        var thrown = false
        try {
            ObservableGroupImp()
        } catch (iae: IllegalArgumentException) {
            thrown = true
        }

        assertTrue(
            thrown, "trying to create an observable" +
                    "group with no observables, should have thrown an exception"
        )
    }

    @Test
    fun `when created with 1 observable - adding an observer effects that observable`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1
        )

        // act
        observableGroupImp.addObserver(observer1)

        //assert
        assertTrue(observable1.hasObservers())
    }

    @Test
    fun `when created with 1 observable - adding and removing an observer effects that observable`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1
        )

        // act
        observableGroupImp.addObserver(observer1)
        observableGroupImp.removeObserver(observer1)

        //assert
        assertFalse(observable1.hasObservers())
    }

    @Test
    fun `when created with multiple observables - adding an observer effects those observables`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1,
            observable2,
            observable3,
        )

        // act
        observableGroupImp.addObserver(observer1)

        //assert
        assertTrue(observable1.hasObservers())
        assertTrue(observable2.hasObservers())
        assertTrue(observable3.hasObservers())
    }

    @Test
    fun `when created with multiple observables - adding and removing an observer effects those observables`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1,
            observable2,
            observable3,
        )

        // act
        observableGroupImp.addObserver(observer1)
        observableGroupImp.removeObserver(observer1)

        //assert
        assertFalse(observable1.hasObservers())
        assertFalse(observable2.hasObservers())
        assertFalse(observable3.hasObservers())
    }

    @Test
    fun `when created with multiple observables - adding two observers then notifying - calls both observers`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1,
            observable2,
            observable3,
        )

        // act
        observableGroupImp.addObserver(observer1)
        observableGroupImp.addObserver(observer2)
        observable1.notifyObservers()
        observable2.notifyObservers()
        observable3.notifyObservers()

        //assert
        assertEquals(3, observer1.notifications())
        assertEquals(3, observer2.notifications())
    }

    @Test
    fun `when created with multiple observables - adding two observers then removing one - only first is notified`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1,
            observable2,
            observable3,
        )

        // act
        observableGroupImp.addObserver(observer1)
        observableGroupImp.addObserver(observer2)
        observableGroupImp.removeObserver(observer2)
        observable1.notifyObservers()
        observable2.notifyObservers()
        observable3.notifyObservers()

        //assert
        assertEquals(3, observer1.notifications())
        assertEquals(0, observer2.notifications())
    }

    @Test
    fun `when created with multiple observables - adding two observers then notifying on second observable - calls both observers`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            observable1,
            observable2,
            observable3,
        )

        // act
        observableGroupImp.addObserver(observer1)
        observableGroupImp.addObserver(observer2)
        observable2.notifyObservers()

        //assert
        assertEquals(1, observer1.notifications())
        assertEquals(1, observer2.notifications())
    }
}
