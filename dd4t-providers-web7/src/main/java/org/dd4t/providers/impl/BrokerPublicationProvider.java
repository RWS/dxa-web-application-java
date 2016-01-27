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

import com.tridion.broker.StorageException;
import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.contentmodel.PublicationDescriptor;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class BrokerPublicationProvider extends BaseBrokerProvider implements PublicationProvider {
    private static final DynamicMetaRetriever DYNAMIC_META_RETRIEVER = new DynamicMetaRetriever();
    private static final PublicationMetaFactory PUBLICATION_META_FACTORY = new PublicationMetaFactory();
    private static final Logger LOG = LoggerFactory.getLogger(BrokerPublicationProvider.class);

    private Class publicationDescriptor;

    //TODO: Document
    @Override
    public int discoverPublicationIdByPageUrlPath (final String url) {
        LOG.debug("Discovering Publication id for url: {}", url);
        final String key = getKey(CacheType.DISCOVER_PUBLICATION_URL, url);
        final CacheElement<Integer> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Integer result = -1;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    final com.tridion.meta.PageMeta pageMeta = DYNAMIC_META_RETRIEVER.getPageMetaByURL(url);
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

        return result == null ? -1 : result;
    }

    //TODO: Document and add caching

    /**
     * Uses cd_dynamic to resolve publication Ids
     *
     * @param fullUrl the full url, including the host name
     * @return a publiction Id
     */
    @Override
    public int discoverPublicationByBaseUrl (final String fullUrl) {

        PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(fullUrl);

        if (publicationMapping != null) {
            return publicationMapping.getPublicationId();
        }

        LOG.info("Could not find publication Id for url: {}", fullUrl);
        return -1;
    }

    @Override
    public String discoverPublicationUrl (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getPublicationUrl();
    }

    @Override
    public String discoverPublicationPath (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getPublicationPath();
    }

    @Override
    public String discoverImagesUrl (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getMultimediaUrl();
    }

    @Override
    public String discoverImagesPath (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getMultimediaPath();
    }

    @Override
    public String discoverPublicationTitle (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getTitle();
    }

    @Override
    public String discoverPublicationKey (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getKey();
    }

    /**
     * For use in remote scenarios
     *
     * @param publicationId the publication Id
     * @return a Publication descriptor
     */
    @Override
    public PublicationDescriptor getPublicationDescriptor (final int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }

        try {
            final PublicationDescriptor concretePublicationDescriptor = (PublicationDescriptor) publicationDescriptor.newInstance();
            concretePublicationDescriptor.setId(publicationMeta.getId());
            concretePublicationDescriptor.setKey(publicationMeta.getKey());
            concretePublicationDescriptor.setPublicationUrl(publicationMeta.getPublicationUrl());
            concretePublicationDescriptor.setPublicationPath(publicationMeta.getPublicationPath());
            concretePublicationDescriptor.setMultimediaUrl(publicationMeta.getMultimediaUrl());
            concretePublicationDescriptor.setMultimediaPath(publicationMeta.getMultimediaPath());
            concretePublicationDescriptor.setTitle(publicationMeta.getTitle());
            return concretePublicationDescriptor;
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }

        return null;
    }


    private PublicationMeta getPublicationMeta (final int publicationId) {
        final String key = getKey(CacheType.PUBLICATION_META, Integer.toString(publicationId));
        final CacheElement<PublicationMeta> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);

        PublicationMeta publicationMeta = null;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    try {
                        publicationMeta = PUBLICATION_META_FACTORY.getMeta(publicationId);
                        cacheElement.setPayload(publicationMeta);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        LOG.debug("Stored Publication Meta with key: {} in cache", key);
                    } catch (StorageException e) {
                        LOG.error(e.getLocalizedMessage(), e);
                    }
                } else {
                    LOG.debug("Fetched a Publication Meta with key: {} from cache", key);
                    publicationMeta = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Fetched a Publication Meta with key: {} from cache", key);
            publicationMeta = cacheElement.getPayload();
        }

        if (publicationMeta == null) {
            LOG.error("Could not find Publication Meta for publication id: {}", publicationId);
            return null;
        }

        return publicationMeta;
    }

    public void setPublicationDescriptor (final Class publicationDescriptor) {
        this.publicationDescriptor = publicationDescriptor;
    }

    public Class getPublicationDescriptor () {
        return publicationDescriptor;
    }
}
