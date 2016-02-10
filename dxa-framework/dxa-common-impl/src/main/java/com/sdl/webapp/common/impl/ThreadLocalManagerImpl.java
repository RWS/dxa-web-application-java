package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.ThreadLocalManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
/**
 * <p>ThreadLocalManagerImpl class.</p>
 */
public class ThreadLocalManagerImpl implements ThreadLocalManager {

    private List<ThreadLocal<?>> threadLocals = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void registerThreadLocal(ThreadLocal<?> threadLocal) {
        this.threadLocals.add(threadLocal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void unregisterThreadLocal(ThreadLocal<?> threadLocal) {
        this.threadLocals.remove(threadLocal);
    }

    /**
     * <p>clearAll.</p>
     */
    public void clearAll() {
        for (ThreadLocal<?> threadLocal : this.threadLocals) {
            threadLocal.remove();
        }
    }
}
