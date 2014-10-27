package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.model.Entity;

import java.util.Map;

@SemanticMappingIgnore
public abstract class AbstractEntity implements Entity {

    private String id;

    private Map<String, String> propertyData;

    private Map<String, String> entityData;

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
