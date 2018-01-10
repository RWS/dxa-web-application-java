package com.sdl.dxa.caching;

/**
 * Functional interface that can provide ID of the current localization.
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface LocalizationIdProvider {

    /**
     * Return current localization ID. By intention, is only used in caching mechanism. I
     * f localization ID is not important for the current implementation, may return a dummy value.
     * Note thought that if value is not specified and there are multiple publications served by the application,
     * then this may lead to cache key collision.
     *
     * @return current localization ID
     * @dxa.publicApi
     */
    String getId();
}
