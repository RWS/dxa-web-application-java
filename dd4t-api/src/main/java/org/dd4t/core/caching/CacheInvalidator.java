package org.dd4t.core.caching;

/**
 * @author Mihai Cadariu
 */
public interface CacheInvalidator {

    public void flush();

    public void invalidate(String key);
}
