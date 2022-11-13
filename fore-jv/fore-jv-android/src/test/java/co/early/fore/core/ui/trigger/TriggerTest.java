package co.early.fore.core.ui.trigger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static co.early.fore.core.ui.trigger.Trigger.ResetRule.IMMEDIATELY;
import static co.early.fore.core.ui.trigger.Trigger.ResetRule.NEVER;
import static co.early.fore.core.ui.trigger.Trigger.ResetRule.ONLY_AFTER_REVERSION;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

import co.early.fore.core.ui.trigger.Trigger;


public class TriggerTest {

    @Mock
    private Threshold mockThreshold;

    @Mock
    private Triggered mockTriggered;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void testCheckLazyReset_IMMEDIATELY() {

        Trigger trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                IMMEDIATELY);

        //check
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);


        trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                IMMEDIATELY);

        // checkLazy threshold breached, but trigger
        // is swallowed for first trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //immediately reset and triggers
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //immediately reset and triggers
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);
    }

    @Test
    public void testCheckLazyReset_ONLY_AFTER_REVERSION() {

        Trigger trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                ONLY_AFTER_REVERSION);

        //check
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);


        trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                ONLY_AFTER_REVERSION);

        // checkLazy threshold breached, but trigger
        // is swallowed for first trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //not yet reset, trigger not fired
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //now triggers once more
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);
    }

    @Test
    public void testCheckLazyReset_NEVER() {

        Trigger trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                NEVER);

        //check
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);


        trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                NEVER);

        // checkLazy threshold breached, but trigger
        // is swallowed for first trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //not reset, trigger not fired
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //threshold no longer breached, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //never reset so no trigger still, even with threshold breached
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.checkLazy();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);
    }

    @Test
    public void testCheckReset_IMMEDIATELY() {

        Trigger trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                IMMEDIATELY);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //no reset, still above threshold
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);
    }

    @Test
    public void testCheckReset_ONLY_AFTER_REVERSION() {

        Trigger trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                ONLY_AFTER_REVERSION);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //no reset, still above threshold
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);
    }

    @Test
    public void testCheckReset_NEVER() {

        Trigger trigger = new Trigger(
                () -> mockTriggered.triggered(),
                () -> mockThreshold.shouldTrigger(),
                NEVER);

        //no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, times(1)).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //no reset, still above threshold
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //below threshold, but never reset, no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(false);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);

        //still no trigger
        when(mockThreshold.shouldTrigger()).thenReturn(true);
        trigger.check();
        verify(mockTriggered, never()).triggered();
        verify(mockThreshold, times(1)).shouldTrigger();
        reset(mockTriggered);
        reset(mockThreshold);
    }


    class Threshold{
        public boolean shouldTrigger(){
            return true;
        }
    }

    class Triggered {
        public void triggered(){
            return;
        }
    }
}
