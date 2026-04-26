package co.early.fore.core.ui.trigger

import kotlin.test.Test
import kotlin.test.assertEquals

class TriggerOnChangeTest {

    private val stateReturns1 = Returns(1)
    private val stateReturns3 = Returns(3)
    private val stateReturns3_6 = Returns(3, 6)
    private val stateReturns3_5_6_6_3 = Returns(3, 5, 6, 6, 3)
    private val stateReturnsNull = Returns<Int?>(null)

    @Test
    fun `when initial state is non-null - triggers on first check`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerOnChange(stateReturns1()) {
            calledTimes++
        }

        // act
        trigger.check()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `when initial state is null - does not trigger on first check`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerOnChange(stateReturnsNull()) {
            calledTimes++
        }

        // act
        trigger.check()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `when initial state changes - triggers with new state value on check`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns3()) {
            answers.add(it)
        }

        // act
        trigger.check()

        // assert
        assertEquals(StateChange(null, 3), answers[0])
    }

    @Test
    fun `when state changes multiple times - check triggers only on change`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns3_5_6_6_3()) {
            answers.add(it)
        }

        // act
        trigger.check()
        trigger.check()
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(StateChange(null, 3), answers[0])
        assertEquals(StateChange(3, 5), answers[1])
        assertEquals(StateChange(5, 6), answers[2])
        assertEquals(StateChange(6, 3), answers[3])
    }

    @Test
    fun `when initial state is non-null - does not trigger on first checkLazy`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns1()) {
            answers.add(it)
        }

        // act
        trigger.checkLazy()

        // assert
        assertEquals(0, answers.size)
    }

    @Test
    fun `when initial state stays null - does not trigger on second checkLazy`() {

        // arrange
        val answers: MutableList<StateChange<Int?>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturnsNull()) {
            answers.add(it)
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, answers.size)
    }

    @Test
    fun `when initial state is non-null - does not trigger on second checkLazy`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns3()) {
            answers.add(it)
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, answers.size)
    }

    @Test
    fun `when state changes multiple times - checkLazy triggers only on change`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns3_5_6_6_3()) {
            answers.add(it)
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(3, answers.size)
        assertEquals(StateChange(3, 5), answers[0])
        assertEquals(StateChange(5, 6), answers[1])
        assertEquals(StateChange(6, 3), answers[2])
    }

    @Test
    fun `when state changes - second check does trigger`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns3_6()) {
            answers.add(it)
        }

        // act
        trigger.check()
        trigger.check()

        // assert
        assertEquals(2, answers.size)
        assertEquals(StateChange(null, 3), answers[0])
        assertEquals(StateChange(3, 6), answers[1])
    }

    @Test
    fun `when state does not change - second checkLazy does not trigger`() {

        // arrange
        val answers: MutableList<StateChange<Int>> = mutableListOf()
        val trigger = TriggerOnChange(stateReturns3()) {
            answers.add(it)
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, answers.size)
    }
}
