package com.sdl.dxa.common.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@ToString
public final class DepthCounter {

    public static final DepthCounter UNLIMITED_DEPTH = new DepthCounter(Integer.MAX_VALUE);

    private int counter;

    private boolean unlimited;

    public DepthCounter(int counter) {
        if (counter < 0 || counter == Integer.MAX_VALUE) {
            log.trace("Started a counter with value < 0, consider it unlimited");
            this.unlimited = true;
        }
        log.trace("Started counter with max depth = {}", counter);
        this.counter = counter >= 0 ? counter : Integer.MAX_VALUE;
    }

    public synchronized boolean isNotTooDeep() {
        return counter > 0;
    }

    public synchronized boolean depthIncreaseAndCheckIfSafe() {
        return isNotTooDeep() && counter-- > 0;
    }

    public synchronized boolean depthIncreaseAndCheckIfSafe(int levels) {
        if (isNotTooDeep() && counter >= levels) {
            this.counter = counter - levels;
            return true;
        }
        return false;
    }

    public synchronized void depthDecrease() {
        counter++;
    }

    public synchronized int getCounter() {
        return counter;
    }

    public boolean isUnlimited() {
        return unlimited;
    }
}
