package com.sdl.webapp.common.api.mapping.semantic;

import lombok.Getter;

/**
 * Field data, which consists of a field value and property data for a field. This is returned by
 * {@code SemanticFieldDataProvider}.
 * <p>
 * The field value is the actual value for the field of an entity. The property data is extra data which is stored in
 * the entity and which is used for semantic markup.
 */
@Getter
public class FieldData {

    private final Object fieldValue;

    private final String propertyData;

    /**
     * <p>Constructor for FieldData.</p>
     *
     * @param fieldValue   a {@link java.lang.Object} object.
     * @param propertyData a {@link java.lang.String} object.
     */
    public FieldData(Object fieldValue, String propertyData) {
        this.fieldValue = fieldValue;
        this.propertyData = propertyData;
    }
}
