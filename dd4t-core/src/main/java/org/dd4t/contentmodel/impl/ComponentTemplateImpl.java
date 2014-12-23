package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.util.DateUtils;

import org.joda.time.DateTime;
import java.util.Map;

public class ComponentTemplateImpl extends BaseRepositoryLocalItem implements ComponentTemplate {

    @JsonProperty("RevisionDate")
    protected String revisionDateAsString;

    @JsonProperty("OutputFormat")
    private String outputFormat;

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
        this.revisionDateAsString = date.toString();
    }

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
