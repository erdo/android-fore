package co.early.fore.kt.core.ui.trigger

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test


class TriggerOnChangeTest {

    private data class TestData(
        val message: String,
        val count: Int,
    )

    @MockK
    private lateinit var mockCurrentState: () -> Int

    @MockK
    private lateinit var mockDoThisWhenTriggered: (StateChange<Int>) -> Unit

    @MockK
    private lateinit var mockNullDoThisWhenTriggered: (StateChange<String?>) -> Unit

    @MockK
    private lateinit var mockDataClassCurrentState: () -> TestData

    @MockK
    private lateinit var mockDataClassDoThisWhenTriggered: (StateChange<TestData>) -> Unit

    private val TEST_1 = TestData("yes", 1)
    private val TEST_2 = TestData("no", 0)

    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)

    @Test
    fun `when initial state changes, check() triggers`() {

        // arrange
        every { mockCurrentState.invoke() } returns 1
        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state stays null, check() does not trigger`() {

        // arrange
        val trigger = TriggerOnChange({ null }, mockNullDoThisWhenTriggered)

        // act
        trigger.check()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state changes, check() triggers with new state value`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3
        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(null, 3)))
        }
    }

    @Test
    fun `when state changes multiple times, check() triggers only on change`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3 andThen 5 andThen 6 andThen 6 andThen 3

        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.check()
        trigger.check()
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(null,3)))
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(3, 5)))
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(5, 6)))
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(6,3)))
        }
    }

    @Test
    fun `when initial state changes, first checkLazy() does not trigger`() {

        // arrange
        every { mockCurrentState.invoke() } returns 1
        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state stays null, checkLazy() does not trigger`() {

        // arrange
        val trigger = TriggerOnChange<String?>({ null }, mockNullDoThisWhenTriggered)

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when initial state changes, second checkLazy() does not trigger`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3
        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }

    @Test
    fun `when state changes multiple times, checkLazy() triggers only on change`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3 andThen 5 andThen 6 andThen 6 andThen 3

        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(3, 5)))
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(5, 6)))
        }
        verify(exactly = 1) {
            mockDoThisWhenTriggered(eq(StateChange(6, 3)))
        }
    }

    @Test
    fun `when first and second state changes are the same, second checkLazy() does not trigger`() {

        // arrange
        every { mockCurrentState.invoke() } returns 3 andThen 3

        val trigger = TriggerOnChange(mockCurrentState, mockDoThisWhenTriggered)

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThisWhenTriggered(any())
        }
    }


    @Test
    fun `when data class state changes, second check() does trigger`() {

        // arrange
        every { mockDataClassCurrentState.invoke() } returns TEST_1 andThen TEST_2

        val trigger =
            TriggerOnChange(mockDataClassCurrentState, mockDataClassDoThisWhenTriggered)

        // act
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDataClassDoThisWhenTriggered(eq(StateChange(null, TEST_1)))
        }
        verify(exactly = 1) {
            mockDataClassDoThisWhenTriggered(eq(StateChange(TEST_1, TEST_2)))
        }
    }

    @Test
    fun `when data class state does not change, second check() does not trigger`() {

        // arrange
        every { mockDataClassCurrentState.invoke() } returns TEST_1 andThen TEST_1

        val trigger =
            TriggerOnChange(mockDataClassCurrentState, mockDataClassDoThisWhenTriggered)

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDataClassDoThisWhenTriggered(any())
        }
    }
}

//fun MockKMatcherScope.eq(other: StateChange<Int>) = match<StateChange<Int>> {
//    it.pre == other.pre && it.now == other.now
//}