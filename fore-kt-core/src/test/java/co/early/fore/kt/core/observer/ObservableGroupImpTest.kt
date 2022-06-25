package co.early.fore.kt.core.observer

import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.delegate.TestDelegateDefault
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test


class ObservableGroupImpTest {

    @MockK
    private lateinit var mockObservable1: Observable

    @MockK
    private lateinit var mockObservable2: Observable

    @MockK
    private lateinit var mockObservable3: Observable

    @MockK
    private lateinit var mockObserver1: Observer

    @MockK
    private lateinit var mockObserver2: Observer


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Fore.setDelegate(TestDelegateDefault())
    }


    @Test
    fun `when created with no observables, throws error`() {

        // arrange, act, assert
        try {
            ObservableGroupImp()
        } catch (iae: IllegalArgumentException){
            assert(true)
            return
        }

        assert(false) {"trying to create an observable" +
                "group with no observables, should have thrown an exception"}
    }

    @Test
    fun `when created with 1 observable, add calls that observable`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            mockObservable1
        )

        // act
        observableGroupImp.addObserver(mockObserver1)

        //assert
        verify(exactly = 1){
            mockObservable1.addObserver(eq(mockObserver1))
        }
        verify(exactly = 0){
            mockObservable1.removeObserver(eq(mockObserver1))
        }
    }

    @Test
    fun `when created with 1 observable, remove calls that observable`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            mockObservable1
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        observableGroupImp.removeObserver(mockObserver1)

        //assert
        verify(exactly = 1){
            mockObservable1.removeObserver(eq(mockObserver1))
        }
    }

    @Test
    fun `when created with multiple observables, add calls those observables`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            mockObservable1,
            mockObservable2,
            mockObservable3
        )

        // act
        observableGroupImp.addObserver(mockObserver1)

        //assert
        verify(exactly = 1){
            mockObservable1.addObserver(eq(mockObserver1))
            mockObservable2.addObserver(eq(mockObserver1))
            mockObservable3.addObserver(eq(mockObserver1))
        }
        verify(exactly = 0){
            mockObservable1.removeObserver(any())
            mockObservable2.removeObserver(any())
            mockObservable3.removeObserver(any())
        }
    }

    @Test
    fun `when created with multiple observables, remove calls those observables`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            mockObservable1,
            mockObservable2,
            mockObservable3
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        observableGroupImp.removeObserver(mockObserver1)

        //assert
        verify(exactly = 1){
            mockObservable1.removeObserver(eq(mockObserver1))
            mockObservable2.removeObserver(eq(mockObserver1))
            mockObservable3.removeObserver(eq(mockObserver1))
        }
    }

    @Test
    fun `when created with multiple observables, adding two observers calls those observables twice`() {

        // arrange
        val observableGroupImp = ObservableGroupImp(
            mockObservable1,
            mockObservable2,
            mockObservable3
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        observableGroupImp.addObserver(mockObserver2)

        //assert
        verify(exactly = 1){
            mockObservable1.addObserver(eq(mockObserver1))
            mockObservable2.addObserver(eq(mockObserver1))
            mockObservable3.addObserver(eq(mockObserver1))
        }
        verify(exactly = 1){
            mockObservable1.addObserver(eq(mockObserver2))
            mockObservable2.addObserver(eq(mockObserver2))
            mockObservable3.addObserver(eq(mockObserver2))
        }
        verify(exactly = 0){
            mockObservable1.removeObserver(any())
            mockObservable2.removeObserver(any())
            mockObservable3.removeObserver(any())
        }
    }

    @Test
    fun `when created with multiple observables, notifying first observable, calls groups somethingchanged`() {

        val realObservable = ObservableImp()

        // arrange
        val observableGroupImp = ObservableGroupImp(
            realObservable,
            ObservableImp(),
            ObservableImp()
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        realObservable.notifyObservers()

        //assert
        verify(exactly = 1){
            mockObserver1.somethingChanged()
        }
    }

    @Test
    fun `when created with multiple observables, notifying second observable, calls groups somethingchanged`() {

        val realObservable = ObservableImp()

        // arrange
        val observableGroupImp = ObservableGroupImp(
            ObservableImp(),
            realObservable,
            ObservableImp()
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        realObservable.notifyObservers()

        //assert
        verify(exactly = 1){
            mockObserver1.somethingChanged()
        }
    }

    @Test
    fun `when created with multiple observables, notifying third observable, calls groups somethingchanged`() {

        val realObservable = ObservableImp()

        // arrange
        val observableGroupImp = ObservableGroupImp(
            ObservableImp(),
            ObservableImp(),
            realObservable
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        realObservable.notifyObservers()

        //assert
        verify(exactly = 1){
            mockObserver1.somethingChanged()
        }
    }

    @Test
    fun `when adding two observers, both somethingchanged methods are called`() {

        val realObservable = ObservableImp()

        // arrange
        val observableGroupImp = ObservableGroupImp(
            realObservable,
            ObservableImp(),
            ObservableImp()
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        observableGroupImp.addObserver(mockObserver2)
        realObservable.notifyObservers()

        //assert
        verify(exactly = 1){
            mockObserver1.somethingChanged()
        }
        verify(exactly = 1){
            mockObserver2.somethingChanged()
        }
    }

    @Test
    fun `when observers are removed, somethingchanged is no longer called`() {

        val realObservable = ObservableImp()

        // arrange
        val observableGroupImp = ObservableGroupImp(
            realObservable,
            ObservableImp(),
            ObservableImp()
        )

        // act
        observableGroupImp.addObserver(mockObserver1)
        observableGroupImp.removeObserver(mockObserver1)
        realObservable.notifyObservers()

        //assert
        verify(exactly = 0){
            mockObserver1.somethingChanged()
        }
    }
}
