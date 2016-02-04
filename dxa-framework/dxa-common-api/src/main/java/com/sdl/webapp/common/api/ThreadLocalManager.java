package com.sdl.webapp.common.api;

/**
 * ThreadLocal Manager.
 * Manage all registered thread local variables and clear them after each request.
 *
 * @author nic
 * @version 1.3-SNAPSHOT
 */
public interface ThreadLocalManager {

    /**
     * <p>registerThreadLocal.</p>
     *
     * @param threadLocal a {@link java.lang.ThreadLocal} object.
     */
    void registerThreadLocal(ThreadLocal<?> threadLocal);

    /**
     * <p>unregisterThreadLocal.</p>
     *
     * @param threadLocal a {@link java.lang.ThreadLocal} object.
     */
    void unregisterThreadLocal(ThreadLocal<?> threadLocal);

    /**
     * <p>clearAll.</p>
     */
    void clearAll();

}
