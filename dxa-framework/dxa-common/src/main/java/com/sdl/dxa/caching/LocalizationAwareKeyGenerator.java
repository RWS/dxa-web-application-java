package com.sdl.dxa.caching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Default implementation of localization-aware cache key generator.
 *
 * @dxa.publicApi
 */
@Component
public class LocalizationAwareKeyGenerator implements KeyGenerator {

    @Autowired
    private LocalizationIdProvider localizationIdProvider;

    @Override
    public LocalizationAwareCacheKey generate(Object target, Method method, Object... params) {
        Object key = SimpleKeyGenerator.generateKey(params);
        return new LocalizationAwareCacheKey(localizationIdProvider.getId(), params.length == 1 ? new SimpleKey(key) : (Serializable) key);
    }

    public LocalizationAwareCacheKey generate(Object... params) {
        return generate(null, null, params);
    }
}
