package com.sdl.dxa.tridion.caching;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class LocalizationAwareKeyGenerator implements KeyGenerator {

    private final WebRequestContext webRequestContext;

    @Autowired
    public LocalizationAwareKeyGenerator(WebRequestContext webRequestContext) {
        this.webRequestContext = webRequestContext;
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return SimpleKeyGenerator.generateKey(Lists.asList(webRequestContext.getLocalization().getId(), params).toArray(new Object[params.length + 1]));
    }

    public Object generate(Object... params) {
        return generate(null, null, params);
    }
}
