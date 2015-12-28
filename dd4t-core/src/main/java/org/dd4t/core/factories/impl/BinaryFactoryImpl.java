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

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.BinaryFactory;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BinaryProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 *
 */
public class BinaryFactoryImpl extends BaseFactory implements BinaryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryFactoryImpl.class);
    // Singleton implementation
    private static final BinaryFactoryImpl INSTANCE = new BinaryFactoryImpl();

    private PayloadCacheProvider cacheProvider;
    private BinaryProvider binaryProvider;

    protected BinaryFactoryImpl () {
        LOG.debug("Create new instance");
    }

    public static BinaryFactoryImpl getInstance () {
        return INSTANCE;
    }

    /**
     * Get a binary by the tcmUri.
     * Null values should be handled on the controller level
     *
     * @param tcmUri String representing the TCMURI of the binary to retrieve
     * @return Binary object containing the metadata and raw byte array content
     * @throws org.dd4t.core.exceptions.FactoryException
     */
    @Override
    public Binary getBinaryByURI (final String tcmUri) throws FactoryException {
        LOG.debug("Enter getBinaryByURI with uri: {}", tcmUri);

        CacheElement<Binary> cacheElement = cacheProvider.loadPayloadFromLocalCache(tcmUri);
        Binary binary;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    try {
                        binary = binaryProvider.getBinaryByURI(tcmUri);
                        cacheElement.setPayload(binary);
                        TCMURI binaryURI = new TCMURI(tcmUri);
                        cacheProvider.storeInItemCache(tcmUri, cacheElement, binaryURI.getPublicationId(), binaryURI.getItemId());
                        LOG.debug("Added binary with uri: {} to cache", tcmUri);
                    } catch (ParseException e) {
                        cacheElement.setPayload(null);
                        cacheElement.setExpired(true);
                        cacheProvider.storeInItemCache(tcmUri, cacheElement);
                        throw new ItemNotFoundException(e);
                    }
                } else {
                    LOG.debug("Return a binary with uri: {} from cache", tcmUri);
                    binary = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return binary with uri: {} from cache", tcmUri);
            binary = cacheElement.getPayload();
        }

        return binary;
    }

    /**
     * Get a binary by the url and publicationId.
     * <p/>
     * Null values should be handled on the controller level
     *
     * @param url           String representing the path part of the binary URL
     * @param publicationId int representing the Publication context id
     * @return Binary object containing the metadata and raw byte array content
     * @throws org.dd4t.core.exceptions.FactoryException
     */
    @Override
    public Binary getBinaryByURL (final String url, final int publicationId) throws FactoryException {
        LOG.debug("Enter getBinaryByURL with url: {} and publicationId: {}", url, publicationId);

        String key = getCacheKey(url, publicationId);
        CacheElement<Binary> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Binary binary;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    try {
                        binary = binaryProvider.getBinaryByURL(url, publicationId);
                        cacheElement.setPayload(binary);

                        TCMURI tcmUri = new TCMURI(binary.getId());
                        cacheProvider.storeInItemCache(key, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
                        LOG.debug("Added binary with url: {} to cache", url);
                    } catch (ParseException e) {
                        throw new ItemNotFoundException(e);
                    }
                } else {
                    LOG.debug("Return a binary with url: {} from cache", url);
                    binary = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return binary with url: {} from cache", url);
            binary = cacheElement.getPayload();
        }

        return binary;
    }

    @Override
    public void setCacheProvider (final PayloadCacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }

    public void setBinaryProvider (final BinaryProvider binaryProvider) {
        this.binaryProvider = binaryProvider;
    }

    private String getCacheKey (String url, int publicationId) {
        return String.format("B-%s-%d", url, publicationId);
    }
}
