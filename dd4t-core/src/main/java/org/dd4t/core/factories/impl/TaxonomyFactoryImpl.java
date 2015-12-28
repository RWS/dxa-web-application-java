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

package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.KeywordImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.TaxonomyFactory;
import org.dd4t.core.serializers.impl.SerializerFactory;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.PayloadCacheProvider;
import org.dd4t.providers.TaxonomyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

/**
 * Service class that wraps around a Taxonomy provider and provides cache functionality. It also offers utility methods
 * for retrieving an entire Taxonomy (as Keyword) or individual Keywords identified by their name, key, desciption or
 * TCMURI.
 *
 * @author Mihai Cadariu
 */
public class TaxonomyFactoryImpl extends BaseFactory implements TaxonomyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TaxonomyFactoryImpl.class);
    private static final TaxonomyFactoryImpl INSTANCE = new TaxonomyFactoryImpl();
    private static final String NOT_FOUND_ERROR_MESSAGE = "Failed to read taxonomy {} from provider";

    private TaxonomyProvider taxonomyProvider;

    private TaxonomyFactoryImpl () {
        LOG.debug("Create new instance");
    }

    public static TaxonomyFactoryImpl getInstance () {
        return INSTANCE;
    }

    /**
     * Returns the root Keyword of Taxonomy by reading the specified taxonomy from the local cache or from the
     * Taxonomy provider, if not found in cache.
     *
     * @param taxonomyURI String representing the taxonomy TCMURI to read
     * @return Keyword the root node of the Taxonomy
     * @throws IOException if said taxonomy cannot be found or an error occurred while fetching it
     */
    @Override
    public Keyword getTaxonomy (String taxonomyURI) throws IOException {
        LOG.debug("Enter getTaxonomy with uri: {}", taxonomyURI);

        CacheElement<Keyword> cacheElement = cacheProvider.loadPayloadFromLocalCache(taxonomyURI);
        Keyword taxonomy;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    try {
                        String taxonomySource = taxonomyProvider.getTaxonomyByURI(taxonomyURI, true);
                        if (taxonomySource == null || taxonomySource.length() == 0) {
                            cacheElement.setPayload(null);
                            cacheProvider.storeInItemCache(taxonomyURI, cacheElement);
                            throw new ItemNotFoundException(String.format("Taxonomy with uri: %s not found.", taxonomyURI));
                        }

                        taxonomy = deserialize(taxonomySource, KeywordImpl.class);
                        cacheElement.setPayload(taxonomy);

                        TCMURI tcmUri = new TCMURI(taxonomyURI);
                        cacheProvider.storeInItemCache(taxonomyURI, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
                        LOG.debug("Added taxonomy with uri: {} to cache", taxonomyURI);
                    } catch (ItemNotFoundException | ParseException | SerializationException e) {
                        LOG.error(NOT_FOUND_ERROR_MESSAGE, taxonomyURI, e);
                        throw new IOException(e);
                    }
                } else {
                    LOG.debug("Return taxonomy with uri: {} from cache", taxonomyURI);
                    taxonomy = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return taxonomy with uri: {} from cache", taxonomyURI);
            taxonomy = cacheElement.getPayload();
        }

        if (taxonomy == null) {
            throw new IOException("Failed to read taxonomy " + taxonomyURI + " from provider");
        }

        return taxonomy;
    }

    private static Keyword deserialize (final String taxonomySource, final Class<KeywordImpl> keywordClass) throws SerializationException {
        return SerializerFactory.deserialize(taxonomySource, keywordClass);
    }

    /**
     * Returns the root Keyword of Taxonomy by reading the specified taxonomy from the local cache or from the
     * Taxonomy provider, if not found in cache.
     * <p/>
     * The returned classified items are filtered to only Components based on the given Schema URI.
     *
     * @param taxonomyURI String representing the taxonomy TCMURI to read
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return Keyword the root node of the Taxonomy
     * @throws IOException if said taxonomy cannot be found or an error occurred while fetching it
     */
    @Override
    public Keyword getTaxonomyFilterBySchema (String taxonomyURI, String schemaURI) throws IOException {
        LOG.debug("Enter getTaxonomyFilterBySchema with uri: {} and schema: {}", taxonomyURI, schemaURI);

        String key = taxonomyURI + schemaURI;
        CacheElement<Keyword> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Keyword taxonomy;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    try {
                        String taxonomySource = taxonomyProvider.getTaxonomyFilterBySchema(taxonomyURI, schemaURI);
                        if (taxonomySource == null || taxonomySource.length() == 0) {
                            cacheElement.setPayload(null);
                            cacheProvider.storeInItemCache(taxonomyURI, cacheElement);
                            throw new ItemNotFoundException("Taxonomy with uri: " + taxonomyURI + " not found.");
                        }

                        taxonomy = deserialize(taxonomySource, KeywordImpl.class);
                        cacheElement.setPayload(taxonomy);

                        TCMURI tcmUri = new TCMURI(taxonomyURI);
                        cacheProvider.storeInItemCache(key, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
                        LOG.debug("Added taxonomy with uri: {} and schema: {} to cache", taxonomyURI, schemaURI);
                    } catch (ItemNotFoundException e) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(taxonomyURI, cacheElement);
                        LOG.error(e.getLocalizedMessage(), e);
                        throw new IOException("Taxonomy with uri: " + taxonomyURI + " not found.");
                    } catch (ParseException | SerializationException e) {
                        LOG.error(NOT_FOUND_ERROR_MESSAGE, taxonomyURI, e);
                        throw new IOException(e);
                    }
                } else {
                    LOG.debug("Return taxonomy with uri: {} and schema: {} from cache", taxonomyURI, schemaURI);
                    taxonomy = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return taxonomy with uri: {} and schema: {} from cache", taxonomyURI, schemaURI);
            taxonomy = cacheElement.getPayload();
        }

        if (taxonomy == null) {
            throw new IOException(NOT_FOUND_ERROR_MESSAGE);
        }

        return taxonomy;
    }

    @Override
    public void setCacheProvider (PayloadCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    public void setTaxonomyProvider (TaxonomyProvider taxonomyProvider) {
        this.taxonomyProvider = taxonomyProvider;
    }
}
