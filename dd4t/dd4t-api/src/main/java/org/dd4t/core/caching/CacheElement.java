package org.dd4t.core.caching;

/**
 * Wrapper around a 'payload' object that represents the actual object in the cache.
 * The payload is wrapped inside this cache element object,
 * so we can differentiate between null and not-null payloads.
 * The cache element can also contain empty (null) payloads.
 *
 * @author Mihai Cadariu
 * @since 28.08.2014
 */
public interface CacheElement<T> {

    T getPayload();

    void setPayload(T payload);

    boolean isExpired();

    void setExpired(boolean update);
}
