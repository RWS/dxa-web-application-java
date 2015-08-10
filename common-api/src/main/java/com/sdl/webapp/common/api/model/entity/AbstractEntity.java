package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.mapping.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;

import java.util.Map;

/**
 * Abstract superclass for implementations of {@code Entity}.
 */
@SemanticMappingIgnore
public abstract class AbstractEntity implements Entity {

    @JsonProperty("Id")
    private String id;

    @JsonIgnore
    private Map<String, String> entityData;

    @JsonIgnore
    private Map<String, String> propertyData;

    @JsonIgnore
    private MvcData mvcData;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, String> getEntityData() {
        return entityData;
    }

    public void setEntityData(Map<String, String> entityData) {
        this.entityData = ImmutableMap.copyOf(entityData);
    }

    @Override
    public Map<String, String> getPropertyData() {
        return propertyData;
    }

    public void setPropertyData(Map<String, String> propertyData) {
        this.propertyData = ImmutableMap.copyOf(propertyData);
    }

    @Override
    public MvcData getMvcData() {
        return mvcData;
    }

    public void setMvcData(MvcData mvcData) {
        this.mvcData = mvcData;
    }
}
