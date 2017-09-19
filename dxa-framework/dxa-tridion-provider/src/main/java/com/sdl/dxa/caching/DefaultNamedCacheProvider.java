package com.sdl.dxa.caching;

import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.web.client.cache.CacheProviderInitializer;
import com.sdl.web.client.cache.GeneralCacheProvider;
import com.sdl.web.client.configuration.ClientConstants;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import com.sdl.webapp.common.util.InitializationUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import static com.sdl.web.client.configuration.ClientConstants.Cache.DEFAULT_CACHE_URI;
import static java.nio.file.Files.exists;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.expiry.Duration.of;
import static org.ehcache.expiry.Expirations.timeToLiveExpiration;
import static org.ehcache.jsr107.Eh107Configuration.fromEhcacheCacheConfiguration;

@Slf4j
@Component
public class DefaultNamedCacheProvider extends BaseClientConfigurationLoader implements NamedCacheProvider {

    private final ModelServiceConfiguration modelServiceConfiguration;

    @Value("#{'${dxa.caching.disabled.caches}'.split(',\\s?')}")
    private Set<String> disabledCaches;

    @Getter
    private CacheManager cacheManager;

    private Set<String> ownCachesNames = new ConcurrentSkipListSet<>();

    private Map<Triple<String, Class, Class>, Cache> ownCaches = new ConcurrentSkipListMap<>();

    private com.sdl.web.client.cache.CacheProvider cilCacheProvider;

    @Autowired
    public DefaultNamedCacheProvider(ModelServiceConfiguration modelServiceConfiguration) throws ConfigurationException {
        this.modelServiceConfiguration = modelServiceConfiguration;
        this.cilCacheProvider = CacheProviderInitializer.getCacheProvider(getCacheConfiguration());
        this.cacheManager = getCacheManager(
                this.cilCacheProvider instanceof GeneralCacheProvider ?
                        getCacheConfiguration().getProperty(ClientConstants.Cache.CLIENT_CACHE_URI) :
                        InitializationUtils.getConfiguration("dxa.caching.configuration", null));

        //cannot be null because of default value
        //noinspection ConstantConditions
        List<String> requiredCaches = Arrays.asList(
                InitializationUtils.getConfiguration("dxa.caching.required.caches", "").split(",\\s?"));
        cacheManager.getCacheNames().forEach(requiredCaches::remove);
        requiredCaches.forEach(this::getCache);
    }

    @Override
    protected String getServiceUrl() {
        return modelServiceConfiguration.getHealthCheckUrl();
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        log.debug("Trying to get cache name {} for key {} and value {}", cacheName, keyType, valueType);
        Cache<K, V> cache = this.cacheManager.getCache(cacheName, keyType, valueType);
        if (cache == null) {
            log.debug("Cache name {} for key {} and value {} does not exist, auto-creating", cacheName, keyType, valueType);
            cache = cacheManager.createCache(cacheName,
                    fromEhcacheCacheConfiguration(
                            newCacheConfigurationBuilder(keyType, valueType, ResourcePoolsBuilder.heap(10000))
                                    .withExpiry(timeToLiveExpiration(
                                            this.cilCacheProvider.getCacheExpirationPeriod() == null ?
                                                    of(5, TimeUnit.MINUTES) :
                                                    of(this.cilCacheProvider.getCacheExpirationPeriod(), TimeUnit.SECONDS))
                                    )));
        }

        if (!ownCachesNames.contains(cacheName)) {
            Triple<String, Class, Class> triple = Triple.of(cacheName, keyType, valueType);
            if (!ownCaches.containsKey(triple)) {
                ownCaches.put(triple, cache);
                ownCachesNames.add(cacheName);
            }
        }

        return cache;
    }

    @Override
    public Cache<Object, Object> getCache(String cacheName) {
        return getCache(cacheName, Object.class, Object.class);
    }

    @Override
    public Collection<Cache> getOwnCaches() {
        return ownCaches.values();
    }

    @Override
    public boolean isCacheEnabled() {
        return cilCacheProvider.isCacheEnabled();
    }

    @Override
    public boolean isCacheEnabled(String cacheName) {
        return isCacheEnabled() && !disabledCaches.contains(cacheName);
    }

    private CacheManager getCacheManager(String cacheManagerUri) {
        String configUrl = cacheManagerUri;
        if (configUrl == null || configUrl.isEmpty()) {
            log.warn("Config URI for Cache Provider is empty, using default fallback option");
            configUrl = DEFAULT_CACHE_URI;
        }

        CachingProvider cachingProvider = Caching.getCachingProvider(); // NOSONAR
        Path configPath = Paths.get(configUrl);
        if (exists(configPath)) {
            return cachingProvider.getCacheManager(configPath.toUri(), null);
        }

        URL cacheManagerUrl = getClass().getClassLoader().getResource(configUrl);
        if (cacheManagerUrl != null) {
            try {
                return cachingProvider.getCacheManager(cacheManagerUrl.toURI(), null);
            } catch (URISyntaxException e) {
                log.warn("Config URI {} is not syntactically correct, fallback to last default option", cacheManagerUri, e);
            }
        }
        return cachingProvider.getCacheManager();
    }
}
