package com.sdl.webapp.common.impl.localization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.SiteLocalization;

public class SiteLocalizationImpl implements SiteLocalization {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("IsMaster")
    private boolean master;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
}
