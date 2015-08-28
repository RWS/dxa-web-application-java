package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentType {

    @JsonProperty(value = "Schema", required = true)
    private String schemaId;

    @JsonProperty(value = "Template", required = true)
    private String templateId;

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
