package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Model;

import java.util.Map;

public abstract class AbstractModel implements Model {

    @JsonProperty(value = "ExtensionData", required = false)
    @JsonDeserialize(contentAs = FieldSetImpl.class)
    private Map<String, FieldSet> extensionData;

    @Override
    public Map<String, FieldSet> getExtensionData() {
        return this.extensionData;
    }

    public void setExtensionData(Map<String, FieldSet> extensionData) {
        this.extensionData = extensionData;
    }
}
