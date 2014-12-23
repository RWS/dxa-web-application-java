package org.dd4t.core.caching.impl;

import org.dd4t.core.caching.CacheElement;

/**
 * Wrapper around a 'payload' object that represents the actual object in the cache.
 * The payload is wrapped inside this cache element object,
 * so we can differentiate between null and not-null payloads.
 * The cache element can also contain empty (null) payloads.
 *
 * @author Mihai Cadariu
 */
public class CacheElementImpl<T> implements CacheElement<T> {

    private boolean isExpired;
    private T payload;

    public CacheElementImpl(T payload) {
        this(payload, false);
    }

    public CacheElementImpl(T payload, boolean isExpired) {
        this.payload = payload;
        this.isExpired = isExpired;
    }

    @Override
    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public void setExpired(boolean expired) {
        this.isExpired = expired;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public void setPayload(T payload) {
        this.payload = payload;
    }
}
