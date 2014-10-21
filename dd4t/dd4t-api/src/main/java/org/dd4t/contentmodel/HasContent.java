package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Interface for items which have (deserialized) content
 *
 * @author bjornl
 */
public interface HasContent {

    /**
     * Get the content as a map of fields where the field name is the key.
     *
     * @return the content as a map of fields.
     */
    public Map<String, Field> getContent();

    /**
     * Set the content
     *
     * @param content
     */
    void setContent(Map<String, Field> content);
}
