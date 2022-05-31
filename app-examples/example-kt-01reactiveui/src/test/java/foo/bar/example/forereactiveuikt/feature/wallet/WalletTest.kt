package foo.bar.example.forereactiveuikt.feature.wallet

import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.logging.SystemLogger
import co.early.fore.core.observer.Observer
import co.early.fore.kt.core.delegate.TestDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class WalletTest {

    @MockK
    private lateinit var mockObserver: Observer

    @Before
    fun setup() {

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        Fore.setDelegate(TestDelegateDefault())

        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    @Throws(Exception::class)
    fun initialConditions() {

        //arrange
        val wallet = Wallet(logger)

        //act

        //assert
        Assert.assertEquals(true, wallet.canIncrease())
        Assert.assertEquals(false, wallet.canDecrease())
        Assert.assertEquals(wallet.totalDollarsAvailable, wallet.savingsWalletAmount)
        Assert.assertEquals(0, wallet.mobileWalletAmount)
    }

    @Test
    @Throws(Exception::class)
    fun increaseMobileWallet() {

        //arrange
        val wallet = Wallet(logger)

        //act
        wallet.increaseMobileWallet()

        //assert
        Assert.assertEquals(true, wallet.canIncrease())
        Assert.assertEquals(true, wallet.canDecrease())
        Assert.assertEquals(wallet.totalDollarsAvailable - 1, wallet.savingsWalletAmount)
        Assert.assertEquals(1, wallet.mobileWalletAmount)
    }

    @Test
    @Throws(Exception::class)
    fun decreaseMobileWallet() {

        //arrange
        val wallet = Wallet(logger)
        wallet.increaseMobileWallet()
        Assert.assertEquals(1, wallet.mobileWalletAmount)

        //act
        wallet.decreaseMobileWallet()

        //assert
        Assert.assertEquals(true, wallet.canIncrease())
        Assert.assertEquals(false, wallet.canDecrease())
        Assert.assertEquals(wallet.totalDollarsAvailable, wallet.savingsWalletAmount)
        Assert.assertEquals(0, wallet.mobileWalletAmount)
    }

    @Test
    @Throws(Exception::class)
    fun canIncreaseIsFalseAtLimit() {

        //arrange
        val wallet = Wallet(logger)
        for (ii in 0 until wallet.totalDollarsAvailable) {
            wallet.increaseMobileWallet()
        }

        //act

        //assert
        Assert.assertEquals(false, wallet.canIncrease())
        Assert.assertEquals(true, wallet.canDecrease())
    }

    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the reactive ui section of the docs for more information about this
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForIncrease() {

        //arrange
        val wallet = Wallet(logger)
        wallet.addObserver(mockObserver)

        //act
        wallet.increaseMobileWallet()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    @Test
    @Throws(Exception::class)
    fun observersNotifiedAtLeastOnceForDecrease() {

        //arrange
        val wallet = Wallet(logger)
        wallet.increaseMobileWallet()
        wallet.addObserver(mockObserver)

        //act
        wallet.decreaseMobileWallet()

        //assert
        verify(atLeast = 1) {
            mockObserver.somethingChanged()
        }
    }

    companion object {
        private val logger: Logger = SystemLogger()
    }
}