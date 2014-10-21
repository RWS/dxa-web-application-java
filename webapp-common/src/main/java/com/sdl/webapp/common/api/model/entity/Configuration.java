package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.HashMap;
import java.util.Map;

public class Configuration extends EntityBase {

    @SemanticProperty("_all")
    private Map<String, String> settings = new HashMap<>();

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
