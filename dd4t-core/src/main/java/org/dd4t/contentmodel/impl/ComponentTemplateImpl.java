package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;

import java.util.Map;

public class ComponentTemplateImpl extends BaseRepositoryLocalItem implements ComponentTemplate {

    @JsonProperty("OutputFormat")
    private String outputFormat;

	@JsonProperty("MetadataFields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> metadata;

    @Override
    public Map<String, Field> getMetadata() {
        return this.metadata;
    }

    @Override
    public void setMetadata(Map<String, Field> metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the output format
     *
     * @return
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * Set the output format
     *
     * @param outputFormat
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
