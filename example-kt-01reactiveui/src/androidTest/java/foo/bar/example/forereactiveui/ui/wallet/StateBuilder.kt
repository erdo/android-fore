package foo.bar.example.forereactiveui.ui.wallet

import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.delegate.TestDelegateDefault
import foo.bar.example.forereactiveui.App
import foo.bar.example.forereactiveui.OG
import foo.bar.example.forereactiveui.feature.wallet.Wallet
import io.mockk.every

/**
 *
 */
class StateBuilder internal constructor(private val mockWallet: Wallet) {

    internal fun withMobileWalletMaximum(totalFundsAvailable: Int): StateBuilder {

        every {
            mockWallet.mobileWalletAmount
        } returns totalFundsAvailable

        every {
            mockWallet.savingsWalletAmount
        } returns 0

        every {
            mockWallet.canDecrease()
        } returns true

        every {
            mockWallet.canIncrease()
        } returns false

        return this
    }

    internal fun withMobileWalletHalfFull(savingsWalletAmount: Int, mobileWalletAmount: Int): StateBuilder {

        every {
            mockWallet.mobileWalletAmount
        } returns mobileWalletAmount

        every {
            mockWallet.savingsWalletAmount
        } returns savingsWalletAmount

        every {
            mockWallet.canDecrease()
        } returns true

        every {
            mockWallet.canIncrease()
        } returns true

        return this
    }

    internal fun withMobileWalletEmpty(totalFundsAvailable: Int): StateBuilder {

        every {
            mockWallet.mobileWalletAmount
        } returns 0

        every {
            mockWallet.savingsWalletAmount
        } returns totalFundsAvailable

        every {
            mockWallet.canDecrease()
        } returns false

        every {
            mockWallet.canIncrease()
        } returns true

        return this
    }

    fun createRule(): ActivityTestRule<WalletsActivity> {
        return object : ActivityTestRule<WalletsActivity>(WalletsActivity::class.java) {
            override fun beforeActivityLaunched() {

                //get hold of the application
                val app: App = ApplicationProvider.getApplicationContext<App>() as App
                OG.setApplication(app)

                ForeDelegateHolder.setDelegate(TestDelegateDefault())

                //inject our mocks so our UI layer will pick them up
                OG.putMock(Wallet::class.java, mockWallet)
            }
        }
    }
}
