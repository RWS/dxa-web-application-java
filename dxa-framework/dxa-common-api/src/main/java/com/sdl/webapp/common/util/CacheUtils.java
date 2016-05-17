package com.sdl.webapp.common.util;

import java.util.Map;

public final class CacheUtils {

    private CacheUtils() {
    }

    /**
     * Saves the given value to a map with a given key and returns the value without change.
     *
     * @param map   map to store key-value
     * @param key   key for the value
     * @param value value to memorize
     * @param <K>   type of the key
     * @param <T>   type of the value
     * @return the value with no changes
     */
    public static <K, T> T memorize(Map<K, T> map, K key, T value) {
        map.put(key, value);
        return value;
    }
}
