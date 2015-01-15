package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Schema;

public class BaseComponent extends BaseRepositoryLocalItem {

    @JsonProperty("Schema")
    @JsonDeserialize(as = SchemaImpl.class)
    private Schema schema;

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}