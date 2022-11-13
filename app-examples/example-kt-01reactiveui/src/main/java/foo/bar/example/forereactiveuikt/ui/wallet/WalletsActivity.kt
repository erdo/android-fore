package foo.bar.example.forereactiveuikt.ui.wallet

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import foo.bar.example.forereactiveuikt.OG
import foo.bar.example.forereactiveuikt.databinding.ActivityWalletBinding
import foo.bar.example.forereactiveuikt.feature.wallet.Wallet

/**
 * Copyright © 2015-2020 early.co. All rights reserved.
 */
class WalletsActivity : FragmentActivity(), SyncableView {

    //models that we need to sync with
    private val wallet: Wallet = OG[Wallet::class.java]

    private lateinit var binding: ActivityWalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(LifecycleObserver(this, wallet))

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.walletIncreaseBtn.setOnClickListener {
            wallet.increaseMobileWallet() //notice how the reactive ui takes care of updating the view for you
        }
        binding.walletDecreaseBtn.setOnClickListener {
            wallet.decreaseMobileWallet() //notice how the reactive ui takes care of updating the view for you
        }
    }

    override fun syncView() {
        binding.walletIncreaseBtn.isEnabled = wallet.canIncrease()
        binding.walletDecreaseBtn.isEnabled = wallet.canDecrease()
        binding.walletMobileamountTxt.text = wallet.mobileWalletAmount.toString()
        binding.walletSavingsamountTxt.text = wallet.savingsWalletAmount.toString()
    }
}
