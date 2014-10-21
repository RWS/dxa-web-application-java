package org.dd4t.contentmodel;

import java.util.List;

public interface Field {

    /**
     * Get the values of the field.
     *
     * @return a list of objects, where the type is depending of the field type.
     * Never returns null.
     */
    public List<Object> getValues();

    /**
     * Get the name of the field.
     *
     * @return the name of the field
     */
    public String getName();

    /**
     * Set the name of the field
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Get the xPath of the field (used for SiteEdit)
     *
     * @return the xPath of the field
     */
    public String getXPath();

    /**
     * Set the xPath of the field (used for SiteEdit)
     *
     * @param xPath
     */
    public void setXPath(String xPath);

    /**
     * Get the field type
     *
     * @return the field type
     */
    public FieldType getFieldType();

    /**
     * Set the field type
     *
     * @param fieldType
     */
    public void setFieldType(FieldType fieldType);
}