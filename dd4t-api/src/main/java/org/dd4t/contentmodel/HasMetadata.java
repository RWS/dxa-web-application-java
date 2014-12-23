package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Interface for items which have metadata
 *
 * @author bjornl
 */
public interface HasMetadata {

    /**
     * Get the metadata as a map of fields where the field name is the key.
     *
     * @return the content as a map of fields.
     */
    public Map<String, Field> getMetadata();

    /**
     * Set the metadata
     *
     * @param metadata
     */
    public void setMetadata(Map<String, Field> metadata);
}
