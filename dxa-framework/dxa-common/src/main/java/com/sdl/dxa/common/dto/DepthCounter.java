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
        // Counter must be decremented unconditionally so it stays balanced with
        // the unconditional increment in depthDecrease() that callers run in
        // their finally blocks. The previous implementation short-circuited the
        // decrement via && when counter reached 0, causing every failed check
        // to leak +1 of budget back into the counter — which allowed the model
        // expander to recurse past its intended depth limit. See SRQ-31346.
        if (unlimited) {
            return true;
        }
        counter--;
        return counter >= 0;
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
