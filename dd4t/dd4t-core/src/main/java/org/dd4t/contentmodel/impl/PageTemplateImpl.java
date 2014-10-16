package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.core.util.DateUtils;

import org.joda.time.DateTime;
import java.util.Map;

public class PageTemplateImpl extends BaseRepositoryLocalItem implements PageTemplate, HasMetadata {

    @JsonProperty("LastPublishDate")
    protected String lastPublishedDateAsString;

    @JsonProperty("RevisionDate")
    protected String revisionDateAsString;

    @JsonProperty("FileExtension")
    private String fileExtension;

	@JsonProperty("MetadataFields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> metadata;

    @Override
    public DateTime getRevisionDate() {
        if (revisionDateAsString == null || revisionDateAsString.isEmpty()) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(revisionDateAsString);
    }

    @Override
    public void setRevisionDate(DateTime date) {
        this.revisionDateAsString = DateUtils.convertDateToString(date);
    }

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
