/**
 * Copyright 2016 Rogier Oudshoorn
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dd4t.caching.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.dd4t.caching.CacheElement;
import org.dd4t.caching.Cachable;
import org.dd4t.caching.CacheDependency;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappingCacheProvider implements CacheProvider {
    private static final Logger LOG = LoggerFactory.getLogger(WrappingCacheProvider.class);
	
	@Resource
	private PayloadCacheProvider internalProvider;


    @Override
    public Object loadFromLocalCache (String key) {
        CacheElement<Object> item = internalProvider.loadPayloadFromLocalCache(key);

        if (item.isExpired()) {
            LOG.debug("Not returning expired item");
            return null;
        } else {
            return item.getPayload();
        }
    }

    @Override
    public void storeInCache (String key, Cachable ob, Collection<Cachable> deps) {

        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement);

        for (Cachable item : deps) {
        	internalProvider.addDependency(key, item.getCacheKey());
        }
    }

    @Override
    public void storeInItemCache (String key, Object ob, int dependingPublicationId, int dependingItemId) {
        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependingPublicationId, dependingItemId);
    }

    @Override
    public void storeInComponentPresentationCache (String key, Object ob, int dependingPublicationId, int dependingCompId, int dependingTemplateId) {
        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependingPublicationId, dependingCompId);
    }

    @Override
    public void storeInKeywordCache (String key, Object ob, int dependingPublicationId, int dependingItemId) {
        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependingPublicationId, dependingItemId);
    }

	@Override
	public void storeInItemCache(String key, Object ob,
			List<CacheDependency> dependencies) {

        CacheElement<Object> cacheElement = internalProvider.loadPayloadFromLocalCache(key);
        cacheElement.setPayload(ob);

        internalProvider.storeInItemCache(key, cacheElement, dependencies);
	}   
}
