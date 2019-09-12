package com.sdl.dxa.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by inikonov on 9/2/2015.
 */
public class Performance implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger("dxa.docs.profiling");
    private long startTime = System.nanoTime();
    private long totalTime;
    private long warnIfMoreThanMillis=2L;
    private String name=Thread.currentThread().getName();
    private static AtomicInteger counter=new AtomicInteger();

    public Performance() {
    }

    public Performance(long warnIfMoreThanMillis) {
        this.warnIfMoreThanMillis = warnIfMoreThanMillis;
    }

    public Performance(long warnIfMoreThanMillis, String name) {
        this(warnIfMoreThanMillis);
        this.name = name;
    }

    public Performance(String name) {
        this.name = name;
    }

    @Override
    public void close() {
        counter.incrementAndGet();
        totalTime = (System.nanoTime() - startTime) / 1_000_000L;
        if (totalTime >= warnIfMoreThanMillis) {
            logger.warn("{} took {} ms ("+counter.get()+" times)", name, totalTime);
        }
    }
}
