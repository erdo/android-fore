package co.early.fore.kt.core.ui

import co.early.fore.kt.core.ui.synctrigger.ResetRule
import co.early.fore.kt.core.ui.synctrigger.SyncTrigger
import co.early.fore.kt.core.ui.synctrigger.SyncTriggerKeeper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SyncTriggerTest {

    @MockK
    private lateinit var mockTriggeredWhen: () -> Boolean
    @MockK
    private lateinit var mockDoThis: () -> Unit

    @Before
    fun setup() = MockKAnnotations.init(this, relaxed = true)

    @Test
    fun `default SyncTrigger uses resetRule ONLY_AFTER_REVERSION`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act

        // assert
        assertEquals(ResetRule.ONLY_AFTER_REVERSION, syncTrigger.getResetRule())
    }

    @Test
    fun `on first successful trigger, check DOES fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on first failed trigger, check DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns false
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        syncTrigger.check()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on first successful trigger, check lazy DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on second successful trigger, check lazy DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE then FALSE trigger, check lazy DOES NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on FALSE then TRUE trigger, check lazy DOES fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check and reset ONLY_AFTER_REVERSION fires only once`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check lazy and reset ONLY_AFTER_REVERSION does NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check and reset ONLY_AFTER_REVERSION fires twice`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 2) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check lazy and reset ONLY_AFTER_REVERSION fires once`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.ONLY_AFTER_REVERSION)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check and reset IMMEDIATELY fires each time`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 3) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check lazy and reset IMMEDIATELY fires each time except first`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 2) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check and reset IMMEDIATELY fires twice`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 2) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check lazy and reset IMMEDIATELY fires once`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.IMMEDIATELY)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check and reset NEVER, fires one time only`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, TRUE, TRUE triggers, check lazy and reset NEVER, does NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check and reset NEVER, fires one time only`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        verify(exactly = 1) {
            mockDoThis()
        }
    }

    @Test
    fun `on TRUE, FALSE, TRUE triggers, check lazy and reset NEVER, does NOT fire`() {

        // arrange
        every { mockTriggeredWhen.invoke() } returns true andThen false andThen true
        val syncTrigger = SyncTrigger({ mockTriggeredWhen() }) {
            mockDoThis()
        }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }

    @Test
    fun `on trigger with keep, first kept value is null`() {

        // arrange
        var keptValue: Int? = null
        val syncTrigger = SyncTriggerKeeper<Int>(
            { keeper ->
                keeper.swap { previous ->
                    keptValue = previous
                    99
                }
                true
            })
        { mockDoThis() }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()

        // assert
        assertNull(keptValue)
    }

    @Test
    fun `on trigger with keep, second kept value is correct`() {

        // arrange
        var keptValue: Int? = null
        val syncTrigger = SyncTriggerKeeper<Int>(
            { keeper ->
                keeper.swap { previous ->
                    keptValue = previous
                    73
                }
                true
            })
        { mockDoThis() }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()
        syncTrigger.check()

        // assert
        assertEquals(73, keptValue)
    }

    @Test
    fun `on 3 triggers with keeps, triggeredWhen receives correct value`() {

        // arrange
        var keptValue = -1
        val syncTrigger = SyncTriggerKeeper<Int>(
            { keeper ->
                keeper.swap { previous ->
                    keptValue = previous ?: 0
                    val newValueToKeep = keptValue + 10
                    newValueToKeep
                }
                true
            })
        { mockDoThis() }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()
        syncTrigger.check()

        // assert
        assertEquals(30, keptValue)
    }

    @Test
    fun `on trigger with keep, first swap indicates change`() {

        // arrange
        var change = false
        val syncTrigger = SyncTriggerKeeper<Int>(
            { keeper ->
                change = keeper.swap {
                    val newValue = 73
                    newValue
                }
                true
            })
        { mockDoThis() }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()

        // assert
        assertEquals(true, change)
    }

    @Test
    fun `on trigger with changing kept value, swap indicates change`() {

        // arrange
        var change = false
        var keptValue: Int
        val syncTrigger = SyncTriggerKeeper<Int>(
            { keeper ->
                change = keeper.swap { previous ->
                    keptValue = previous ?: 0
                    val newValueToKeep = keptValue + 10
                    newValueToKeep
                }
                true
            })
        { mockDoThis() }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()
        syncTrigger.check()

        // assert
        assertEquals(true, change)
    }

    @Test
    fun `on trigger with same kept value, swap indicates no change`() {

        // arrange
        var change = false
        val syncTrigger = SyncTriggerKeeper<Int>(
            { keeper ->
                change = keeper.swap {
                    7
                }
                true
            })
        { mockDoThis() }.resetRule(ResetRule.NEVER)

        // act
        syncTrigger.check()
        syncTrigger.check()

        // assert
        assertEquals(false, change)
    }
}
