package com.sdl.dxa.caching.statistics;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.ehcache.sizeof.SizeOf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Getter
@Slf4j
public class CacheStatisticsProvider {

    private static final String SHALLOW_SIZE = "largestCachedObject-ShallowSize-InBytes";
    private static final String DEEP_SIZE = "largestCachedObject-DeepSize-InBytes";

    @Value("${dxa.caching.statistics.enabled:#{false}}")
    private boolean enabled;

    private final ConcurrentMap<String, Map<String, Long>> mapper = new ConcurrentHashMap<>();

    public void storeStatsInfo(String cacheName, Object cachedObject) {
        if (!enabled)
            return;

        SizeOf sizeOf = SizeOf.newInstance();
        try {
            long shallowSize = sizeOf.sizeOf(cachedObject);
            long deepSize = sizeOf.deepSizeOf(cachedObject);

            Map<String, Long> entry = getStatistics(cacheName);
            if (entry == null) {
                entry = new HashMap<>();
                entry.put(SHALLOW_SIZE, shallowSize);
                entry.put(DEEP_SIZE, deepSize);
                mapper.put(cacheName, entry);
            } else {
                // We only want to keep the latest largest record
                boolean update = false;
                long mappedShallowSize = entry.get(SHALLOW_SIZE);
                if (shallowSize > mappedShallowSize) {
                    entry.put(SHALLOW_SIZE, shallowSize);
                    update = true;
                }
                long mappedDeepSize = entry.get(DEEP_SIZE);
                if (deepSize > mappedDeepSize) {
                    entry.put(DEEP_SIZE, deepSize);
                    update = true;
                }
                if (update) {
                    mapper.put(cacheName, entry);
                }
            }
        }
        catch(Exception exception) {
            log.warn(String.format("There has been a problem fetching the size of the EhCache object [%s]",
                    exception.getMessage()));
        }
    }

    public Map<String, Long> getStatistics(String cacheName) {
        return mapper.get(cacheName);
    }

    public Map<String, Map<String, Long>> getAllStatistics() {
        return mapper;
    }
}
