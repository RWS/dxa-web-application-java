package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Region support. Or are we just sticking the region on the CP?
 */
public class PageImpl extends BasePage implements GenericPage, HasMetadata {

    @JsonProperty("Filename")
    protected String fileName;

	@JsonProperty("PageTemplate")
    @JsonDeserialize(as = PageTemplateImpl.class)
    protected PageTemplate pageTemplate;

	@JsonProperty("ComponentPresentations")
    @JsonDeserialize(contentAs = ComponentPresentation.class)
    protected List<ComponentPresentation> componentPresentations;

	@JsonProperty("StructureGroup") @JsonDeserialize(as = StructureGroupImpl.class)
    protected StructureGroup structureGroup;

    public PageTemplate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public List<ComponentPresentation> getComponentPresentations() {
        if (componentPresentations == null) {
            componentPresentations = new ArrayList<>();
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