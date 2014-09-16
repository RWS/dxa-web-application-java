package org.dd4t.providers.impl;
/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


import java.text.ParseException;
import java.util.Collection;

import org.dd4t.core.caching.Cachable;
import org.dd4t.core.caching.CacheAgent;
import org.dd4t.providers.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.cache.Cache;
import com.tridion.cache.CacheFactory;
import com.tridion.cache.KeyGenerator;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.storage.ItemMeta;
import com.tridion.taxonomies.Keyword;
import com.tridion.util.TCMURI;

/**
 * Simple static agent to wrap around the default Tridion Dependency Cache. Note
 * that using this Cache is actually unsupported behavior, and after every
 * Tridion Upgrade this class MUST be validated.
 * 
 * This class can only cache items that implement the Cachable Interface. This
 * interfaces defines base methods in which this agent stores its caching
 * references inside of the items. This enables the agent to store dependencies
 * to all items it caches.
 * 
 * 
 * @author Rogier Oudshoorn (Capgemini), Quirijn Slings (SDL)
 * 
 */
@SuppressWarnings("deprecation")
public class BrokerCacheProvider implements CacheAgent, CacheProvider {


	private static Logger logger = LoggerFactory.getLogger(BrokerCacheProvider.class);

	// Tridion's own cache region for component presentation
	@Deprecated
	public static final String COMPPRES_CACHE_REGION = "com.tridion.ComponentPresentation";

	// Tridion's own cache region for page meta
	@Deprecated
	public static final String PAGEMETA_CACHE_REGION = "com.tridion.broker.pages.meta.PageMeta";

	// Tridion's own cache region for component meta
	@Deprecated
	public static final String COMPONENTMETA_CACHE_REGION = "com.tridion.broker.pages.meta.ComponentMeta";

	// Cache Region used to store our cached items
	public static final String CUSTOM_CACHE_REGION = "org.dd4t.Generic";

	// Cache region for keywords
	public static final String KEYWORD_CACHE_REGION = "com.tridion.broker.Taxonomies.Taxonomy";
	
	// Cache region for items (covers o.a. pageMeta)
	public static final String ITEM_CACHE_REGION = "com.tridion.storage.ItemMeta";
	
	/*
	 * Retrieve item from the cache for given key.
	 */
	public Object loadFromLocalCache(String key) {
		CacheFactory cacheFactory = CacheFactory.getInstance();
		return cacheFactory.getCache(CUSTOM_CACHE_REGION).get(key);
	}

	/*
	 * Store given item in the cache with a reference to supplied Tridion
	 * Component Presentation.
	 */
	public void storeInCache(String key, Object ob,
			ComponentPresentation compPres) {
		storeInComponentPresentationCache(key, ob, compPres.getPublicationId(), compPres.getComponentId(), compPres.getComponentTemplateId());
	}

	/*
	 * Store given item in the cache with a reference to supplied Tridion STORAGE
	 * PageMeta.
	 */
	public void storeInCache(String key, Object ob, com.tridion.storage.PageMeta pageMeta) {
		storeInItemCache(key, ob, pageMeta.getPublicationId(), pageMeta.getItemId());
	}

	/*
	 * Store given item in the cache with a reference to given collection of
	 * also cached items
	 */
	public void storeInCache(String key, Cachable ob, Collection<Cachable> deps) {
		// get reference to cache singleton
		Cache cache = CacheFactory.getInstance().getCache(CUSTOM_CACHE_REGION);

		// store item
		cache.put(key, ob);

		// notify item it has been cached
		ob.notifyCached(key, CUSTOM_CACHE_REGION);

		if (deps != null) {
			// loop over all given dependency items
			for (Cachable dep : deps) {
				// add dependency to the item
				cache.addDependency(key, dep.getCacheRealm(), dep.getCacheKey());
			}
		} else {
			logger.warn("storeInCache: Collection of cachable dependencies is null");
		}
	}

	/*
	 * Store given item in the cache with a reference to supplied Tridion STORAGE
	 * ComponentMeta.
	 */
	@Override
	public void storeInCache(String key, Object ob, com.tridion.storage.ComponentMeta componentMeta) {
		storeInItemCache(key, ob, componentMeta.getPublicationId(), componentMeta.getItemId());
	}

    @Override
    public void storeInCache(String key, Object ob, ItemMeta item) {
		storeInItemCache(key, ob, item.getPublicationId(), item.getItemId());        
    }

    @Override
    public void storeInCache(String key, Object ob, Keyword keyword) {
        
        TCMURI uri;
        try {
            uri = new TCMURI(keyword.getTaxonomyURI());           
            storeInKeywordCache(key, ob, uri.getPublicationId(), uri.getItemId());
        
        } catch (ParseException e) {
            logger.error("unable to parse URI: "+e.getMessage(), e);
        }        
    }

	@Override
	public void storeInItemCache(String key, Object ob, int dependingPublicationId,
			int dependingItemId) {
        // get reference to cache singleton
        Cache cache = CacheFactory.getInstance().getCache(CUSTOM_CACHE_REGION);

        // store item
        cache.put(key, ob);

        // if we are given an Cachable object
        if (ob instanceof Cachable) {
                // cast object
                Cachable cachable = (Cachable) ob;
                // notify item it has been cached
                cachable.notifyCached(key, CUSTOM_CACHE_REGION);
        }

        // add dependency to source item
        cache.addDependency(key, ITEM_CACHE_REGION,
                        KeyGenerator.createKey(dependingPublicationId, dependingItemId));        		
	}

	@Override
	public void storeInComponentPresentationCache(String key, Object ob,
			int dependingPublicationId, int dependingItemId, int dependingTemplateId) {
        // get reference to cache singleton
        Cache cache = CacheFactory.getInstance().getCache(CUSTOM_CACHE_REGION);

        // store item
        cache.put(key, ob);

        // if we are given an Cachable object
        if (ob instanceof Cachable) {
                // cast object
                Cachable cachable = (Cachable) ob;
                // notify item it has been cached
                cachable.notifyCached(key, CUSTOM_CACHE_REGION);
        }

        // add dependency to source item
        cache.addDependency(key, ITEM_CACHE_REGION,
                        KeyGenerator.createKey(dependingPublicationId, dependingItemId, dependingTemplateId));        
	}

	@Override
	public void storeInKeywordCache(String key, Object ob,
			int dependingPublicationId, int dependingItemId) {
        // get reference to cache singleton
        Cache cache = CacheFactory.getInstance().getCache(CUSTOM_CACHE_REGION);

        // store item
        cache.put(key, ob);

        // if we are given an Cachable object
        if (ob instanceof Cachable) {
                // cast object
                Cachable cachable = (Cachable) ob;
                // notify item it has been cached
                cachable.notifyCached(key, CUSTOM_CACHE_REGION);
        }

        // add dependency to source item
        cache.addDependency(key, KEYWORD_CACHE_REGION,
                        KeyGenerator.createKey(dependingPublicationId, dependingItemId));      		
	}
}
