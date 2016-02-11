package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.SiteLocalization;

/**
 * <p>SiteLocalizationImpl class.</p>
 */
public class SiteLocalizationImpl implements SiteLocalization {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("IsMaster")
    private boolean master;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * <p>Setter for the field <code>path</code>.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /** {@inheritDoc} */
    @Override
    public String getLanguage() {
        return language;
    }

    /**
     * <p>Setter for the field <code>language</code>.</p>
     *
     * @param language a {@link java.lang.String} object.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMaster() {
        return master;
    }

    /**
     * <p>Setter for the field <code>master</code>.</p>
     *
     * @param master a boolean.
     */
    public void setMaster(boolean master) {
        this.master = master;
    }
}
