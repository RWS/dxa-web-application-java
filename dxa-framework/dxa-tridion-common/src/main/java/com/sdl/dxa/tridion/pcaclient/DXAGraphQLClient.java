package com.sdl.dxa.tridion.pcaclient;

import com.sdl.dxa.caching.NamedCacheProvider;
import com.sdl.dxa.caching.statistics.CacheStatisticsProvider;
import com.sdl.web.content.client.util.ClientCacheKeyEnhancer;
import com.sdl.web.pca.client.DefaultGraphQLClient;
import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.web.pca.client.exception.GraphQLClientException;
import com.sdl.web.pca.client.exception.UnauthorizedException;

import com.tridion.ambientdata.web.WebContext;

import org.slf4j.Logger;

import javax.cache.Cache;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.slf4j.LoggerFactory.getLogger;

public class DXAGraphQLClient extends DefaultGraphQLClient {

    private static final Logger LOG = getLogger(DXAGraphQLClient.class);

    private static final AtomicLong HIT_CACHE = new AtomicLong();
    private static final AtomicLong MISS_CACHE = new AtomicLong();

    private Cache<Object, Object> queryCache;
    private NamedCacheProvider namedCacheProvider;
    private CacheStatisticsProvider cacheStatisticsProvider;

    public DXAGraphQLClient(String endpoint, Map<String, String> defaultHeaders,
                            NamedCacheProvider namedCacheProvider, CacheStatisticsProvider cacheStatisticsProvider) {
        super(endpoint, defaultHeaders);
        this.namedCacheProvider = namedCacheProvider;
        this.queryCache = namedCacheProvider.getCache("queryCache");
        this.cacheStatisticsProvider = cacheStatisticsProvider;
    }

    public DXAGraphQLClient(String endpoint, Map<String, String> defaultHeaders, Authentication auth,
                            NamedCacheProvider namedCacheProvider, CacheStatisticsProvider cacheStatisticsProvider) {
        super(endpoint, defaultHeaders, auth);
        this.namedCacheProvider = namedCacheProvider;
        this.queryCache = namedCacheProvider.getCache("queryCache");
        this.cacheStatisticsProvider = cacheStatisticsProvider;
    }

    private boolean isCacheEnabled() {
        return namedCacheProvider.isCacheEnabled("queryCache");
    }

    private String createCacheKey(String query) {
        if (WebContext.getCurrentClaimStore() != null) {
            LOG.debug("ClaimStore is available, so utilizing the ClientCacheKeyEnhancer");
            return (new ClientCacheKeyEnhancer(query)).addUserConditions().compile();
        }
        LOG.debug("There is no ClaimStore, so utilizing the query itself as key");
        return UUID.nameUUIDFromBytes(query.getBytes()).toString();
    }

    private String getFromCache(Cache<Object, Object> cache, String cacheKey) {
        LOG.debug("Cache is enabled, trying to get the cached response from cache, Hit/miss:" + HIT_CACHE.get() + "/" + MISS_CACHE.get() + ", key:" + cacheKey);
        String cachedResponse = (String)cache.get(cacheKey);
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
            String response = this.getFromCache(this.queryCache, cacheKey);
            if (response == null) {
                LOG.debug("No such query in cache, getting from content service");
                response = super.execute(queryJsonEntity, timeout);
                if (response != null) {
                    this.queryCache.put(cacheKey, response);
                    if (this.cacheStatisticsProvider != null) {
                        this.cacheStatisticsProvider.storeStatsInfo("queryCache", response);
                    }
                }
            }
            return response;
        }
    }
}
