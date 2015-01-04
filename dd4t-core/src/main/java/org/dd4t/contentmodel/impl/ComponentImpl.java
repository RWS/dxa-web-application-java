package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.*;

import java.util.HashMap;
import java.util.Map;

public class ComponentImpl extends BaseComponent implements Component, HasContent, HasMetadata, HasMultimedia {

	@JsonProperty("ComponentType") @JsonDeserialize(as = ComponentImpl.ComponentType.class)
    protected ComponentType componentType;

	@JsonProperty("Fields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> content;

	@JsonProperty("Multimedia") @JsonDeserialize(as = MultimediaImpl.class)
    private Multimedia multimedia;

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    public Map<String, Field> getContent() {
        if (content == null) {
            content = new HashMap<String, Field>();
        }
        return content;
    }

    /**
     * Set the content
     */
    public void setContent(Map<String, Field> content) {
        this.content = content;
    }

    /**
     * Get the component type
     *
     * @return the component type
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * Set the component type
     *
     * @param componentType
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * Get the multimedia object
     *
     * @return the multimedia object
     */
    @Override
    public Multimedia getMultimedia() {
        return multimedia;
    }

    /**
     * Set the multimedia object
     */
    @Override
    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }
}