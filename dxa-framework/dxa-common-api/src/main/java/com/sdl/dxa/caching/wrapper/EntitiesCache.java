package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.webapp.common.api.model.EntityModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.CacheManager;

@Component
public class EntitiesCache extends SimpleCacheWrapper<EntityModel> {

    private Cache<Object, EntityModel> cache;

    private CacheManager cacheManager;

    public EntitiesCache(LocalizationAwareKeyGenerator keyGenerator) {
        super(keyGenerator);
    }

    @Autowired(required = false) // cannot autowire in constructor because CacheManager may not exist
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        cache = cacheManager == null ? null : cacheManager.getCache("entities");
    }

    @Override
    public Cache<Object, EntityModel> getCache() {
        return this.cache;
    }

    public Object getKey(EntityModelData entityModelData) {
        return getKey(entityModelData.getId(), entityModelData.getSchemaId(), entityModelData.getMvcData());
    }
}
