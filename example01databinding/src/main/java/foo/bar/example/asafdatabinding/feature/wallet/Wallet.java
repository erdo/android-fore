package foo.bar.example.asafdatabinding.feature.wallet;


import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableImp;

/**
 *
 */
public class Wallet extends ObservableImp{

    private static final String TAG = Wallet.class.getSimpleName();

    private final Logger logger;
    public final int totalDollarsAvailable = 10;
    private int mobileWalletDollars = 0;


    public Wallet(Logger logger) {
        super(WorkMode.SYNCHRONOUS, logger);
        this.logger = Affirm.notNull(logger);
    }


    public void increaseMobileWallet() {
        if (canIncrease()) {
            mobileWalletDollars++;
            logger.i(TAG, "Increasing mobile wallet to:" + mobileWalletDollars);
            notifyObservers();
        }
    }

    public void decreaseMobileWallet(){
        if (canDecrease()) {
            mobileWalletDollars--;
            logger.i(TAG, "Decreasing mobile wallet to:" + mobileWalletDollars);
            notifyObservers();
        }
    }

    public int getMobileWalletAmount(){
        return mobileWalletDollars;
    }

    public int getSavingsWalletAmount(){
        return totalDollarsAvailable - mobileWalletDollars;
    }

    public boolean canIncrease(){
      return mobileWalletDollars<totalDollarsAvailable;
    }

    public boolean canDecrease(){
        return mobileWalletDollars>0;
    }

}
