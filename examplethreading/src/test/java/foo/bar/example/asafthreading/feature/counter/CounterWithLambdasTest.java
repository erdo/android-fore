package foo.bar.example.asafthreading.feature.counter;

import org.junit.Assert;
import org.junit.Test;

import co.early.asaf.framework.WorkMode;
import co.early.asaf.framework.logging.Logger;
import co.early.asaf.framework.logging.TestLogger;
import co.early.asaf.framework.observer.Observer;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class CounterWithLambdasTest {


    private static Logger logger = new TestLogger();

    @Test
    public void initialConditions() throws Exception {

        //arrange
        CounterWithLambdas counterWithLambdas = new CounterWithLambdas(WorkMode.SYNCHRONOUS, logger);

        //act

        //assert
        Assert.assertEquals(false, counterWithLambdas.isBusy());
        Assert.assertEquals(0, counterWithLambdas.getCount());
    }


    @Test
    public void increasesBy20() throws Exception {

        //arrange
        CounterWithLambdas counterWithLambdas = new CounterWithLambdas(WorkMode.SYNCHRONOUS, logger);

        //act
        counterWithLambdas.increaseBy20();

        //assert
        Assert.assertEquals(false, counterWithLambdas.isBusy());
        Assert.assertEquals(20, counterWithLambdas.getCount());
    }


    /**
     *
     * NB all we are checking here is that observers are called AT LEAST once
     *
     * We don't really want tie our tests (OR any observers in production code)
     * to an expected number of times this method might be called. (This would be
     * testing an implementation detail and make the tests unnecessarily brittle)
     *
     * The contract says nothing about how many times the observers will get called,
     * only that they will be called if something changes ("something" is not defined
     * and can change between implementations).
     *
     * See the databinding readme for more information about this
     *
     * @throws Exception
     */
    @Test
    public void observersNotifiedAtLeastOnce() throws Exception {

        //arrange
        CounterWithLambdas counterWithLambdas = new CounterWithLambdas(WorkMode.SYNCHRONOUS, logger);
        Observer mockObserver = mock(Observer.class);
        counterWithLambdas.addObserver(mockObserver);

        //act
        counterWithLambdas.increaseBy20();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }

}
