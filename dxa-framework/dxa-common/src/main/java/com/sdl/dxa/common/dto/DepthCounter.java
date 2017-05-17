package com.sdl.dxa.common.dto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DepthCounter {

    public static final DepthCounter UNLIMITED_DEPTH = new DepthCounter(Integer.MAX_VALUE);

    private int counter;

    public DepthCounter(int counter) {
        log.trace("Started counter with max depth = {}", counter);
        this.counter = counter;
    }

    public synchronized boolean isNotTooDeep() {
        return counter > 0;
    }

    public synchronized boolean depthIncreaseAndCheckIfSafe() {
        return isNotTooDeep() && counter-- > 0;
    }

    public synchronized void depthDecrease() {
        counter++;
    }

    public synchronized int getDeep() {
        return counter;
    }
}
