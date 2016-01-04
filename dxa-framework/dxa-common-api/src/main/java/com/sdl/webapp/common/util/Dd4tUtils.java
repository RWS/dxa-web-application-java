package com.sdl.webapp.common.util;

import java.util.Map;

public interface Dd4tUtils {
    Object getFromNestedMultiLevelMapOrAlternative(Map<String, Object> multiLevelMap, String key, Object alternative);
}
