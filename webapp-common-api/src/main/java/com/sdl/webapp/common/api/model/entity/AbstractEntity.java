package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.mapping.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.model.Entity;

import java.util.Map;

@SemanticMappingIgnore
public abstract class AbstractEntity implements Entity {

    private String id;

    private Map<String, String> entityData;

    private Map<String, String> propertyData;

    private String viewName;

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

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
