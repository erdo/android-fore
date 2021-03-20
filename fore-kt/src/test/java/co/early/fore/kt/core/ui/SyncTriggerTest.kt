package co.early.fore.kt.core.ui

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.Assert.assertEquals
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
        assertEquals(SyncTrigger.ResetRule.ONLY_AFTER_REVERSION, syncTrigger.resetRule)
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
        }.resetRule(SyncTrigger.ResetRule.ONLY_AFTER_REVERSION)

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
        }.resetRule(SyncTrigger.ResetRule.ONLY_AFTER_REVERSION)

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
        }.resetRule(SyncTrigger.ResetRule.ONLY_AFTER_REVERSION)

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
        }.resetRule(SyncTrigger.ResetRule.ONLY_AFTER_REVERSION)

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
        }.resetRule(SyncTrigger.ResetRule.IMMEDIATELY)

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
        }.resetRule(SyncTrigger.ResetRule.IMMEDIATELY)

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
        }.resetRule(SyncTrigger.ResetRule.IMMEDIATELY)

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
        }.resetRule(SyncTrigger.ResetRule.IMMEDIATELY)

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
        }.resetRule(SyncTrigger.ResetRule.NEVER)

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
        }.resetRule(SyncTrigger.ResetRule.NEVER)

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
        }.resetRule(SyncTrigger.ResetRule.NEVER)

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
        }.resetRule(SyncTrigger.ResetRule.NEVER)

        // act
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()
        syncTrigger.checkLazy()

        // assert
        verify(exactly = 0) {
            mockDoThis()
        }
    }
}
