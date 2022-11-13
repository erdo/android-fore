package co.early.fore.core.observer;

import android.os.Looper;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.early.fore.core.WorkMode;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.logging.SystemLogger;

import static co.early.fore.core.testhelpers.CountDownLatchWrapper.runInBatch;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.robolectric.shadows.ShadowLooper.runUiThreadTasks;

/**
 * Copyright © 2018 early.co. All rights reserved.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ObserverTest {

    @Mock
    private Logger mockLogger;
    @Mock
    private Observer mockObserver1;
    @Mock
    private Observer mockObserver2;
    @Mock
    private Observer mockObserver3;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void whenInitialised_withSynchronousAndLogger_stateIsCorrect() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS, mockLogger);

        //act

        //assert
        verifyZeroInteractions(mockLogger);
        Assert.assertFalse(observable.hasObservers());
    }

    @Test
    public void whenInitialised_withSynchronousAndNoLogger_stateIsCorrect() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);

        //act

        //assert
        verifyZeroInteractions(mockLogger);
        Assert.assertFalse(observable.hasObservers());
    }

    @Test
    public void whenInitialised_withAsynchronousAndLogger_stateIsCorrect() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS, mockLogger);

        //act

        //assert
        verifyZeroInteractions(mockLogger);
        Assert.assertFalse(observable.hasObservers());
    }

    @Test
    public void whenInitialised_withAsynchronousAndNoLogger_stateIsCorrect() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS);

        //act

        //assert
        verifyZeroInteractions(mockLogger);
        Assert.assertFalse(observable.hasObservers());
    }

    @Test
    public void whenOneObserverAdded_withSynchronousAndLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS, mockLogger);
        observable.addObserver(mockObserver1);

        //act
        observable.notifyObservers();

        //assert
        verifyZeroInteractions(mockLogger);
        verify(mockObserver1, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenOneObserverAdded_withSynchronousAndNoLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);
        observable.addObserver(mockObserver1);

        //act
        observable.notifyObservers();

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenOneObserverAdded_withAsynchronousAndLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS, mockLogger);
        observable.addObserver(mockObserver1);

        //act
        runInBatch(1, observable, observable::notifyObservers);

        //assert
        verifyZeroInteractions(mockLogger);
        verify(mockObserver1, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenOneObserverAdded_withAsynchronousAndNoLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS);
        observable.addObserver(mockObserver1);

        //act
        runInBatch(1, observable, () -> {
            observable.notifyObservers();
        });

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenTwoObserversAdded_withSynchronousAndLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS, mockLogger);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);

        //act
        observable.notifyObservers();

        //assert
        verifyZeroInteractions(mockLogger);
        verify(mockObserver1, times(1)).somethingChanged();
        verify(mockObserver2, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenTwoObserversAdded_withSynchronousAndNoLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);

        //act
        observable.notifyObservers();

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        verify(mockObserver2, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenTwoObserversAdded_withAsynchronousAndLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS, mockLogger);

        int[] results = new int[1];
        results[0] = 0;

        CountDownLatch countDownLatch = new CountDownLatch(2);
        Observer realObserver1 = () -> {
            results[0]++;
            countDownLatch.countDown();
        };
        Observer realObserver2 = () -> {
            results[0]++;
            countDownLatch.countDown();
        };

        observable.addObserver(realObserver1);
        observable.addObserver(realObserver2);


        //act
        observable.notifyObservers();
        countDownLatch.await(10, TimeUnit.MILLISECONDS);


        //assert
        verifyZeroInteractions(mockLogger);
        Assert.assertEquals(2, results[0]);
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenTwoObserversAdded_withAsynchronousAndNoLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);

        //act
        runInBatch(1, observable, () -> {
            observable.notifyObservers();
        });

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        verify(mockObserver2, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenThreeObserversAdded_withSynchronousAndLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS, mockLogger);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);
        observable.addObserver(mockObserver3);

        //act
        observable.notifyObservers();

        //assert
        verify(mockLogger, times(1)).w(anyString(), anyString());
        verifyNoMoreInteractions(mockLogger);
        verify(mockObserver1, times(1)).somethingChanged();
        verify(mockObserver2, times(1)).somethingChanged();
        verify(mockObserver3, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenThreeObserversAdded_withSynchronousAndNoLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);
        observable.addObserver(mockObserver3);

        //act
        observable.notifyObservers();

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        verify(mockObserver2, times(1)).somethingChanged();
        verify(mockObserver3, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenThreeObserversAdded_withAsynchronousAndLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS, mockLogger);

        int[] results = new int[1];
        results[0] = 0;

        CountDownLatch countDownLatch = new CountDownLatch(3);
        Observer realObserver1 = () -> {
            results[0]++;
            countDownLatch.countDown();
        };
        Observer realObserver2 = () -> {
            results[0]++;
            countDownLatch.countDown();
        };
        Observer realObserver3 = () -> {
            results[0]++;
            countDownLatch.countDown();
        };

        observable.addObserver(realObserver1);
        observable.addObserver(realObserver2);
        observable.addObserver(realObserver3);


        //act
        observable.notifyObservers();
        countDownLatch.await(10, TimeUnit.MILLISECONDS);


        //assert
        verify(mockLogger, times(1)).w(anyString(), anyString());
        verifyNoMoreInteractions(mockLogger);
        Assert.assertEquals(3, results[0]);
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenThreeObserversAdded_withAsynchronousAndNoLogger_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);
        observable.addObserver(mockObserver3);

        //act
        runInBatch(1, observable, () -> {
            observable.notifyObservers();
        });

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        verify(mockObserver2, times(1)).somethingChanged();
        verify(mockObserver3, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenThreeObserversAdded_thenTwoRemoved_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);
        observable.addObserver(mockObserver3);

        observable.removeObserver(mockObserver1);
        observable.removeObserver(mockObserver2);

        //act
        observable.notifyObservers();

        //assert
        verifyZeroInteractions(mockObserver1);
        verifyZeroInteractions(mockObserver2);
        verify(mockObserver3, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenThreeObserversAdded_thenThreeRemoved_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);
        observable.addObserver(mockObserver3);

        observable.removeObserver(mockObserver1);
        observable.removeObserver(mockObserver2);
        observable.removeObserver(mockObserver3);

        //act
        observable.notifyObservers();

        //assert
        verifyZeroInteractions(mockObserver1);
        verifyZeroInteractions(mockObserver2);
        verifyZeroInteractions(mockObserver3);
        Assert.assertFalse(observable.hasObservers());
    }

    @Test
    public void whenTwoObserversAdded_thenNotify5Times_notificationsFiredCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS);
        observable.addObserver(mockObserver1);
        observable.addObserver(mockObserver2);

        //act
        observable.notifyObservers();
        observable.notifyObservers();
        observable.notifyObservers();
        observable.notifyObservers();
        observable.notifyObservers();

        //assert
        verify(mockObserver1, times(5)).somethingChanged();
        verify(mockObserver2, times(5)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    public void whenExceptionThrowingObserverAdded_withNotificationOnNonUIThread_notifyWarnsCorrectly() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.SYNCHRONOUS, mockLogger);
        Observer exceptionThrowingObserver = () -> {
            throw new RuntimeException();
        };
        observable.addObserver(exceptionThrowingObserver);


        //act
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            try {
                observable.notifyObservers();
            } catch (Throwable t) {
                //ignore
            } finally {
                countDownLatch.countDown();
            }
        });
        thread.start();
        countDownLatch.await();


        //assert
        verify(mockLogger, times(2)).e(any(), any());
        Assert.assertTrue(observable.hasObservers());
    }


    /**
     * The handling of threads and loopers in Robolectric is a bit basic unfortunately,
     * we are going to have a hard time testing the finer points of the notification
     * handling in fore until these issues get resolved:
     *
     * https://github.com/robolectric/robolectric/issues/1306
     * https://github.com/robolectric/robolectric/pull/2166
     * https://github.com/robolectric/robolectric/issues/2977
     *
     * So the following tests have been ignored for the moment until we can work out a
     * nice way to test the threading behaviour of fore
     */

    @Test
    @Ignore("This test makes sure that if we call notifyObservers() on the UI thread - even when we" +
            "are setup in ASYNCHRONOUS mode: the somethingChanged() notification will be called inline" +
            "_without_ being posted to the end of the message queue (this is important for supporting" +
            "android adapters). Unfortunately when we run this test with Robolectric, the Handler implementation" +
            "always runs in-line, so the test would always pass, even if the code was failing.")
    public void whenNotifyOnUIThread_withAsynchronousSetup_notificationsSentOnUIThreadWithNoYield() throws Exception {

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS);
        observable.addObserver(mockObserver1);

        //act
        observable.notifyObservers();

        //assert
        verify(mockObserver1, times(1)).somethingChanged();
        Assert.assertTrue(observable.hasObservers());
    }

    @Test
    @Ignore("This test makes sure that if we call notifyObservers() on a thread which isn't the UI thread," +
            "the somethingChanged() notification will come through on the UI thread providing we are setup with" +
            "ASYNCHRONOUS mode. Unfortunately when we run this test with Robolectric, the Handler implementation" +
            "always fires on the same thread it was created on, so the test always fails, even if the code is correct.")
    public void whenNotifyOffUIThread_withAsynchronousSetup_notificationsSentOnUIThread() throws Exception {

        Logger logger = new SystemLogger();

        //arrange
        Observable observable = new ObservableImp(WorkMode.ASYNCHRONOUS, logger);
        int[] results = new int[1];
        results[0] = 0;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Observer realObserver = () -> {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                results[0]++;
            }
            countDownLatch.countDown();
            Looper.myLooper().quit();
        };
        observable.addObserver(realObserver);


        //act
        Thread thread = new Thread(() -> {
            Looper.prepare();
            observable.notifyObservers();
            runUiThreadTasks();
        });
        thread.start();
        countDownLatch.await();


        //assert
        Assert.assertEquals(1, results[0]);
    }

}
