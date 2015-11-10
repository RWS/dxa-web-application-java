package com.sdl.webapp.common.util;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NestedStringMap {
    private Map<String, Object> map;

    public NestedStringMap(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Object get(String key) {
        if (key == null || CollectionUtils.isEmpty(map)) {
            return null;
        }

        final List<String> keys = Arrays.asList(key.split("/"));
        final Iterator<String> iterator = keys.iterator();
        Map currentMap = map;
        while (iterator.hasNext()) {
            String current = iterator.next();
            if (!iterator.hasNext()) { //last element
                return currentMap.get(current);
            }

            currentMap = MapUtils.getMap(currentMap, current);
            if (currentMap == null) {
                return null;
            }
        }

        return null;
    }
}
