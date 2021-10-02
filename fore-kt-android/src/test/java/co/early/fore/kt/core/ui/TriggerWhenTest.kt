package co.early.fore.kt.core.ui

import co.early.fore.kt.core.ui.trigger.ResetRule
import co.early.fore.kt.core.ui.trigger.TriggerWhen
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TriggerWhenTest {

    @MockK
    private lateinit var mockTriggeredWhen: () -> Boolean

    @MockK
    private lateinit var mockDoThis: () -> Unit

    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)

    @Test
    fun `default Trigger uses resetRule ONLY_AFTER_REVERSION`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act

        // assert
        assertEquals(ResetRule.ONLY_AFTER_REVERSION, trigger.getResetRule())
    }

    @Test
    fun `on first successful trigger, check DOES fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on first failed trigger, check DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns false
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.check()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on first successful trigger, check lazy DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on second successful trigger, check lazy DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE then FALSE trigger, check lazy DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on FALSE then TRUE trigger, check lazy DOES fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check and reset ONLY_AFTER_REVERSION fires only once`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check lazy and reset ONLY_AFTER_REVERSION does NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check and reset ONLY_AFTER_REVERSION fires twice`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 2) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check lazy and reset ONLY_AFTER_REVERSION fires once`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check and reset IMMEDIATELY fires each time`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 3) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check lazy and reset IMMEDIATELY fires each time except first`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 2) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check and reset IMMEDIATELY fires twice`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 2) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check lazy and reset IMMEDIATELY fires once`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check and reset NEVER, fires one time only`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check lazy and reset NEVER, does NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check and reset NEVER, fires one time only`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.check()
        trigger.check()
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check lazy and reset NEVER, does NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        trigger.checkLazy()
        trigger.checkLazy()
        trigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `SyncTrigger typealias works as the same as TriggerWhen`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val trigger = TriggerWhen({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        trigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }
}
