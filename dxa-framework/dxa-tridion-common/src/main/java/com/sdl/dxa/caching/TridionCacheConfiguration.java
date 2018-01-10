package com.sdl.dxa.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.Collectors;

@Configuration
@EnableCaching
@Profile("!dxa.no-cache")
@Slf4j
public class TridionCacheConfiguration extends CachingConfigurerSupport {

    private final LocalizationAwareKeyGenerator localizationAwareKeyGenerator;

    private final NamedCacheProvider defaultCacheProvider;

    @Autowired
    public TridionCacheConfiguration(LocalizationAwareKeyGenerator localizationAwareKeyGenerator,
                                     NamedCacheProvider defaultCacheProvider) {
        this.localizationAwareKeyGenerator = localizationAwareKeyGenerator;
        this.defaultCacheProvider = defaultCacheProvider;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(
                new SpringJCacheManagerAdapter(defaultCacheProvider));
        compositeCacheManager.setFallbackToNoOpCache(true);
        return compositeCacheManager;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return localizationAwareKeyGenerator;
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return context -> context.getOperation().getCacheNames().stream()
                .map(name -> cacheManager().getCache(name))
                .peek(cache -> log.trace("Resolved cache {} which is a {} cache", cache.getName(), cache.getClass()))
                .collect(Collectors.toList());
    }
}
