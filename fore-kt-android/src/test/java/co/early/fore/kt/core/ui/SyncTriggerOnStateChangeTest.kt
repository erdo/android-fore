package co.early.fore.kt.core.ui

import co.early.fore.kt.core.ui.synctrigger.SyncTriggerOnStateChange
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SyncTriggerOnStateChangeTest {

    private data class TestData(
        val message: String,
        val count: Int,
    )

    @MockK
    private lateinit var mockCurrentState: () -> Int

    @MockK
    private lateinit var mockDoThisWhenTriggered: (Int) -> Unit

    @MockK
    private lateinit var mockNullDoThisWhenTriggered: (String?) -> Unit

    @MockK
    private lateinit var mockDataClassCurrentState: () -> TestData

    @MockK
    private lateinit var mockDataClassDoThisWhenTriggered: (TestData) -> Unit

    private val TEST_1 = TestData("yes", 1)
    private val TEST_2 = TestData("no", 0)

    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)

    @Test
    fun `when initial state changes, check() triggers`() {

        // arrange
        every { mockCurrentState.invoke() } returns 1
        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state stays null, check() does not trigger`() {

        // arrange
        val syncTrigger = SyncTriggerOnStateChange<String?>({ null }, mockNullDoThisWhenTriggered)

        // act
        syncTrigger.check()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state changes, check() triggers with new state value`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3
        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(3)
        }
    }

    @Test
    fun `when state changes multiple times, check() triggers only on change`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3 andThen 5 andThen 6 andThen 6 andThen 3

        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 2) {
            mockDoThisWhenTriggered(3)
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(5)
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(6)
        }
    }

    @Test
    fun `when initial state changes, first checkLazy() does not trigger`() {

        // arrange
        every { mockCurrentState.invoke() } returns 1
        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state stays null, checkLazy() does not trigger`() {

        // arrange
        val syncTrigger = SyncTriggerOnStateChange<String?>({ null }, mockNullDoThisWhenTriggered)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state changes, second checkLazy() does not trigger`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3
        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when state changes multiple times, checkLazy() triggers only on change`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3 andThen 5 andThen 6 andThen 6 andThen 3

        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(3)
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(5)
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(6)
        }
    }

    @Test
    fun `when first and second state changes are the same, second checkLazy() does not trigger`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3 andThen 3

        val syncTrigger = SyncTriggerOnStateChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }


    @Test
    fun `when data class state changes, second check() does trigger`() {

        // arrange
        every { mockDataClassCurrentState.invoke() } returns TEST_1 andThen TEST_2

        val syncTrigger =
            SyncTriggerOnStateChange(mockDataClassCurrentState, mockDataClassDoThisWhenTriggered)

        // act
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDataClassDoThisWhenTriggered(TEST_1)
        }
        verify(exactly = 1) {
            mockDataClassDoThisWhenTriggered(TEST_2)
        }
    }

    @Test
    fun `when data class state does not change, second check() does not trigger`() {

        // arrange
        every { mockDataClassCurrentState.invoke() } returns TEST_1 andThen TEST_1

        val syncTrigger =
            SyncTriggerOnStateChange(mockDataClassCurrentState, mockDataClassDoThisWhenTriggered)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDataClassDoThisWhenTriggered(any())
        }
    }
}
