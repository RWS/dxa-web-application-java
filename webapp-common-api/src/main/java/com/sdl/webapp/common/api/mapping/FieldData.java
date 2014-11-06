package com.sdl.webapp.common.api.mapping;

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
