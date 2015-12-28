/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.providers;

import java.util.Collection;

import org.dd4t.core.caching.Cachable;

public interface CacheProvider {
    /**
     * Loads given object from the cache
     *
     * @param key
     * @return
     */
    Object loadFromLocalCache (String key);

    /**
     * Store given item in the cache with a reference to given collection of also cached items
     */
    void storeInCache (String key, Cachable ob, Collection<Cachable> deps);

    /**
     * Store given item in the cache with a reference to supplied Tridion Item.
     */
    void storeInItemCache (String key, Object ob, int dependingPublicationId, int dependingItemId);

    /**
     * Store given item in the cache with a reference to supplied Tridion Component Presentation.
     */
    void storeInComponentPresentationCache (String key, Object ob, int dependingPublicationId, int dependingCompId, int dependingTemplateId);

    /**
     * Store given item in the cache with a reference to supplied Tridion Keyword.
     */
    void storeInKeywordCache (String key, Object ob, int dependingPublicationId, int dependingItemId);
}
