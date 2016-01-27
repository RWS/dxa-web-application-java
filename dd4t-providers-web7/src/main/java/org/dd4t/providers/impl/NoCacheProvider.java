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

package org.dd4t.providers.impl;

import org.dd4t.core.caching.Cachable;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.impl.CacheElementImpl;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PayloadCacheProvider;

import java.util.Collection;

/**
 * TODO: move this to dd4t-caching
 * <p/>
 * CacheProvider which doesn't cache anything.
 *
 * @author rooudsho
 */
public class NoCacheProvider implements PayloadCacheProvider, CacheProvider {

    @Override
    public <T> void storeInItemCache (String key, CacheElement<T> cacheElement) {

    }

    @Override
    public <T> void storeInItemCache (String key, CacheElement<T> cacheElement, int dependingPublicationId, int dependingItemId) {

    }

    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache (String key) {
        return new CacheElementImpl<T>(null, true);
    }

    @Override
    public void storeInCache (String key, Cachable ob, Collection<Cachable> deps) {
    }

    @Override
    public void storeInItemCache (String key, Object ob, int dependingPublicationId, int dependingItemId) {
    }

    @Override
    public void storeInComponentPresentationCache (String key, Object ob, int dependingPublicationId, int dependingCompId, int dependingTemplateId) {
    }

    @Override
    public void storeInKeywordCache (String key, Object ob, int dependingPublicationId, int dependingItemId) {
    }

    @Override
    public Object loadFromLocalCache (String key) {
        return null;
    }
}
