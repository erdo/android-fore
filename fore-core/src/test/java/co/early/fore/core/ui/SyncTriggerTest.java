package co.early.fore.core.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static co.early.fore.core.ui.SyncTrigger.ResetRule.IMMEDIATELY;
import static co.early.fore.core.ui.SyncTrigger.ResetRule.NEVER;
import static co.early.fore.core.ui.SyncTrigger.ResetRule.ONLY_AFTER_REVERSION;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;


public class SyncTriggerTest {

    @Mock
    private Threshold mockThreshold;

    @Mock
    private Trigger mockTrigger;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void testCheckLazyReset_IMMEDIATELY() {

        SyncTrigger syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                IMMEDIATELY);

        //check
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);


        syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                IMMEDIATELY);

        // checkLazy threshold breached, but trigger
        // is swallowed for first trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //immediately reset and triggers
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //immediately reset and triggers
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);
    }

    @Test
    public void testCheckLazyReset_ONLY_AFTER_REVERSION() {

        SyncTrigger syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                ONLY_AFTER_REVERSION);

        //check
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);


        syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                ONLY_AFTER_REVERSION);

        // checkLazy threshold breached, but trigger
        // is swallowed for first trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //not yet reset, trigger not fired
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //now triggers once more
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);
    }

    @Test
    public void testCheckLazyReset_NEVER() {

        SyncTrigger syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                NEVER);

        //check
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);


        syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                NEVER);

        // checkLazy threshold breached, but trigger
        // is swallowed for first trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //not reset, trigger not fired
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //threshold no longer breached, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //never reset so no trigger still, even with threshold breached
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.checkLazy();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);
    }

    @Test
    public void testCheckReset_IMMEDIATELY() {

        SyncTrigger syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                IMMEDIATELY);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //no reset, still above threshold
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);
    }

    @Test
    public void testCheckReset_ONLY_AFTER_REVERSION() {

        SyncTrigger syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                ONLY_AFTER_REVERSION);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //no reset, still above threshold
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);
    }

    @Test
    public void testCheckReset_NEVER() {

        SyncTrigger syncTrigger = new SyncTrigger(
                () -> mockTrigger.triggered(),
                () -> mockThreshold.shouldTrigger(),
                NEVER);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //no reset, still above threshold
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //below threshold, but never reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);

        //still no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        syncTrigger.check();
        verify(mockTrigger, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTrigger);
        reset(mockThreshold);
    }


    class Threshold{
        public boolean shouldTrigger(){
            return true;
        }
    }

    class Trigger{
        public void triggered(){
            return;
        }
    }
}
