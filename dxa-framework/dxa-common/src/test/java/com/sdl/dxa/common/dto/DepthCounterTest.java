package com.sdl.dxa.common.dto;

import org.junit.jupiter.api.Test;

import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.*;

public class DepthCounterTest {

    @Test
    public void shouldSayIfItIsTooDeep_IfZero() {
        assertTrue(new DepthCounter(1).isNotTooDeep());
        assertFalse(new DepthCounter(0).isNotTooDeep());
    }

    @Test
    public void shouldIncreaseAndCheck_WhileOneStillWorks() {
        //given 
        DepthCounter counter = new DepthCounter(1);

        assertTrue(counter.depthIncreaseAndCheckIfSafe());
        assertEquals(0, counter.getCounter());
        assertFalse(counter.depthIncreaseAndCheckIfSafe());
    }

    @Test
    public void shouldIncreaseByManyLevelsCheck() {
        //given
        DepthCounter counter = new DepthCounter(15);

        assertTrue(counter.depthIncreaseAndCheckIfSafe(10));
        assertEquals(5, counter.getCounter());
        assertFalse(counter.depthIncreaseAndCheckIfSafe(10));
        assertTrue(counter.depthIncreaseAndCheckIfSafe(counter.getCounter()));
        assertFalse(counter.depthIncreaseAndCheckIfSafe(5));
    }


    @Test
    public void shouldDecreaseDepth() {
        DepthCounter counter = new DepthCounter(0);
        counter.depthDecrease();
        assertEquals(1, counter.getCounter());
    }

    /**
     * Regression for SRQ-31346.
     * <p>
     * Callers of {@link DepthCounter#depthIncreaseAndCheckIfSafe()} pair each
     * call with an unconditional {@link DepthCounter#depthDecrease()} in a
     * finally block. For the counter to stay balanced, the increment-and-check
     * method must decrement on every call, including when the check fails.
     * <p>
     * The previous implementation short-circuited the decrement via {@code &&}
     * when the counter reached zero. The {@code finally}-block decrement then
     * leaked +1 of budget back into the counter on every "too deep" event,
     * letting the page model expander recurse past its intended depth limit
     * and ultimately overflow the JVM stack on heavily nested page models.
     */
    @Test
    public void depthIncreaseAndCheckIfSafe_doesNotLeakWhenLimitExceeded() {
        DepthCounter counter = new DepthCounter(2);

        // Budget gets consumed.
        assertTrue(counter.depthIncreaseAndCheckIfSafe());
        assertTrue(counter.depthIncreaseAndCheckIfSafe());
        assertEquals(0, counter.getCounter());

        // Past the limit: check must fail AND counter must still be decremented
        // so the matching depthDecrease() restores it cleanly.
        assertFalse(counter.depthIncreaseAndCheckIfSafe());
        counter.depthDecrease();
        assertEquals(0, counter.getCounter());

        // Repeated failures stay balanced — no accumulating budget leak.
        for (int i = 0; i < 100; i++) {
            assertFalse(counter.depthIncreaseAndCheckIfSafe());
            counter.depthDecrease();
        }
        assertEquals(0, counter.getCounter());
    }

    /**
     * Unlimited counters must not be decremented on each call — otherwise
     * Integer.MAX_VALUE - 1 calls would silently flip them into limited mode.
     */
    @Test
    public void depthIncreaseAndCheckIfSafe_unlimitedAlwaysSafe() {
        DepthCounter counter = DepthCounter.UNLIMITED_DEPTH;

        for (int i = 0; i < 1000; i++) {
            assertTrue(counter.depthIncreaseAndCheckIfSafe());
        }
        assertEquals(MAX_VALUE, counter.getCounter());
    }

    @Test
    public void shouldBeUnlimited_MaxValue() {
        //given 

        //when
        int deep = DepthCounter.UNLIMITED_DEPTH.getCounter();

        //then
        assertEquals(MAX_VALUE, deep);
    }

    @Test
    public void shouldConsiderNegativeValuesAsUnlimited() {
        //given 

        //when
        DepthCounter counter = new DepthCounter(-1);

        //then
        assertEquals(DepthCounter.UNLIMITED_DEPTH, counter);
    }
}