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
package org.dd4t.core.caching;

import java.util.Collection;

import com.tridion.dcp.ComponentPresentation;
import com.tridion.storage.ComponentMeta;
import com.tridion.storage.ItemMeta;
import com.tridion.storage.PageMeta;
import com.tridion.taxonomies.Keyword;

/**
 * Interface is deprecated, use CacheProvider (from org.dd4t.providers) instead.
 * 
 * @author rooudsho
 *
 */
@Deprecated
public interface CacheAgent {
	public Object loadFromLocalCache(String key);
	
	/**
	 * Store given item in the cache with a reference to supplied Tridion Component Presentation.
	 */
	public void storeInCache(String key, Object ob, ComponentPresentation compPres);

	/**
	 * Store given item in the cache with a reference to supplied Tridion Component metadata. Deprecated in favour
         * of the overload based on ItemMeta (type-specific keys is gone!).
	 */
	@Deprecated
	public void storeInCache(String key, Object ob, ComponentMeta componentMeta);

	/**
	 * Store given item in the cache with a reference to supplied Tridion PageMeta. Deprecated in favour
	 * of the overload based on ItemMeta (type-specific keys is gone!).
	 */
	@Deprecated
	public void storeInCache(String key, Object ob, PageMeta pageMeta);
	
        /**
         * Store given item with a reference to supplied Tridion Storage Meta.
         * 
         * @param key The cachekey
         * @param ob The cached object
         * @param item The tridion item
         */
        public void storeInCache(String key, Object ob, ItemMeta item);
	
	/**
	 * Store given item in the cache with a reference to given collection of also cached items
	 */
	public void storeInCache(String key, Cachable ob, Collection<Cachable> deps);

	/**
	 * Store given item (described by publicationId and itemId) in the cache.
	 * 
	 * @param key
	 * @param ob
	 * @param publicationId
	 * @param itemId
	 */
	public void storeInCache(String key, Object ob, Keyword keyword);
}
