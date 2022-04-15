package co.early.fore.kt.core.observer

import co.early.fore.core.WorkMode
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.logging.SilentLogger
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ObservableImpTest {

    @MockK
    private lateinit var mockObserver1: Observer

    @MockK
    private lateinit var mockObserver2: Observer

    @MockK
    private lateinit var mockObserver3: Observer

    @MockK
    private lateinit var mockObserver4: Observer

    @MockK
    private lateinit var mockObserver5: Observer

    @MockK
    private lateinit var mockLogger: Logger

    private lateinit var observableImp: ObservableImp


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Fore.setDelegate(TestDelegateDefault())
    }


    @Test
    fun `when created, has no observers`() {

        // arrange, act
        observableImp = ObservableImp()

        // assert
        assertEquals(false, observableImp.hasObservers())
    }

    @Test
    fun `when observers added, has observers`() {

        // arrange
        observableImp = ObservableImp()

        // act
        observableImp.addObserver(mockObserver1)

        // assert
        assertEquals(true, observableImp.hasObservers())
    }

    @Test
    fun `when observers added, then removed, has no observers`() {

        // arrange
        observableImp = ObservableImp()

        // act
        observableImp.addObserver(mockObserver1)
        observableImp.removeObserver(mockObserver1)

        // assert
        assertEquals(false, observableImp.hasObservers())
    }

    @Test
    fun `when removing an observer that wasn't added, warning is logged`() {

        // arrange
        observableImp = ObservableImp(logger = mockLogger)

        // act
        observableImp.addObserver(mockObserver1)
        observableImp.removeObserver(mockObserver2)

        // assert
        verify(exactly = 1) { mockLogger.w(any()) }
    }

    @Test
    fun `when adding more than 4 observers, warning is logged`() {

        // arrange
        observableImp = ObservableImp(logger = mockLogger)

        // act
        observableImp.addObserver(mockObserver1)
        observableImp.addObserver(mockObserver2)
        observableImp.addObserver(mockObserver3)
        observableImp.addObserver(mockObserver4)
        verify(exactly = 0) { mockLogger.w(any()) }
        observableImp.addObserver(mockObserver5)

        // assert
        verify(exactly = 1) { mockLogger.w(any()) }
    }

    @Test
    fun `when notifying, all added observers are called`() {

        // arrange
        observableImp = ObservableImp()
        observableImp.addObserver(mockObserver1)
        observableImp.addObserver(mockObserver2)
        observableImp.addObserver(mockObserver3)
        observableImp.removeObserver(mockObserver2)

        // act
        observableImp.notifyObservers()

        // assert
        verify(exactly = 1) { mockObserver1.somethingChanged() }
        verify(exactly = 0) { mockObserver2.somethingChanged() }
        verify(exactly = 1) { mockObserver3.somethingChanged() }
    }

    @Test
    fun `when adding and removing observers on different threads, observer list is protected from concurrent changes`() {

        // arrange
        observableImp = ObservableImp(
            notificationMode = WorkMode.ASYNCHRONOUS,
            logger = SilentLogger(),
            dispatcher = Dispatchers.Unconfined
        )
        val loop = 10000
        val latch = CountDownLatch(loop + 1)
        var counter = 0

        // act
        CoroutineScope(
            Executors.newFixedThreadPool(10).asCoroutineDispatcher()
        ).launch {
            repeat(loop) {
                launch {
                    val observer = Observer { counter++ }
                    observableImp.addObserver(observer)
                    if (it % 2 == 0) {
                        observableImp.removeObserver(observer)
                    }
                    latch.countDown()
                }
            }
            launch {
                observableImp.notifyObservers()
                latch.countDown()
            }
        }
        try {
            latch.await(100, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // assert
        assertEquals(loop / 2, counter)
    }
}
