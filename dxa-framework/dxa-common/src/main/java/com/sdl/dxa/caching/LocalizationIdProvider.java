package com.sdl.dxa.caching;

/**
 * Functional interface that can provide ID of the current localization.
 */
@FunctionalInterface
public interface LocalizationIdProvider {

    String getId();
}
