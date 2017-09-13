package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class LocalizationAwareKeyGenerator implements KeyGenerator {

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public LocalizationAwareCacheKey generate(Object target, Method method, Object... params) {
        return new LocalizationAwareCacheKey(webRequestContext.getLocalization().getId(),
                SimpleKeyGenerator.generateKey(params));
    }

    public LocalizationAwareCacheKey generate(Object... params) {
        return generate(null, null, params);
    }
}
