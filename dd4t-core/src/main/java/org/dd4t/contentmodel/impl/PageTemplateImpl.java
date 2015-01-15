package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.PageTemplate;

public class PageTemplateImpl extends BaseRepositoryLocalItem implements PageTemplate, HasMetadata {

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
}
