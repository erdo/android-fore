package foo.bar.example.asafdatabinding.feature.wallet;

import org.junit.Assert;
import org.junit.Test;

import co.early.asaf.framework.logging.Logger;
import co.early.asaf.framework.logging.TestLogger;
import co.early.asaf.framework.observer.Observer;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class WalletTest {


    private static Logger logger = new TestLogger();

    @Test
    public void initialConditions() throws Exception {

        //arrange
        Wallet wallet = new Wallet(logger);

        //act

        //assert
        Assert.assertEquals(true, wallet.canIncrease());
        Assert.assertEquals(false, wallet.canDecrease());
        Assert.assertEquals(wallet.totalDollarsAvailable, wallet.getSavingsWalletAmount());
        Assert.assertEquals(0, wallet.getMobileWalletAmount());
    }


    @Test
    public void increaseMobileWallet() throws Exception {

        //arrange
        Wallet wallet = new Wallet(logger);

        //act
        wallet.increaseMobileWallet();

        //assert
        Assert.assertEquals(true, wallet.canIncrease());
        Assert.assertEquals(true, wallet.canDecrease());
        Assert.assertEquals(wallet.totalDollarsAvailable - 1, wallet.getSavingsWalletAmount());
        Assert.assertEquals(1, wallet.getMobileWalletAmount());
    }


    @Test
    public void decreaseMobileWallet() throws Exception {

        //arrange
        Wallet wallet = new Wallet(logger);
        wallet.increaseMobileWallet();
        Assert.assertEquals(1, wallet.getMobileWalletAmount());

        //act
        wallet.decreaseMobileWallet();

        //assert
        Assert.assertEquals(true, wallet.canIncrease());
        Assert.assertEquals(false, wallet.canDecrease());
        Assert.assertEquals(wallet.totalDollarsAvailable, wallet.getSavingsWalletAmount());
        Assert.assertEquals(0, wallet.getMobileWalletAmount());
    }


    @Test
    public void canIncreaseIsFalseAtLimit() throws Exception {

        //arrange
        Wallet wallet = new Wallet(logger);
        for (int ii=0; ii<wallet.totalDollarsAvailable; ii++){
            wallet.increaseMobileWallet();
        }

        //act

        //assert
        Assert.assertEquals(false, wallet.canIncrease());
        Assert.assertEquals(true, wallet.canDecrease());
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
     * See the databinding readme for more information about this
     *
     * @throws Exception
     */
    @Test
    public void observersNotifiedAtLeastOnceForIncrease() throws Exception {

        //arrange
        Wallet wallet = new Wallet(logger);
        Observer mockObserver = mock(Observer.class);
        wallet.addObserver(mockObserver);

        //act
        wallet.increaseMobileWallet();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }

    @Test
    public void observersNotifiedAtLeastOnceForDecrease() throws Exception {

        //arrange
        Wallet wallet = new Wallet(logger);
        wallet.increaseMobileWallet();
        Observer mockObserver = mock(Observer.class);
        wallet.addObserver(mockObserver);

        //act
        wallet.decreaseMobileWallet();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }



}
