package com.sdl.webapp.common.api.mapping;

/**
 * Field data, which consists of a field value and property data for a field. This is returned by
 * {@code SemanticFieldDataProvider}.
 *
 * The field value is the actual value for the field of an entity. The property data is extra data which is stored in
 * the entity and which is used for semantic markup.
 */
public class FieldData {

    private final Object fieldValue;

    private final String propertyData;

    public FieldData(Object fieldValue, String propertyData) {
        this.fieldValue = fieldValue;
        this.propertyData = propertyData;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public String getPropertyData() {
        return propertyData;
    }
}
