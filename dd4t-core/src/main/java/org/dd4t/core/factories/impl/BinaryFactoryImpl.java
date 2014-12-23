package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.BinaryFactory;
import org.dd4t.core.filters.Filter;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BinaryProvider;
import org.dd4t.providers.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * @author R.S. Kempees
 */
public class BinaryFactoryImpl implements BinaryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryFactoryImpl.class);
    // Singleton implementation
    private static final BinaryFactoryImpl INSTANCE = new BinaryFactoryImpl();

    @Autowired
    private CacheProvider cacheProvider;
    @Autowired
    private BinaryProvider binaryProvider;

    private BinaryFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static BinaryFactoryImpl getInstance() {
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
    public Binary getBinaryByURI(final String tcmUri) throws FactoryException {
        LOG.debug("Enter getBinaryByURI with uri: {}", tcmUri);

        CacheElement<Binary> cacheElement = cacheProvider.loadFromLocalCache(tcmUri);
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
                    } catch (ItemNotFoundException |SerializationException | ParseException e) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(tcmUri, cacheElement);
                        throw new FactoryException(e);
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
     *
     * Null values should be handled on the controller level
     *
     * @param url           String representing the path part of the binary URL
     * @param publicationId int representing the Publication context id
     * @return Binary object containing the metadata and raw byte array content
     * @throws org.dd4t.core.exceptions.FactoryException
     */
    @Override
    public Binary getBinaryByURL(final String url, final int publicationId) throws FactoryException {
        // TODO: check if in File cache here. This means it needs to be moved away from the binary controller
        LOG.debug("Enter getBinaryByURL with url: {} and publicationId: {}", url, publicationId);

        String key = getCacheKey(url, publicationId);
        CacheElement<Binary> cacheElement = cacheProvider.loadFromLocalCache(key);
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
                    } catch (ItemNotFoundException |SerializationException |ParseException e) {
                        throw new FactoryException(e);
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
    public List<Filter> getFilters() {
        // TODO: Not implemented yet
        return new ArrayList<>();
    }

    @Override
    public void setFilters(final List<Filter> filters) {
        // TODO: Not implemented yet
    }

    @Override
    public void setCacheProvider(final CacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }

    public void setBinaryProvider(final BinaryProvider binaryProvider) {
        this.binaryProvider = binaryProvider;
    }

    private String getCacheKey(String url, int publicationId) {
        return String.format("B-%s-%d", url, publicationId);
    }
}
