package com.sdl.webapp.common.util;

import java.util.Map;

@FunctionalInterface
public interface Dd4tUtils {

    Object getFromNestedMultiLevelMapOrAlternative(Map<String, Object> multiLevelMap, String key, Object alternative);
}
