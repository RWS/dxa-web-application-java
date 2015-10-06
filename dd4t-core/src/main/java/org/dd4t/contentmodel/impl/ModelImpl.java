package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Model;

import java.util.Map;

public class ModelImpl implements Model {
    private Map<String, FieldSet> extensionData;

    @Override
    public Map<String, FieldSet> getExtensionData() {
        return this.extensionData;
    }

    public void setExtensionData(Map<String, FieldSet> extensionData) {
        this.extensionData = extensionData;
    }
}
