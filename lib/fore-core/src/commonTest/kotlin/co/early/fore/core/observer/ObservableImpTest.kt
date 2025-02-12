package co.early.fore.core.observer

import co.early.fore.core.delegate.Fore
import co.early.fore.core.delegate.TestDelegateDefault
import co.early.fore.core.logging.Logger
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObservableImpTest {

    private val observer1 = ObserverNotificationCollector()
    private val observer2 = ObserverNotificationCollector()
    private val observer3 = ObserverNotificationCollector()
    private val observer4 = ObserverNotificationCollector()
    private val observer5 = ObserverNotificationCollector()

    private val warningCollector = WarningCollector()

    @BeforeTest
    fun setup() {
        Fore.setDelegate(TestDelegateDefault())
    }

    @Test
    fun `when created - has no observers`() {

        // arrange, act
        val observableImp = ObservableImp()

        // assert
        assertEquals(false, observableImp.hasObservers())
    }

    @Test
    fun `when observers added - has observers`() {

        // arrange
        val observableImp = ObservableImp()

        // act
        observableImp.addObserver(observer1)

        // assert
        assertTrue(observableImp.hasObservers())
    }

    @Test
    fun `when observers added then removed - has no observers`() {

        // arrange
        val observableImp = ObservableImp()

        // act
        observableImp.addObserver(observer1)
        observableImp.removeObserver(observer1)

        // assert
        assertFalse(observableImp.hasObservers())
    }

    @Test
    fun `when adding the same observer twice - warning is logged`() {

        // arrange
        val observableImp = ObservableImp(logger = warningCollector)

        // act
        observableImp.addObserver(observer1)
        observableImp.addObserver(observer1)

        // assert
        assertEquals(1, warningCollector.getLogs().size)
    }

    @Test
    fun `when removing an observer that wasn't added - warning is logged`() {

        // arrange
        val observableImp = ObservableImp(logger = warningCollector)

        // act
        observableImp.addObserver(observer1)
        observableImp.removeObserver(observer2)

        // assert
        assertEquals(1, warningCollector.getLogs().size)
    }


    @Test
    fun `when adding more than 4 observers - warning is logged`() {

        // arrange
        val observableImp = ObservableImp(logger = warningCollector)

        // act
        observableImp.addObserver(observer1)
        observableImp.addObserver(observer2)
        observableImp.addObserver(observer3)
        observableImp.addObserver(observer4)
        assertTrue(warningCollector.getLogs().isEmpty())
        observableImp.addObserver(observer5)

        // assert
        assertEquals(1, warningCollector.getLogs().size)
    }

    @Test
    fun `when notifying - all added observers are called`() {

        // arrange
        val observableImp = ObservableImp()
        observableImp.addObserver(observer1)
        observableImp.addObserver(observer2)
        observableImp.addObserver(observer3)
        observableImp.removeObserver(observer2)

        // act
        observableImp.notifyObservers()

        // assert
        assertEquals(1, observer1.notifications())
        assertEquals(0, observer2.notifications())
        assertEquals(1, observer3.notifications())
    }
}

class WarningCollector : Logger {

    private val warnings: MutableList<String> = mutableListOf()

    fun getLogs(): List<String> {
        return warnings.toList()
    }

    override fun w(message: String) {
        warnings.add(message)
    }

    override fun w(tag: String, message: String) {
        warnings.add(message)
    }

    override fun w(message: String, throwable: Throwable) {
        warnings.add(message)
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        warnings.add(message)
    }

    override fun e(message: String) {}

    override fun e(tag: String, message: String) {}

    override fun e(message: String, throwable: Throwable) {}

    override fun e(tag: String, message: String, throwable: Throwable) {}

    override fun i(message: String) {}

    override fun i(tag: String, message: String) {}

    override fun i(message: String, throwable: Throwable) {}

    override fun i(tag: String, message: String, throwable: Throwable) {}

    override fun d(message: String) {}

    override fun d(tag: String, message: String) {}

    override fun d(message: String, throwable: Throwable) {}

    override fun d(tag: String, message: String, throwable: Throwable) {}

    override fun v(message: String) {}

    override fun v(tag: String, message: String) {}

    override fun v(message: String, throwable: Throwable) {}

    override fun v(tag: String, message: String, throwable: Throwable) {}
}

class ObserverNotificationCollector : Observer {

    private var notifications = 0

    fun notifications(): Int {
        return notifications
    }

    override fun somethingChanged() {
        notifications++
    }
}
