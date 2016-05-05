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

import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.DynamicMappingsRetrieverImpl;
import com.sdl.web.api.dynamic.DynamicMetaRetriever;
import com.sdl.web.api.dynamic.DynamicMetaRetrieverImpl;
import com.sdl.web.api.dynamic.mapping.PublicationMapping;
import com.sdl.web.api.meta.WebPublicationMetaFactory;
import com.sdl.web.api.meta.WebPublicationMetaFactoryImpl;
import com.tridion.broker.StorageException;
import com.tridion.configuration.ConfigurationException;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PublicationMeta;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.util.Constants;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the CIL/CIS class, making use of the new API.
 *
 * @author R. Kempees
 */
public class BrokerPublicationProvider extends AbstractPublicationProvider implements PublicationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerPublicationProvider.class);
    private static final WebPublicationMetaFactory WEB_PUBLICATION_META_FACTORY = new WebPublicationMetaFactoryImpl();
    private static final DynamicMappingsRetriever DYNAMIC_MAPPINGS_RETRIEVER = new DynamicMappingsRetrieverImpl();
    private static final DynamicMetaRetriever DYNAMIC_META_RETRIEVER = new DynamicMetaRetrieverImpl();

    //TODO: Document and add caching

    /**
     * Uses cd_dynamic to resolve publication Ids
     *
     * @param fullUrl the full url, including the host name
     * @return a publiction Id
     */
    @Override
    public int discoverPublicationByBaseUrl (final String fullUrl) {

        PublicationMapping publicationMapping = null;
        try {
            publicationMapping = DYNAMIC_MAPPINGS_RETRIEVER.getPublicationMapping(fullUrl);
        } catch (ConfigurationException e) {
            LOG.error(e.getLocalizedMessage(),e);
        }

        if (publicationMapping != null) {
            return publicationMapping.getPublicationId();
        }

        LOG.info("Could not find publication Id for url: {}", fullUrl);
        return Constants.UNKNOWN_PUBLICATION_ID;
    }

    //TODO: Document
    @Override
    public int discoverPublicationIdByPageUrlPath (final String url) {
        LOG.debug("Discovering Publication id for url: {}", url);
        final String key = getKey(CacheType.DISCOVER_PUBLICATION_URL, url);
        final CacheElement<Integer> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Integer result = Constants.UNKNOWN_PUBLICATION_ID;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    final PageMeta pageMeta = loadPageMetaByConcreteFactory(url);
                    if (pageMeta != null) {
                        result = pageMeta.getPublicationId();
                        LOG.debug("Publication Id for URL: {}, is {}", url, result);
                    } else {
                        LOG.warn("Could not resolve publication Id for URL: {}", url);
                    }

                    cacheElement.setPayload(result);
                    cacheProvider.storeInItemCache(key, cacheElement);
                    LOG.debug("Stored Publication Id with key: {} in cache", key);
                } else {
                    LOG.debug("Fetched a Publication Id with key: {} from cache", key);
                    result = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Fetched Publication Id with key: {} from cache", key);
            result = cacheElement.getPayload();
        }

        return result == null ? Constants.UNKNOWN_PUBLICATION_ID : result;
    }


    // TODO: try to merge with the web8 provider
    @Override
    public int discoverPublicationByImagesUrl (final String fullUrl) {
        LOG.debug("Discovering Publication id for Binary url: {}", fullUrl);
        final String key = getKey(CacheType.DISCOVER_IMAGES_URL, fullUrl);
        final CacheElement<Integer> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Integer result = Constants.UNKNOWN_PUBLICATION_ID;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    final BinaryMeta binaryMeta = loadBinaryMetaByConcreteFactory(fullUrl);
                    if (binaryMeta != null) {
                        result = binaryMeta.getPublicationId();
                        LOG.debug("Publication Id for URL: {}, is {}", fullUrl, result);
                    } else {
                        LOG.warn("Could not resolve publication Id for Binary URL: {}", fullUrl);
                    }

                    cacheElement.setPayload(result);
                    cacheProvider.storeInItemCache(key, cacheElement);
                    LOG.debug("Stored Publication Id with key: {} in cache", key);
                } else {
                    LOG.debug("Fetched a Publication Id with key: {} from cache", key);
                    result = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Fetched Publication Id for Binary with key: {} from cache", key);
            result = cacheElement.getPayload();
        }

        return result == null ? Constants.UNKNOWN_PUBLICATION_ID : result;
    }

    @Override
    protected PublicationMeta loadPublicationMetaByConcreteFactory (final int publicationId) {
        try {
            return WEB_PUBLICATION_META_FACTORY.getMeta(publicationId);
        } catch (StorageException e) {
            LOG.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    protected PageMeta loadPageMetaByConcreteFactory (final String url) {
        return DYNAMIC_META_RETRIEVER.getPageMetaByURL(url);
    }

    @Override
    protected BinaryMeta loadBinaryMetaByConcreteFactory (final String url) {
        return DYNAMIC_META_RETRIEVER.getBinaryMetaByURL(url);
    }
}
