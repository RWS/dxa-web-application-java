package com.sdl.webapp.config;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.DynamicMappingsRetrieverImpl;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;

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
    public static class TridionCacheConfiguration {

        @Value("${dxa.caching.configuration}")
        private String cacheConfigFile;

        @PostConstruct
        public void finish() {
            log.info("Configured DXA cache using {}", cacheConfigFile);
        }

        @Bean
        public CacheManager cacheManager() throws URISyntaxException {
            javax.cache.CacheManager cacheManager = jsr107cacheManager();
            return new JCacheCacheManager(cacheManager);
        }

        @Bean
        public javax.cache.CacheManager jsr107cacheManager() throws URISyntaxException {
            CachingProvider cachingProvider = Caching.getCachingProvider(); //NOSONAR
            URI uri = getClass().getResource(cacheConfigFile).toURI();
            return cachingProvider.getCacheManager(uri, getClass().getClassLoader());
        }
    }
}
