package com.sdl.webapp.config;

import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.DynamicMappingsRetrieverImpl;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.expiry.Duration.of;
import static org.ehcache.expiry.Expirations.timeToLiveExpiration;
import static org.ehcache.jsr107.Eh107Configuration.fromEhcacheCacheConfiguration;

@ComponentScan({"com.sdl.webapp.tridion", "com.sdl.dxa.tridion"})
@Configuration
public class TridionSpringConfiguration {

    @Bean
    public DynamicMetaRetriever dynamicMetaRetriever() {
        return new DynamicMetaRetriever();
    }

    @Bean
    public BinaryContentRetriever binaryContentRetriever() {
        return new BinaryContentRetrieverImpl();
    }

    @Bean
    public DynamicMappingsRetriever dynamicMappingsRetriever() {
        return new DynamicMappingsRetrieverImpl();
    }

    @Configuration
    @EnableCaching
    @Profile("!dxa.no-cache")
    @Slf4j
    public static class TridionCacheConfiguration extends CachingConfigurerSupport {

        @Value("${dxa.caching.configuration}")
        private String cacheConfigFile;

        @Value("#{'${dxa.caching.required.caches}'.split(',\\s?')}")
        private Set<String> requiredCaches;

        @Value("#{'${dxa.caching.disabled.caches}'.split(',\\s?')}")
        private Set<String> disabledCaches;

        @PostConstruct
        public void finish() {
            log.info("Configured DXA cache using {}", cacheConfigFile);
        }

        @Bean
        @Override
        public CacheManager cacheManager() {
            CompositeCacheManager compositeCacheManager = new CompositeCacheManager(
                    new JCacheCacheManager(jsr107cacheManager()));
            compositeCacheManager.setFallbackToNoOpCache(true);
            return compositeCacheManager;
        }

        @Bean
        @Override
        public KeyGenerator keyGenerator() {
            return localizationAwareKeyGenerator();
        }

        @Bean
        public LocalizationAwareKeyGenerator localizationAwareKeyGenerator() {
            return new LocalizationAwareKeyGenerator();
        }

        @Bean
        public javax.cache.CacheManager jsr107cacheManager() {
            URI configUri = null;
            try {
                configUri = getClass().getResource(cacheConfigFile).toURI();
            } catch (URISyntaxException e) {
                log.warn("Cannot read caching configuration {}, using default caching configuration", cacheConfigFile, e);
            }

            CachingProvider cachingProvider = Caching.getCachingProvider(); //NOSONAR
            javax.cache.CacheManager cacheManager = configUri == null ?
                    cachingProvider.getCacheManager() :
                    cachingProvider.getCacheManager(configUri, getClass().getClassLoader());

            requiredCaches.stream()
                    .filter(cacheName -> !disabledCaches.contains(cacheName) && cacheManager.getCache(cacheName) == null)
                    .peek(cacheName -> log.trace("Created missing but required cache: {}", cacheName))
                    .forEach(cacheName -> cacheManager.createCache(cacheName,
                            fromEhcacheCacheConfiguration(newCacheConfigurationBuilder(Object.class, Object.class,
                                    ResourcePoolsBuilder.heap(1000)).withExpiry(timeToLiveExpiration(of(3600, SECONDS))))));

            disabledCaches.stream()
                    .peek(cacheName -> log.trace("Destroyed cache (NoOp Cache will be used): {}", cacheName))
                    .forEach(cacheManager::destroyCache);

            return cacheManager;
        }
    }
}
