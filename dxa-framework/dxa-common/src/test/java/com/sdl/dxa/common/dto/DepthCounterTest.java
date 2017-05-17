package com.sdl.dxa.common.dto;

import org.junit.Test;

import static java.lang.Integer.MAX_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DepthCounterTest {

    @Test
    public void shouldSayIfItIsTooDeep_SoZeroOrLess() {
        assertTrue(new DepthCounter(1).isNotTooDeep());
        assertFalse(new DepthCounter(0).isNotTooDeep());
        assertFalse(new DepthCounter(-1).isNotTooDeep());
    }

    @Test
    public void shouldIncreaseAndCheck_WhileOneStillWorks() {
        //given 
        DepthCounter counter = new DepthCounter(1);

        assertTrue(counter.depthIncreaseAndCheckIfSafe());
        assertEquals(0, counter.getDeep());
        assertFalse(counter.depthIncreaseAndCheckIfSafe());
    }

    @Test
    public void shouldDecreaseDepth() {
        DepthCounter counter = new DepthCounter(0);
        counter.depthDecrease();
        assertEquals(1, counter.getDeep());
    }

    @Test
    public void shouldBeUnlimited_MaxValue() {
        //given 

        //when
        int deep = DepthCounter.UNLIMITED_DEPTH.getDeep();

        //then
        assertEquals(MAX_VALUE, deep);
    }
}