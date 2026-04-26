package co.early.fore.core.ui.trigger

import kotlin.test.Test
import kotlin.test.assertEquals

class TriggerWhenTest {

    private val triggeredWhenReturnsTrue = Returns(true)
    private val triggeredWhenReturnsFalse = Returns(false)
    private val triggeredWhenReturnsTrueFalse = Returns(true, false)
    private val triggeredWhenReturnsFalseTrue = Returns(false, true)
    private val triggeredWhenReturnsTrueFalseTrue = Returns(true, false, true)

    @Test
    fun `default Trigger uses resetRule ONLY_AFTER_REVERSION`() {

        // arrange
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) { }

        // act

        // assert
        assertEquals(ResetRule.ONLY_AFTER_REVERSION, trigger.getResetRule())
    }

    @Test
    fun `on first successful triggeredWhen - check DOES fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }

        // act
        trigger.check()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `on first failed triggeredWhen - check DOES NOT fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsFalse()) {
            calledTimes++
        }

        // act
        trigger.check()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `on first successful triggeredWhen - checkLazy DOES NOT fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }

        // act
        trigger.checkLazy()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `on second successful triggeredWhen in a row - checkLazy DOES NOT fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `on TRUE then FALSE triggeredWhen - checkLazy DOES NOT fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalse()) {
            calledTimes++
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `on FALSE then TRUE triggeredWhen - checkLazy DOES fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsFalseTrue()) {
            calledTimes++
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `with ONLY_AFTER_REVERSION resetRule and TRUE TRUE TRUE triggeredWhen - check fires the trigger only once`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `with ONLY_AFTER_REVERSION resetRule and TRUE TRUE TRUE triggeredWhen - checkLazy does NOT fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `with ONLY_AFTER_REVERSION resetRule and TRUE FALSE TRUE triggeredWhen - check fires the trigger twice`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalseTrue()) {
            calledTimes++
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(2, calledTimes)
    }

    @Test
    fun `with ONLY_AFTER_REVERSION resetRule and TRUE FALSE TRUE triggeredWhen - checkLazy fires the trigger only once`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalseTrue()) {
            calledTimes++
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `with IMMEDIATELY resetRule and TRUE TRUE TRUE triggeredWhen - check fires the trigger each time`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(3, calledTimes)
    }

    @Test
    fun `with IMMEDIATELY resetRule and TRUE TRUE TRUE triggeredWhen - check fires the trigger each time except the first`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(2, calledTimes)
    }

    @Test
    fun `with IMMEDIATELY resetRule and TRUE FALSE TRUE triggeredWhen - check fires the trigger twice`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalseTrue()) {
            calledTimes++
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(2, calledTimes)
    }

    @Test
    fun `with IMMEDIATELY resetRule and TRUE FALSE TRUE triggeredWhen - check fires the trigger only once`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalseTrue()) {
            calledTimes++
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `with NEVER resetRule and TRUE TRUE TRUE triggeredWhen - check fires the trigger only once`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `with NEVER resetRule and TRUE TRUE TRUE triggeredWhen - checkLazy does NOT fire the trigger`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrue()) {
            calledTimes++
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, calledTimes)
    }

    @Test
    fun `with NEVER resetRule and TRUE FALSE TRUE triggeredWhen - check fires the trigger only once`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalseTrue()) {
            calledTimes++
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        assertEquals(1, calledTimes)
    }

    @Test
    fun `with NEVER resetRule and TRUE FALSE TRUE triggeredWhen - checkLazy does NOT fire`() {

        // arrange
        var calledTimes = 0
        val trigger = TriggerWhen(triggeredWhenReturnsTrueFalseTrue()) {
            calledTimes++
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        assertEquals(0, calledTimes)
    }
}

class Returns<T>(
    vararg answers: T
) {

    private val answers: List<T> = listOf(*answers)
    private var callCount = 0

    operator fun invoke(): () -> T = {
        val returnValue = when {
            callCount < answers.size -> answers[callCount]
            else -> answers[answers.size - 1]
        }
        callCount++
        returnValue
    }
}
