package com.sdl.webapp.common.util;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NestedCustomMap {
    private Map<String, Object> map;
    private Function<Map.Entry<String, Map<String, Object>>, Map<String, Object>> loadNestedFunction;

    public NestedCustomMap(Map<String, Object> map,
                           Function<Map.Entry<String, Map<String, Object>>, Map<String, Object>> function) {
        this.map = map;
        this.loadNestedFunction = function;
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
        Map<String, Object> currentMap = map;
        while (iterator.hasNext()) {
            String current = iterator.next();
            if (!iterator.hasNext()) { //last element
                return currentMap.get(current);
            }

            currentMap = loadNestedFunction.apply(Maps.immutableEntry(current, currentMap));
            if (currentMap == null) {
                return null;
            }
        }

        return null;
    }

}
