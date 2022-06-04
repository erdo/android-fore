package foo.bar.example.forereactiveuikt.ui.wallet

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView
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

    //single observer reference
    var observer = Observer { syncView() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    //reactive UI stuff below
    override fun syncView() {
        wallet_increase_btn.isEnabled = wallet.canIncrease()
        wallet_decrease_btn.isEnabled = wallet.canDecrease()
        wallet_mobileamount_txt.text = wallet.mobileWalletAmount.toString()
        wallet_savingsamount_txt.text = wallet.savingsWalletAmount.toString()
    }

    override fun onStart() {
        super.onStart()
        wallet.addObserver(observer)
        syncView() //  <- don't forget this
    }

    override fun onStop() {
        super.onStop()
        wallet.removeObserver(observer)
    }
}
