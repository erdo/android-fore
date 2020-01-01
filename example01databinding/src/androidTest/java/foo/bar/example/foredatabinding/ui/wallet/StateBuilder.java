package foo.bar.example.foredatabinding.ui.wallet;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import co.early.fore.core.WorkMode;
import foo.bar.example.foredatabinding.App;
import foo.bar.example.foredatabinding.OG;
import foo.bar.example.foredatabinding.feature.wallet.Wallet;

import static org.mockito.Mockito.when;

/**
 *
 */
public class StateBuilder {

    private Wallet mockWallet;

    StateBuilder(Wallet mockWallet) {
        this.mockWallet = mockWallet;
    }

    StateBuilder withMobileWalletMaximum(int totalFundsAvailable) {
        when(mockWallet.getMobileWalletAmount()).thenReturn(totalFundsAvailable);
        when(mockWallet.getSavingsWalletAmount()).thenReturn(0);
        when(mockWallet.canDecrease()).thenReturn(true);
        when(mockWallet.canIncrease()).thenReturn(false);
        return this;
    }

    StateBuilder withMobileWalletHalfFull(int savingsWalletAmount, int mobileWalletAmount) {
        when(mockWallet.getMobileWalletAmount()).thenReturn(mobileWalletAmount);
        when(mockWallet.getSavingsWalletAmount()).thenReturn(savingsWalletAmount);
        when(mockWallet.canDecrease()).thenReturn(true);
        when(mockWallet.canIncrease()).thenReturn(true);
        return this;
    }

    StateBuilder withMobileWalletEmpty(int totalFundsAvailable) {
        when(mockWallet.getMobileWalletAmount()).thenReturn(0);
        when(mockWallet.getSavingsWalletAmount()).thenReturn(totalFundsAvailable);
        when(mockWallet.canDecrease()).thenReturn(false);
        when(mockWallet.canIncrease()).thenReturn(true);
        return this;
    }


    ActivityTestRule<WalletsActivity>  createRule(){

        return new ActivityTestRule<WalletsActivity>(WalletsActivity.class) {
            @Override
            protected void beforeActivityLaunched() {

                //get hold of the application
                App app = (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
                OG.setApplication(app, WorkMode.SYNCHRONOUS);

                //inject our mocks so our UI layer will pick them up
                OG.putMock(Wallet.class, mockWallet);
            }

        };
    }

}
