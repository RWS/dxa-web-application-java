package org.dd4t.core.caching;

/**
 * @author Mihai Cadariu
 * @since 25.07.2014
 */
public interface CacheInvalidator {

    public void flush();

    public void invalidate(String key);
}
