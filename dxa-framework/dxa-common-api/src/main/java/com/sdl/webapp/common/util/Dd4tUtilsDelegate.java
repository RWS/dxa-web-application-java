package com.sdl.webapp.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Dd4tUtilsDelegate implements Dd4tUtils {
    @Autowired
    private Dd4tUtils dd4tUtils;

    @Override
    public Object getFromNestedMultiLevelMapOrAlternative(Map<String, Object> multiLevelMap, String key, Object alternative) {
        return dd4tUtils.getFromNestedMultiLevelMapOrAlternative(multiLevelMap, key, alternative);
    }
}
