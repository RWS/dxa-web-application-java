package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.*;
import org.dd4t.core.util.DateUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageImpl extends BasePage implements GenericPage, HasMetadata {

    @JsonProperty("Filename")
    protected String fileName;

	@JsonProperty("PageTemplate") @JsonDeserialize(as = PageTemplateImpl.class)
    protected PageTemplate pageTemplate;

	@JsonProperty("ComponentPresentations") @JsonDeserialize(contentAs = ComponentPresentationImpl.class)
    protected List<ComponentPresentation> componentPresentations;

	@JsonProperty("StructureGroup") @JsonDeserialize(as = StructureGroupImpl.class)
    protected StructureGroup structureGroup;

    @JsonProperty("Version")
    protected int version;

    @JsonProperty("LastPublishedDate")
    protected String lastPublishedDateAsString;

    @JsonProperty("RevisionDate")
    protected String revisionDateAsString;

	@JsonProperty("MetadataFields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> metadata;

    @JsonProperty("Categories")
    private List<Category> categories;

	@JsonProperty("Schema") @JsonDeserialize(as=SchemaImpl.class)
	private Schema schema;

    @Override
    public DateTime getLastPublishedDate() {
        if (lastPublishedDateAsString == null || lastPublishedDateAsString.isEmpty()) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(lastPublishedDateAsString);
    }

    @Override
    public void setLastPublishedDate(DateTime date) {
        this.lastPublishedDateAsString = DateUtils.convertDateToString(date);
    }

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

    public int getVersion() {

        return version;
    }

    public void setVersion(int version) {

        this.version = version;
    }
	public Schema getSchema () {
		return schema;
	}

	public void setSchema (final Schema schema) {
		this.schema = schema;
	}
    /**
     * Get the metadata as a map of fields
     */
    public Map<String, Field> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<String, Field>();
        }
        return metadata;
    }

    /**
     * Set the metadata
     */
    public void setMetadata(Map<String, Field> metadata) {
        this.metadata = metadata;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public PageTemplate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public List<ComponentPresentation> getComponentPresentations() {
        if (componentPresentations == null) {
            componentPresentations = new ArrayList<ComponentPresentation>();
        }
        return componentPresentations;
    }

    public void setComponentPresentations(List<ComponentPresentation> componentPresentations) {
        this.componentPresentations = componentPresentations;
    }

    /**
     * Get the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the file extension of the page (this is actually determined by the
     * page template but also set here for clarity).
     */
    public String getFileExtension() {
        if (this.getPageTemplate() != null) {
            return this.getPageTemplate().getFileExtension();
        } else {
            return "";
        }
    }

    /**
     * Set the file extension. It sets the file extension on the page template
     * because that is were the extension is determined.
     */
    public void setFileExtension(String fileExtension) {
        if (this.getPageTemplate() != null) {
            this.getPageTemplate().setFileExtension(fileExtension);
        }
    }

    public StructureGroup getStructureGroup() {
        return structureGroup;
    }

    public void setStructureGroup(StructureGroup structureGroup) {
        this.structureGroup = structureGroup;
    }


}