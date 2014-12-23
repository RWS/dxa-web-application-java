package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Container for embedded fields.
 *
 * @author Rogier Oudshoorn
 */
public interface FieldSet {

    /**
     * Get the schema of the component
     *
     * @return the schema
     */
    public Schema getSchema();

    /**
     * Set the schema of the component
     *
     * @param schema
     */
    public void setSchema(Schema schema);

    public Map<String, Field> getContent();

    public void setContent(Map<String, Field> content);

}
