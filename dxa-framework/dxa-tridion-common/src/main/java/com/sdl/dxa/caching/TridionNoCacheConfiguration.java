package com.sdl.dxa.caching;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dxa.no-cache")
public class TridionNoCacheConfiguration extends CachingConfigurerSupport {

    @Bean(name="compositeCacheManager")
    @Override
    public CacheManager cacheManager() {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setFallbackToNoOpCache(true);
        return compositeCacheManager;
    }

}
