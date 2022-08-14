package foo.bar.example.forereactiveuikt.ui.wallet

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import foo.bar.example.forereactiveuikt.OG
import foo.bar.example.forereactiveuikt.R
import foo.bar.example.forereactiveuikt.feature.wallet.Wallet
import kotlinx.android.synthetic.main.activity_wallet.*

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class WalletsActivity : FragmentActivity(R.layout.activity_wallet), SyncableView {

    //models that we need to sync with
    private val wallet: Wallet = OG[Wallet::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(LifecycleObserver(this, wallet))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        wallet_increase_btn.setOnClickListener {
            wallet.increaseMobileWallet() //notice how the reactive ui takes care of updating the view for you
        }
        wallet_decrease_btn.setOnClickListener {
            wallet.decreaseMobileWallet() //notice how the reactive ui takes care of updating the view for you
        }
    }

    override fun syncView() {
        wallet_increase_btn.isEnabled = wallet.canIncrease()
        wallet_decrease_btn.isEnabled = wallet.canDecrease()
        wallet_mobileamount_txt.text = wallet.mobileWalletAmount.toString()
        wallet_savingsamount_txt.text = wallet.savingsWalletAmount.toString()
    }
}
