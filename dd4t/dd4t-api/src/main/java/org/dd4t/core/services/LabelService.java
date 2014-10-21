package org.dd4t.core.services;

import java.io.IOException;
import java.util.Map;

/**
 * Provides labels for given keys and potentially also Publication contexts. This generic interface simply defines
 * the methods one can use to load the values into the service and to read values for given keys.
 *
 * @author Mihai Cadariu
 * @since 23.06.2014
 */
public abstract interface LabelService {

    public final String CATEGORY_PREFIX = "CATEGORY-";

    public final String SCHEMA_PREFIX = "SCHEMA-";

    public final String VIEW_PREFIX = "VIEW-";

    /**
     * Loads the labels key, value pairs for a given Publication. It potentially works with the cache as well, storing
     * newly loaded values into the cache.
     *
     * @param publicationId int representing the context Publication to load labels for
     * @return Map with String keys and String values representing the key, value pairs of localized labels for the
     * given Publication
     * @throws IOException if something went wrong with loading the values from Tridion
     */
    public Map<String, String> load(int publicationId) throws IOException;

    /**
     * Return a label for the given key in the current Publication context.
     *
     * @param key String representing the label key to return
     * @return String representing the label value corresponding to the key
     * @throws IOException if something went wrong with loading the values from Tridion
     */
    public String getLabel(String key) throws IOException;

    /**
     * Return a label for the given key in the given Publication context.
     *
     * @param key           String representing the label key to return
     * @param publicationId int representing the context Publication to load the label for
     * @return String representing the label value corresponding to the key
     * @throws IOException if something went wrong with loading the values from Tridion
     */
    public String getLabel(String key, int publicationId) throws IOException;

    public String getCategoryLabel(String category) throws IOException;

    public String getSchemaLabel(String schema) throws IOException;

    public String getViewLabel(String view) throws IOException;

    public String getCategoryKey(String category);

    public String getSchemaKey(String schema);

    public String getViewKey(String view);
}
