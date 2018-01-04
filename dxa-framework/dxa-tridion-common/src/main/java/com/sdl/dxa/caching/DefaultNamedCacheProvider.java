package com.sdl.dxa.caching;

import com.sdl.web.client.cache.CacheProviderInitializer;
import com.sdl.web.client.cache.GeneralCacheProvider;
import com.sdl.web.client.configuration.ClientConstants;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.content.client.configuration.impl.BaseClientConfigurationLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.xml.XmlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
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

/**
 * Default implementation of DXA cache.
 *
 * @dxa.publicApi
 */
@Slf4j
@Component
public class DefaultNamedCacheProvider extends BaseClientConfigurationLoader implements NamedCacheProvider {

    @Value("#{'${dxa.caching.disabled.caches}'.split(',\\s?')}")
    private Set<String> disabledCaches;

    @Value("#{'${dxa.caching.required.caches}'.split(',\\s?')}")
    private Set<String> requiredCaches;

    @Value("${dxa.caching.configuration:#{null}}")
    private String cachingConfigurationFile;

    private boolean isCilConfigUsed;

    @Getter
    private CacheManager cacheManager;

    private Set<String> ownCachesNames = new ConcurrentSkipListSet<>();

    private Map<Triple<String, Class, Class>, Cache> ownCaches = new ConcurrentSkipListMap<>();

    private com.sdl.web.client.cache.CacheProvider cilCacheProvider;

    public DefaultNamedCacheProvider() throws ConfigurationException {
        // empty
    }

    @PostConstruct
    public void init() throws ConfigurationException {
        this.cilCacheProvider = CacheProviderInitializer.getCacheProvider(getCacheConfiguration());

        boolean cilUsesGeneralCache = this.cilCacheProvider instanceof GeneralCacheProvider;
        this.isCilConfigUsed = cilUsesGeneralCache;
        String cacheConfigurationUri = cilUsesGeneralCache ?
                getCacheConfiguration().getProperty(ClientConstants.Cache.CLIENT_CACHE_URI) :
                cachingConfigurationFile;
        log.info("Using cache config {}, CIL uses GeneralCacheProvider: {}", cacheConfigurationUri, cilUsesGeneralCache);
        this.cacheManager = getCacheManager(cacheConfigurationUri);

        //cannot be null because of default value
        //noinspection ConstantConditions
        cacheManager.getCacheNames().forEach(requiredCaches::remove);
        log.info("Required caches not yet created: '{}', creating them", requiredCaches);
        requiredCaches.forEach(this::getCache);
    }

