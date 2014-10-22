package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.Entity;

import java.util.Map;

public abstract class AbstractEntity implements Entity {

    @SemanticProperty(ignoreMapping = true)
    private String id;

    @SemanticProperty(ignoreMapping = true)
    private Map<String, String> propertyData;

    @SemanticProperty(ignoreMapping = true)
    private Map<String, String> entityData;

    @SemanticProperty(ignoreMapping = true)
    private String viewName;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, String> getPropertyData() {
        return propertyData;
    }

    public void setPropertyData(Map<String, String> propertyData) {
        this.propertyData = propertyData;
    }

    @Override
    public Map<String, String> getEntityData() {
        return entityData;
    }

    public void setEntityData(Map<String, String> entityData) {
        this.entityData = entityData;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String toString() {
        return "EntityBase{" +
                "id='" + id + '\'' +
                ", viewName='" + viewName + '\'' +
                '}';
    }
}
