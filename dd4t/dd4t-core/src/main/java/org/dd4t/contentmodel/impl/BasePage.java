package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.Schema;

public abstract class BasePage extends BaseRepositoryLocalItem {

    @JsonProperty("Schema")
    protected Schema schema;

    /**
     * Get the schema
     *
     * @return the schema
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Set the schema
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }
}
