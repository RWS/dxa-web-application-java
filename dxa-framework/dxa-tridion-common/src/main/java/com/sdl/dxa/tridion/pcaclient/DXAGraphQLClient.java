package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.client.cache.CacheProvider;
import com.sdl.web.client.cache.CacheProviderInitializer;
import com.sdl.web.client.configuration.api.ConfigurationException;
import com.sdl.web.content.client.util.ClientCacheKeyEnhancer;
import com.sdl.web.pca.client.DefaultGraphQLClient;
import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.web.pca.client.exception.GraphQLClientException;
import com.sdl.web.pca.client.exception.UnauthorizedException;
import com.sdl.webapp.common.util.ApplicationContextHolder;

import com.tridion.ambientdata.web.WebContext;

import org.slf4j.Logger;

import javax.cache.Cache;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.slf4j.LoggerFactory.getLogger;

public class DXAGraphQLClient extends DefaultGraphQLClient {

    private static final Logger LOG = getLogger(DXAGraphQLClient.class);

    private static final AtomicLong HIT_CACHE = new AtomicLong();
    private static final AtomicLong MISS_CACHE = new AtomicLong();

    private GraphQlServiceConfigurationLoader configurationLoader;
    private CacheProvider cacheProvider;
    private Cache<String, Serializable> queryCache;

    public DXAGraphQLClient(String endpoint, Map<String, String> defaultHeaders) {
        super(endpoint, defaultHeaders);
        initializeCacheProvider();
    }

    public DXAGraphQLClient(String endpoint, Map<String, String> defaultHeaders, Authentication auth) {
        super(endpoint, defaultHeaders, auth);
        initializeCacheProvider();
    }

    private void initializeCacheProvider() {
        if (ApplicationContextHolder.getContext() == null) {
            LOG.warn("The application context is not yet available. No caching for now.");
            return;
        }

        try {
            configurationLoader = ApplicationContextHolder.getContext().getBean(GraphQlServiceConfigurationLoader.class);
            this.cacheProvider = CacheProviderInitializer.getCacheProvider(configurationLoader.getCacheConfiguration());
            if (isCacheEnabled()) {
                this.queryCache = this.cacheProvider.provideCacheForClass(String.class, Serializable.class);
            }
        }
        catch (ConfigurationException e) {
            LOG.error("Failed to initiate cache provider", e);
        }
    }

    private boolean isCacheEnabled() {
        if (configurationLoader == null)
            initializeCacheProvider();
        return this.cacheProvider != null && this.cacheProvider.isCacheEnabled();
    }

    private String createCacheKey(String query) {
        if (WebContext.getCurrentClaimStore() != null) {
            LOG.debug("ClaimStore is available, so utilizing the ClientCacheKeyEnhancer");
            return (new ClientCacheKeyEnhancer(query)).addUserConditions().compile();
        }
        LOG.debug("There is no ClaimStore, so utilizing the query itself as key");
        return UUID.nameUUIDFromBytes(query.getBytes()).toString();
    }

    private Serializable getFromCache(Cache<String, Serializable> cache, String cacheKey) {
        LOG.debug("Cache is enabled, trying to get the cached response from cache, Hit/miss:" + HIT_CACHE.get() + "/" + MISS_CACHE.get() + ", key:" + cacheKey);
        Serializable cachedResponse = cache.get(cacheKey);
        if (cachedResponse != null) {
            HIT_CACHE.incrementAndGet();
            return cachedResponse;
        }
        else {
            LOG.debug("Entity ({}) is not found in cache", cacheKey);
            MISS_CACHE.incrementAndGet();
            return null;
        }
    }

    @Override
    public String execute(String queryJsonEntity, int timeout) throws UnauthorizedException, GraphQLClientException {
        if (!isCacheEnabled()) {
            return super.execute(queryJsonEntity, timeout);
        }
        else {
            LOG.debug("Cache is enabled, trying to get response from cache");
            String cacheKey = createCacheKey(queryJsonEntity);
            String response = (String)this.getFromCache(this.queryCache, cacheKey);
            if (response == null) {
                LOG.debug("No such query in cache, getting from content service");
                response = super.execute(queryJsonEntity, timeout);
                if (response != null) {
                    this.queryCache.put(cacheKey, response);
                }
            }
            return response;
        }
    }
}
