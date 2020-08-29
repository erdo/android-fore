package foo.bar.example.forereactiveui.feature.wallet

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.observer.ObservableImp

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class Wallet(private val logger: Logger) : Observable by ObservableImp() {

    val totalDollarsAvailable = 10
    var mobileWalletAmount = 0
        private set

    fun increaseMobileWallet() {
        if (canIncrease()) {
            mobileWalletAmount++
            logger.i("Increasing mobile wallet to:$mobileWalletAmount")
            notifyObservers() // our state has changed, so we need to let our observers know
        }
    }

    fun decreaseMobileWallet() {
        if (canDecrease()) {
            mobileWalletAmount--
            logger.i("Decreasing mobile wallet to:$mobileWalletAmount")
            notifyObservers() // our state has changed, so we need to let our observers know
        }
    }

    val savingsWalletAmount: Int
        get() = totalDollarsAvailable - mobileWalletAmount

    fun canIncrease(): Boolean = mobileWalletAmount < totalDollarsAvailable

    fun canDecrease(): Boolean = mobileWalletAmount > 0
}
