/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    @JsonProperty ("Filename")
    protected String fileName;

    @JsonProperty ("PageTemplate")
    @JsonDeserialize (as = PageTemplateImpl.class)
    protected PageTemplate pageTemplate;

    @JsonProperty ("ComponentPresentations")
    @JsonDeserialize (contentAs = ComponentPresentation.class)
    protected List<ComponentPresentation> componentPresentations;

    @JsonProperty ("StructureGroup")
    @JsonDeserialize (as = StructureGroupImpl.class)
    protected StructureGroup structureGroup;

    @Override
    public PageTemplate getPageTemplate () {
        return pageTemplate;
    }

    @Override
    public void setPageTemplate (PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    @Override
    public List<ComponentPresentation> getComponentPresentations () {
        if (componentPresentations == null) {
            componentPresentations = new ArrayList<>();
        }
        return componentPresentations;
    }

    @Override
    public void setComponentPresentations (List<ComponentPresentation> componentPresentations) {
        this.componentPresentations = componentPresentations;
    }

    /**
     * Get the file name
     */
    @Override
    public String getFileName () {
        return fileName;
    }

    /**
     * Set the file name
     */
    @Override
    public void setFileName (String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the file extension of the page (this is actually determined by the
     * page template but also set here for clarity).
     */
    @Override
    public String getFileExtension () {
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
    @Override
    public void setFileExtension (String fileExtension) {
        if (this.getPageTemplate() != null) {
            this.getPageTemplate().setFileExtension(fileExtension);
        }
    }

    @Override
    public StructureGroup getStructureGroup () {
        return structureGroup;
    }

    @Override
    public void setStructureGroup (StructureGroup structureGroup) {
        this.structureGroup = structureGroup;
    }
}