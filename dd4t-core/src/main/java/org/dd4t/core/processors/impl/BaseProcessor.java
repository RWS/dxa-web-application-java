package org.dd4t.core.processors.impl;

import org.dd4t.core.processors.Processor;
import org.dd4t.core.processors.RunPhase;

/**
 * Extend from the Base Processor to have your Factory Items
 * Post processed. There are three options when a processor can run:
 *  - Before the item is cached in the DD4T object cache
 *  - After the item is cached in the DD4T object cache (this means every time).
 *  - On both occassions, which should happen rarely.
 *
 *  Note: running processors after fetching an item from cache is
 *  perfectly fine, but in case of expensive operations, be sure to
 *  use additional caching (like output caching in a web application) to
 *  cache final output. Even so, as most processors tends to run here, this
 *  is the default phase.
 */
public abstract class BaseProcessor implements Processor {

    private RunPhase runPhase = RunPhase.AFTER_CACHING;

    /**
     * To be configured in a Spring application context.
     *
     * @param phase the Run Phase (Pre, Post, Both)
     */

    @Override public void setRunPhase (final RunPhase phase) {
        this.runPhase = phase;
    }

    @Override public RunPhase getRunPhase () {
        return runPhase;
    }
}
