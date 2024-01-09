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