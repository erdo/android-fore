package co.early.fore.core.observer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Copyright © 2018 early.co. All rights reserved.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ObserverGroupTest {

    @Mock
    private Observable mockObservable1;
    @Mock
    private Observable mockObservable2;
    @Mock
    private Observable mockObservable3;
    @Mock
    private Observer mockObserver1;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void whenObserverAdded_observablesAreCalled() {

        //arrange
        ObservableGroup observableGroup = new ObservableGroupImp(
                mockObservable1, mockObservable2, mockObservable3);

        //act
        observableGroup.addObserver(mockObserver1);

        //assert
        verify(mockObservable1, times(1)).addObserver(eq(mockObserver1));
        verify(mockObservable2, times(1)).addObserver(eq(mockObserver1));
        verify(mockObservable3, times(1)).addObserver(eq(mockObserver1));
    }

    @Test
    public void whenObserverRemoved_observablesAreCalled() {

        //arrange
        ObservableGroup observableGroup = new ObservableGroupImp(
                mockObservable1, mockObservable2, mockObservable3);

        //act
        observableGroup.removeObserver(mockObserver1);

        //assert
        verify(mockObservable1, times(1)).removeObserver(eq(mockObserver1));
        verify(mockObservable2, times(1)).removeObserver(eq(mockObserver1));
        verify(mockObservable3, times(1)).removeObserver(eq(mockObserver1));
    }

    @Test
    public void whenInitialisedWithNullObservablesList_throwsException() {

        //arrange
        boolean exceptionThrown = false;

        //act
        try {
            ObservableGroup observableGroup = new ObservableGroupImp((Observable) null);
        } catch (Throwable npe) {
            exceptionThrown = true;
        }

        //assert
        Assert.assertTrue(exceptionThrown);
    }

    @Test
    public void whenInitialisedWithSingleNullObservable_throwsException() {

        //arrange
        boolean exceptionThrown = false;

        //act
        try {
            ObservableGroup observableGroup = new ObservableGroupImp(
                    mockObservable1, null, mockObservable3);
        } catch (Throwable re) {
            exceptionThrown = true;
        }

        //assert
        Assert.assertTrue(exceptionThrown);
    }
}
