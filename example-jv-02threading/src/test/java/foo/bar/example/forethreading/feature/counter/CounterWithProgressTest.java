package foo.bar.example.forethreading.feature.counter;

import org.junit.Assert;
import org.junit.Test;

import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;
import co.early.fore.core.observer.Observer;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class CounterWithProgressTest {


    private static Logger logger = new SystemLogger();

    @Test
    public void initialConditions() throws Exception {

        //arrange
        CounterWithProgress counterWithProgress = new CounterWithProgress(WorkMode.SYNCHRONOUS, logger);

        //act

        //assert
        Assert.assertEquals(false, counterWithProgress.isBusy());
        Assert.assertEquals(0, counterWithProgress.getProgress());
        Assert.assertEquals(0, counterWithProgress.getCount());
    }


    @Test
    public void increasesBy20() throws Exception {

        //arrange
        CounterWithProgress counterWithProgress = new CounterWithProgress(WorkMode.SYNCHRONOUS, logger);

        //act
        counterWithProgress.increaseBy20();

        //assert
        Assert.assertEquals(false, counterWithProgress.isBusy());
        Assert.assertEquals(0, counterWithProgress.getProgress());
        Assert.assertEquals(20, counterWithProgress.getCount());
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
    public void observersNotifiedAtLeastOnce() throws Exception {

        //arrange
        CounterWithProgress counterWithProgress = new CounterWithProgress(WorkMode.SYNCHRONOUS, logger);
        Observer mockObserver = mock(Observer.class);
        counterWithProgress.addObserver(mockObserver);

        //act
        counterWithProgress.increaseBy20();

        //assert
        verify(mockObserver, atLeastOnce()).somethingChanged();
    }

    /**
     * Not so easy to test. We'll skip mockito for this test and do it manually.
     * We will test that the progress is:
     *
     * - published 20 times during one call to increaseBy20()
     * - never decreases from the last progress value (except when it gets set back to 0)
     *
     * @throws Exception
     */
    @Test
    public void progressIsPublished() throws Exception {

        //arrange
        final CounterWithProgress counterWithProgress = new CounterWithProgress(WorkMode.SYNCHRONOUS, logger);
        final ProgressTracker pt = new ProgressTracker();
        counterWithProgress.addObserver(() -> {
            int latestProgess = counterWithProgress.getProgress();
            if (latestProgess!=0){//or we just ignore it
                Assert.assertEquals(true, latestProgess >= pt.latestProgress);//never want progress to go down
                if (latestProgess > pt.latestProgress) {//if progress ticks up, then we count it as a progress publication
                    pt.latestProgress = latestProgess;
                    pt.numberOfProgressPublications++;
                }
            }
        });

        //act
        counterWithProgress.increaseBy20();

        //assert
        Assert.assertEquals(20, pt.numberOfProgressPublications);
    }


    private class ProgressTracker{
        public int numberOfProgressPublications = 0;
        public int latestProgress = 0;
    }


}
