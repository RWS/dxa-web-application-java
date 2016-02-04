package com.sdl.webapp.common.util;

import java.util.Map;

/**
 * <p>Dd4tUtils interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface Dd4tUtils {
    /**
     * <p>getFromNestedMultiLevelMapOrAlternative.</p>
     *
     * @param multiLevelMap a {@link java.util.Map} object.
     * @param key           a {@link java.lang.String} object.
     * @param alternative   a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    Object getFromNestedMultiLevelMapOrAlternative(Map<String, Object> multiLevelMap, String key, Object alternative);
}
