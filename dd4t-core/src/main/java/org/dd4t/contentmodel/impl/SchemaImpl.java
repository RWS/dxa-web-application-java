package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.Schema;

public class SchemaImpl extends BaseRepositoryLocalItem implements Schema {

    @JsonProperty("RootElementName")
    private String rootElement;

    public String getRootElement() {
        return rootElement;
    }

    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }
}
