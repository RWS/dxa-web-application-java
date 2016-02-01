package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.xpm.ComponentType;

/**
 * <p>ComponentTypeImpl class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class ComponentTypeImpl implements ComponentType {

    @JsonProperty(value = "Schema", required = true)
    private String schemaId;

    @JsonProperty(value = "Template", required = true)
    private String templateId;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaId() {
        return schemaId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    /** {@inheritDoc} */
    @Override
    public String getTemplateId() {
        return templateId;
    }

    /** {@inheritDoc} */
    @Override
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
