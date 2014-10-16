package org.dd4t.core.services.impl;

import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.services.LabelService;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Provides labels for given keys and potentially also Publication contexts. This abstract base class implements the
 * logic to retrieve labels for given keys and Publications, but it does not implement the loading of the labels Map.
 * That logic should be defined in specific implementation classes, potentially interacting with local caches.
 * <p/>
 * This class uses a CacheProvider in order to store loaded label maps temporarily for faster retrieval and a
 * PublicationResolver to provide the current Publication context in case a specific context is omitted.
 *
 * @author Mihai Cadariu
 * @since 23.06.2014
 */
public abstract class LabelServiceBase implements LabelService {

    private static final Logger LOG = LoggerFactory.getLogger(LabelServiceBase.class);
    @Autowired
    protected PublicationResolver publicationResolver;
    @Autowired
    protected CacheProvider cacheProvider;

    /**
     * Return a label for the given key in the current Publication context, which is defined by the given
     * PublicationResolver.
     *
     * @param key String representing the label key to return
     * @return String representing the label value corresponding to the key
     * @throws IOException if something went wrong with loading the values from Tridion
     */
    @Override
    public String getLabel(String key) throws IOException {
        return getLabel(key, publicationResolver.getPublicationId());
    }

    /**
     * Return a label for the given key in the given Publication context. If the label is not fonud, the key is returned.
     *
     * @param key           String representing the label key to return
     * @param publicationId int representing the context Publication to load the label for
     * @return String representing the label value corresponding to the key
     * @throws IOException if something went wrong with loading the values from Tridion
     */
    @Override
    public String getLabel(String key, int publicationId) throws IOException {
        String mapKey = getMapKey(publicationId);
        CacheElement<Map<String, String>> cacheElement = cacheProvider.loadFromLocalCache(mapKey);
        Map<String, String> labelMap;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    LOG.debug("Could not find label map for publicationId: {} in cache", publicationId);
                    labelMap = load(publicationId);
                } else {
                    labelMap = cacheElement.getPayload();
                }
            }
        } else {
            labelMap = cacheElement.getPayload();
        }

        if (labelMap == null) {
            return key;
        }

        String result = labelMap.get(key);
        return result == null ? key : result;
    }

    @Override
    public String getCategoryKey(String category) {
        if (TCMURI.isValid(category) || category.startsWith(CATEGORY_PREFIX)) {
            return category;
        }

        return CATEGORY_PREFIX + category;
    }

    @Override
    public String getSchemaKey(String schema) {
        if (TCMURI.isValid(schema) || schema.startsWith(SCHEMA_PREFIX)) {
            return schema;
        }

        return SCHEMA_PREFIX + schema;
    }

    @Override
    public String getViewKey(String view) {
        if (TCMURI.isValid(view) || view.startsWith(VIEW_PREFIX)) {
            return view;
        }

        return VIEW_PREFIX + view;
    }

    @Override
    public String getCategoryLabel(String category) throws IOException {
        String key = getCategoryKey(category);
        return TCMURI.isValid(key) ? key : getLabel(key);
    }

    @Override
    public String getSchemaLabel(String schema) throws IOException {
        String key = getSchemaKey(schema);
        return TCMURI.isValid(key) ? key : getLabel(key);
    }

    @Override
    public String getViewLabel(String view) throws IOException {
        String key = getViewKey(view);
        return TCMURI.isValid(key) ? key : getLabel(key);
    }

    /**
     * Provides a key for identifying label maps in given publication context in the the local cache.
     *
     * @param publicationId int representing the context Publication to identify the labels map for
     * @return String representing the cache key for a label map in the given Publication context
     */
    protected String getMapKey(int publicationId) {
        return "LabelMap-" + publicationId;
    }
}
