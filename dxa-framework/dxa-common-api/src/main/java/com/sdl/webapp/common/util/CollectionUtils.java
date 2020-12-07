package com.sdl.webapp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Utils class with methods for dealing with DXA-specific collections operations.
 */
@Slf4j
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Gets data from possible infinite map os data with nested maps, or returns a given alternative if nothing found.
     * <p>Usage example: {@code CollectionUtils.getByCompoundKeyOrAlternative("Program/Asset/ValueType", map, "default");}
     *
     * @param keyPath     key path to search looking like a set of keys separated by {@code /} symbol
     * @param map         map to look for the value in
     * @param alternative alternative to return if no value for key is found
     * @param type        expected type for generic code
     * @return a value or alternative if there is no value
     */
    @Contract("_, _, !null, _ -> !null")
    public static <T> T getByCompoundKeyOrAlternative(String keyPath, Map<String, ?> map, T alternative, @NotNull Class<T> type) {
        if (keyPath == null || keyPath.isEmpty()) {
            log.info("Key path is null or empty '{}', alternative '{}'", keyPath, alternative);
            return alternative;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(keyPath.replaceFirst("^/", "").replaceFirst("/$", ""), "/");

        Object value = getByCompoundKeyOrAlternative(stringTokenizer, map);
        if (value == null) {
            log.debug("Found null value for key '{}', alternative '{}'", keyPath, alternative);
            return alternative;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            log.warn("Found value '{}' for key '{}' while the requested type is '{}', returning alternative '{}'", value, keyPath, type, alternative);
            return alternative;
        }
        log.trace("Found value '{}' for key '{}', alternative '{}'", value, keyPath, alternative);
        //cast is type safe because of check for assignable type
        //noinspection unchecked
        return (T) value;
    }

    @Nullable
    private static Object getByCompoundKeyOrAlternative(StringTokenizer key, @Nullable Map<String, ?> map) {
        String token = key.nextToken();
        if (map == null || !map.containsKey(token)) {
            return null;
        }
        Object value = map.get(token);
        if (value == null) {
            return null;
        }
        if (!key.hasMoreTokens()) {
            return value;
        }

        //noinspection unchecked
        return value instanceof Map ? getByCompoundKeyOrAlternative(key, (Map<String, ?>) value) : null;
    }

}
