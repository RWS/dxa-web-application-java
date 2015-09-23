package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.xpm.ComponentType;

public class ComponentTypeImpl implements ComponentType {

    @JsonProperty(value = "Schema", required = true)
    private String schemaId;

    @JsonProperty(value = "Template", required = true)
    private String templateId;

    @Override
    public String getSchemaId() {
        return schemaId;
    }

    @Override
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    @Override
    public String getTemplateId() {
        return templateId;
    }

    @Override
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
