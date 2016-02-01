package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Configuration class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class Configuration extends AbstractEntityModel {

    @SemanticProperty("_all")
    private Map<String, String> settings = new HashMap<>();

    /**
     * <p>Getter for the field <code>settings</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getSettings() {
        return settings;
    }

    /**
     * <p>Setter for the field <code>settings</code>.</p>
     *
     * @param settings a {@link java.util.Map} object.
     */
    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Configuration{" +
                "settings=" + settings +
                '}';
    }
}