    @Override
    protected String getServiceUrl() {
        return "";
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        log.debug("Trying to get cache name {} for key {} and value {}", cacheName, keyType, valueType);
        Cache<K, V> cache = this.cacheManager.getCache(cacheName, keyType, valueType);
        if (cache == null) {
            log.debug("Cache name {} for key {} and value {} does not exist, auto-creating", cacheName, keyType, valueType);
            cache = cacheManager.createCache(cacheName, buildDefaultCacheConfiguration(keyType, valueType));
        }

        if (!ownCachesNames.contains(cacheName)) {
            Triple<String, Class, Class> triple = Triple.of(cacheName, keyType, valueType);
            if (!ownCaches.containsKey(triple)) {
                ownCaches.put(triple, cache);
                ownCachesNames.add(cacheName);
                log.debug("Added cache {} to own caches of DXA", cacheName);
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

    @NotNull
    private <K, V> javax.cache.configuration.Configuration<K, V> buildDefaultCacheConfiguration(Class<K> keyType, Class<V> valueType) {
        return fromEhcacheCacheConfiguration(isCilConfigUsed ?
                buildDefaultCilCacheConfiguration(keyType, valueType) :
                buildDefaultConfigCacheConfiguration(keyType, valueType));
    }

    @NotNull
    private <V, K> CacheConfigurationBuilder<K, V> buildDefaultConfigCacheConfiguration(Class<K> keyType, Class<V> valueType) {
        try {
            return buildConfigCacheConfiguration(cachingConfigurationFile, keyType, valueType);
        } catch (ConfigurationException | MalformedURLException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            log.warn("Exception happened when creating cache, using fallback configuration", e);
            return buildFallbackCacheConfiguration(keyType, valueType);
        }
    }

    @NotNull
    private <K, V> CacheConfigurationBuilder<K, V> buildDefaultCilCacheConfiguration(Class<K> keyType, Class<V> valueType) {
        CacheConfigurationBuilder<K, V> configurationBuilder;
        try {
            configurationBuilder = buildConfigCacheConfiguration(
                    getCacheConfiguration().getProperty(ClientConstants.Cache.CLIENT_CACHE_URI), keyType, valueType);
        } catch (ConfigurationException | IllegalAccessException | InstantiationException | ClassNotFoundException | MalformedURLException e) {
            log.warn("Cannot create a default CIL cache configuration, fallback to default configuration", e);
            configurationBuilder = buildDefaultConfigCacheConfiguration(keyType, valueType);
        }

        Integer cacheExpirationPeriod = this.cilCacheProvider.getCacheExpirationPeriod();
        Duration timeToLive;
        if (cacheExpirationPeriod != null) {
            timeToLive = of(cacheExpirationPeriod, TimeUnit.SECONDS);
        } else {
            log.warn("Cache Expiration Period is not set, fallback to 5 minutes, set it in cd_client_conf.xml");
            timeToLive = of(5, TimeUnit.MINUTES);
        }

        return configurationBuilder.withExpiry(timeToLiveExpiration(timeToLive));
    }

    @NotNull
    private <V, K> CacheConfigurationBuilder<K, V> buildConfigCacheConfiguration(String config, Class<K> keyType, Class<V> valueType)
            throws MalformedURLException, IllegalAccessException, ClassNotFoundException, InstantiationException, ConfigurationException {
        URI configUri = getConfigUri(config);
        if (configUri == null) {
            throw new MalformedURLException("Cannot load because it doesn't exist: " + config);
        }

        CacheConfigurationBuilder<K, V> cacheConfigurationBuilder = new XmlConfiguration(configUri.toURL()).newCacheConfigurationBuilderFromTemplate("default", keyType, valueType);
        if (cacheConfigurationBuilder == null) {
            throw new ConfigurationException("Cannot load 'default' template from " + config);
        }
        return cacheConfigurationBuilder;
    }

    private <K, V> CacheConfigurationBuilder<K, V> buildFallbackCacheConfiguration(Class<K> keyType, Class<V> valueType) {
        return newCacheConfigurationBuilder(keyType, valueType, ResourcePoolsBuilder.heap(10000))
                .withExpiry(timeToLiveExpiration(of(5, TimeUnit.MINUTES)));
    }

    private CacheManager getCacheManager(String cacheManagerUri) {
        CachingProvider cachingProvider = Caching.getCachingProvider(); // NOSONAR
        URI configUri = getConfigUri(cacheManagerUri);
        return configUri != null ?
                cachingProvider.getCacheManager(configUri, null) :
                cachingProvider.getCacheManager();
    }

    @Nullable
    private URI getConfigUri(String cacheManagerUri) {
        String configUrl = cacheManagerUri;
        if (configUrl == null || configUrl.isEmpty()) {
            log.warn("Config URI for Cache Provider is empty, using default fallback option");
            configUrl = DEFAULT_CACHE_URI;
        }

        Path configPath = Paths.get(configUrl);
        if (exists(configPath)) {
            return configPath.toUri();
        } else {
            URL resource = getClass().getClassLoader().getResource(configUrl);
            if (resource != null) {
                try {
                    return resource.toURI();
                } catch (URISyntaxException e) {
                    log.warn("Config URI {} is not syntactically correct, fallback to last default option", cacheManagerUri, e);
                }
            }
        }

        log.warn("Cannot find EhCache config {}", cacheManagerUri);
        return null;
    }
}
