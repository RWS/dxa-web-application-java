package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Container for embedded fields.
 *
 * @author <a href="rogier.oudshoorn@">Rogier Oudshoorn</a>
 * @version $Revision: 6 $
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
