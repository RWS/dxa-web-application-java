package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.PageTemplate;

import java.util.Map;

public class PageTemplateImpl extends BaseRepositoryLocalItem implements PageTemplate, HasMetadata {

    @JsonProperty("LastPublishDate")
    protected String lastPublishedDateAsString;

    @JsonProperty("MetadataFields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> metadata;


    @JsonProperty("FileExtension")
    private String fileExtension;



    /**
     * Get the file extension
     */
    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Set the file extension
     */
    @Override
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Get the metadata as a map of fields
     */
    public Map<String, Field> getMetadata() {
        return metadata;
    }

    /**
     * Set the metadata
     */
    public void setMetadata(Map<String, Field> metadata) {
        this.metadata = metadata;
    }
}
