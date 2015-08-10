package com.sdl.webapp.common.api;

/**
 * ThreadLocal Manager.
 * Manage all registered thread local variables and clear them after each request.
 *
 * @author nic
 */
public interface ThreadLocalManager {

    void registerThreadLocal(ThreadLocal<?> threadLocal);

    void unregisterThreadLocal(ThreadLocal<?> threadLocal);

    void clearAll();

}
