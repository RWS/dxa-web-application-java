package org.dd4t.core.caching;

/**
 * Wrapper around a 'payload' object that represents the actual object in the cache.
 * The payload is wrapped inside this cache element object,
 * so we can differentiate between null and not-null payloads.
 * The cache element can also contain empty (null) payloads.
 *
 * @author Mihai Cadariu
 */
public interface CacheElement<T> {

	public T getPayload();

	public void setPayload(T payload);

	public boolean isExpired();

	public void setExpired(boolean update);
}
