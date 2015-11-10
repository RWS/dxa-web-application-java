package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.util.HashMap;
import java.util.Map;

public class Configuration extends AbstractEntityModel {

    @SemanticProperty("_all")
    private Map<String, String> settings = new HashMap<>();

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "settings=" + settings +
                '}';
    }
}
